# Extraheringsstrategi

Detta dokument sammanfattar diskussionen om sporadiska CRC-fel vid
RAR-extrahering och möjliga alternativ till Junrar.

## Bakgrund

CopyExtractor får allt oftare följande fel nära slutet av en extrahering:

```text
com.github.junrar.exception.CrcErrorException
```

Ett CRC-fel betyder att extraherade data inte stämmer med kontrollsumman i
arkivet. Det kan bero på en ofullständig eller skadad RAR-fil, men en förändring
i Junrar kan också ha påverkat beteendet. Projektet uppgraderade Junrar från
7.5.7 till 7.5.10 den 27 april 2026. I arbetet med den nya
extraheringsabstraktionen uppgraderades fallbacken vidare till Junrar 8.0.0.

Den nuvarande kontrollen av om källmaterialet är färdigskrivet använder
katalogens `lastModified` och en väntetid på tio sekunder. Det garanterar inte
att en redan skapad RAR-fil har slutat växa. Innan extraheraren byts bör därför
programmet även kontrollera att arkivfilernas storlek och ändringstid är stabila
under en konfigurerbar tidsperiod.

## Önskad prioriteringsordning

1. Detektera operativsystem och processorarkitektur. Använd en extraherare som
   levereras med CopyExtractor om en kompatibel variant finns.
2. Leta efter en installerad extraherare, i första hand `unrar` och därefter
   `7z`. En explicit konfigurerad sökväg bör prioriteras före `PATH`.
3. Använd Junrar som ren Java-fallback om ingen annan extraherare är tillgänglig.

RARLAB:s officiella UnRAR ska skiljas från det äldre Linux-programmet
`unrar-free` 0.0.2, som inte är kommandoradskompatibelt. Befintliga målfiler ska
alltid lämnas orörda (`-o-` för UnRAR och `-aos` för 7-Zip).

Fallback ska användas när en extraherare saknas, inte kan startas eller inte
stöder arkivet. Ett rapporterat CRC-fel ska däremot inte automatiskt leda till
ett nytt försök med Junrar, eftersom arkivet då sannolikt är ofullständigt eller
skadat.

Externa program ska startas med `ProcessBuilder` och separata argument, utan ett
mellanliggande kommandoskal. Arkivet bör testas före extrahering, exempelvis med
`unrar t` eller `7z t`. Extrahering bör ske till en temporär katalog som flyttas
till slutdestinationen först efter en lyckad exitkod.

## SevenZipJBinding

Möjliga Maven-beroenden:

```xml
<dependency>
    <groupId>net.sf.sevenzipjbinding</groupId>
    <artifactId>sevenzipjbinding</artifactId>
    <version>16.02-2.01</version>
</dependency>
<dependency>
    <groupId>net.sf.sevenzipjbinding</groupId>
    <artifactId>sevenzipjbinding-all-platforms</artifactId>
    <version>16.02-2.01</version>
</dependency>
```

`sevenzipjbinding-all-platforms` innehåller native-bibliotek och väljer variant
vid körning. Det krävs därför ingen separat installation av 7-Zip. Biblioteket
stöder bland annat RAR, RAR5, lösenordsskyddade arkiv och flerpartsarkiv.

Det är ett rimligt alternativ för CopyExtractors huvudsakliga mål:

- Windows x64
- Ubuntu/Linux x64

Namnet `all-platforms` betyder dock inte alla existerande plattformar:

- ARM-varianterna ingår enligt projektets dokumentation inte i
  `all-platforms`.
- Den senaste officiella versionen publicerades 2020 och använder 7-Zip 16.02
  från 2016.
- Det är en JNI-lösning. Ett fel i native-koden kan påverka eller krascha hela
  JVM-processen.
- Native-biblioteket packas upp till `java.io.tmpdir` och laddas därifrån.
  Miljöer där temporära kataloger är skrivskyddade eller monterade med `noexec`
  kan därför ge problem.
- Projektets dokumentation nämner en möjlig JVM-krasch vid `OutOfMemoryError`.
- Nya RAR-varianter eller komprimeringsmetoder som tillkommit efter den gamla
  7-Zip-motorn kan saknas.

SevenZipJBinding måste därför verifieras i projektets fat JAR på minst Windows
x64 och Ubuntu x64, med RAR4, RAR5, solid-arkiv, flerpartsarkiv, stora filer och
CRC-fel. Det bör inte införas med antagandet att det fungerar med alla arkiv och
plattformar.

## Rekommenderad design

### Nuvarande kod och historik

`UnrarHandler` är trots sitt generella namn direkt kopplad till Junrar:

- Den anropar den statiska metoden `JunrarWrapper.getFileHeaderIterator`.
- Dess interna flöde arbetar med `FileHeaderWrapper`.
- `FileHeaderWrapper` innehåller Junrar-typerna `Archive` och `FileHeader` och
  anropar `Archive.extractFile` direkt.
- `UnrarHandler` exponerar dessutom Junrars `RarException` i interna
  metodsignaturer.

Anropskedjan är alltså:

```text
Executor
  -> UnrarHandler
     -> JunrarWrapper
        -> Junrar Archive
     -> FileHeaderWrapper
        -> Junrar Archive.extractFile
```

Git-historiken visar att `UnrarHandler`, `JunrarWrapper` och
`FileHeaderWrapper` skapades tillsammans den 31 december 2023 i commit
`0f4ff70` (`Improve test coverage`). Commitmeddelandet beskriver refaktoreringen
som ett sätt att möjliggöra tester. Wrapper-klasserna bör därför främst ses som
testbarhetsadaptrar, inte som en färdig abstraktion för utbytbara
extraheringsmotorer.

Den föregående `RarHandler` innehöll samtidigt en TODO om att använda en
installerad UnRAR-applikation för RAR5. Det visar att stöd för en alternativ
backend övervägdes, men det implementerades inte som ett gemensamt interface.

För att verkligen kunna byta motor behöver gränsen flyttas upp: `UnrarHandler`
ska bero på ett eget `ArchiveExtractor`-interface som inte importerar några
Junrar-klasser. Junrar-kod och Junrar-undantag ska endast finnas i
`JunrarExtractor`.

Extraherarna bör döljas bakom ett gemensamt interface, exempelvis:

```java
public interface ArchiveExtractor {
    boolean isAvailable();
    boolean supports(File archive);
    ExtractionResult test(File archive);
    ExtractionResult extract(File archive, File destination);
}
```

Tänkbara implementationer:

- `BundledUnrarExtractor`
- `SystemUnrarExtractor`
- `SystemSevenZipExtractor`
- `SevenZipJBindingExtractor`
- `JunrarExtractor`

Det återstår att välja mellan att paketera officiella UnRAR-binärer och att
använda SevenZipJBinding som den medföljande native-lösningen. Officiell UnRAR
ger normalt bäst RAR-kompatibilitet, medan SevenZipJBinding ger ett Java-API och
en färdig Maven-distribution. Licensvillkoren måste granskas innan någon native-
binär distribueras tillsammans med CopyExtractor.

## Påbörjad implementation

På branchen `improve-archive-extraction` har följande fristående grund lagts
till utan att ändra eller koppla om den befintliga `UnrarHandler`:

- `ArchiveExtractor` är det implementationoberoende kontraktet.
- `ExtractionResult` skiljer lyckad körning, arkivfel och start-/processfel.
- `CommandRunner` och `ProcessCommandRunner` kör externa program utan shell.
- `CommandArchiveExtractor` innehåller gemensam fel- och processhantering.
- `UnrarCommandExtractor` bygger kommandon för officiell UnRAR.
- `SevenZipCommandExtractor` bygger kommandon för 7-Zip.

Processutdata slås ihop med felutdata, dräneras för att undvika blockerade
processer och begränsas till 64 KiB i minnet. Processens standard input stängs
direkt så att en extraherare inte kan fastna i en interaktiv fråga. Discovery
för systeminstallerade verktyg är implementerad. Paketerade binärer, automatiskt
backend-val och inkoppling av detta val i standardflödet återstår.

### Progress

Det nya extraheringskontraktet har även ett implementationoberoende
progresslager:

- `ExtractionPhase` beskriver `TESTING`, `EXTRACTING`, `COMPLETED` och `FAILED`.
- `ExtractionProgress` kan bära fas, meddelande och i framtiden valfri procent
  och aktuellt filnamn.
- `ProgressListener` tar emot händelser utan att känna till vald backend.
- `ConsoleProgressListener` visar en spinner och förfluten tid (`mm:ss`).

UnRAR- och 7-Zip-implementationerna skickar start- och sluthändelser runt varje
processkörning. Exakt procent visas ännu inte, eftersom den kräver separata och
versionskänsliga parsers för respektive programs utdata. Anrop utan listener
fortsätter fungera genom en inbyggd no-op-listener.

### Uppdaterad UnrarHandler

`UnrarHandler` är nu backend-oberoende och har tre injicerade beroenden:

- `Configuration`
- `ArchiveExtractor`
- `ProgressListener`

Standardkonstruktorn använder tills vidare `JunrarExtractor` och
`ConsoleProgressListener`, så `Executor` fortsätter fungera utan att någon
backend-väljare ännu är inkopplad. En annan backend kan användas genom den nya
konstruktorn utan att `UnrarHandler` behöver ändras igen.

All Junrar-specifik iteration och extrahering har flyttats till
`JunrarExtractor`. Den bevarar kontrollen som hoppar över en befintlig målfil
med minst förväntad storlek, skapar underkataloger och blockerar arkivposter som
försöker skriva utanför målkatalogen. `FileOutputStream` stängs nu alltid med
try-with-resources.

Resultaträkningen sker efter refaktoreringen per behandlat arkiv i
`UnrarHandler`, inte per filpost inuti arkivet. Backend-discovery och automatiskt
val mellan paketerad UnRAR, installerad UnRAR, 7-Zip och Junrar återstår.

### Lista tillgängliga extraherare

CLI-kommandot nedan kan köras utan konfigurationsfil:

```text
java -jar CopyExtractor.jar --list-extractors
```

Kortformen är `-le`. `ArchiveExtractorDiscovery` provar UnRAR och 7-Zip via
systemets `PATH`. På Windows provas även standardinstallationerna under
`ProgramFiles` och `ProgramFiles(x86)`. På Linux provas `unrar`, `7zz`, `7z` och
`7za`. Junrar rapporteras alltid som en inbyggd Java-fallback.

Exempel:

```text
Archive extractors:
  available UnRAR      ProgramFiles (C:\Program Files\WinRAR\UnRAR.exe)
  not found 7-Zip      not found (-)
  available Junrar     built-in Java fallback (classpath)
```

`availableExtractors()` exponerar dessutom de hittade implementationerna i
prioritetsordning för den kommande automatiska backend-väljaren. Discovery är
ännu inte inkopplad i standardkonstruktorn för `UnrarHandler`.
