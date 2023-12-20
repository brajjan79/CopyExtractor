# CopyExtractor
[![Java CI tests with Maven](https://github.com/brajjan79/CopyExtractor/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/brajjan79/CopyExtractor/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/074f22e39d4c4bbaa99c7fc660ef2e8c)](https://app.codacy.com/gh/brajjan79/CopyExtractor/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/074f22e39d4c4bbaa99c7fc660ef2e8c)](https://app.codacy.com/gh/brajjan79/CopyExtractor/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)

CopyExtractor scans a given folder for files to unrar or copy depending on the
given configuration.

**CopyExtractor can**:

-   extraxt rar files up to RAR 4

-   copy any files based on the configured file types

-   ignore folders and files user does not want copied or extracted

-   include files or folders that user want to have copied or extracted to the
    same place

**CopyExtractor currently does not support**:

-   copying only

-   RAR 5

-   zip or anything that is not rar

## Install

Installation process is done via Apache Maven.
You may also download the already built artifact.

### Clone repository

```git
git clone https://github.com/brajjan79/CopyExtractor.git
```

### Maven package

```mvn
cd CopyExtractor # Enter repository root directory
mvn package -DskipTests
```

maven will package the source files into a .jar file located in ./target
with the name CopyExtractor-\<version>.jar where \<version> is the current
version on main branch.

## JAVA compatabilities

Supported JAVA versions: 8 (probably), 16 (tested) 

## Usage

```java
# Display help text.
java -jar \<some path\> CopyExtractor-\<version\>.jar --help

# Execute configuration
java -jar \<some path\> CopyExtractor-\<version\>.jar -f path/to/config_file.json
```

### CLI

```java
usage: java --jar CopyExtractor.jar  [options value]

Options:
 -f,--config-file-path <path>   Local path to configuration file.
 -s,--source-folder <path>      Folder to extract from.
 -t,--target-folder <path>      Folder to extract to.
 -i,--ignore <list>             Files and or folders to ignore Example: 'sample, proof'.
 -ft,--file-types <list>        File types to copy. Example: 'jpg, bmp'
 -if,--include-folders <list>   Folders to include. Example: 'thumbs'
 -rx,--group-by-regex <str>     Regex to group files or folders.
 -R,--recursive                 Extract recursively.
 -kf,--keep-folder              Source folder will be kept.
 -kfs,--keep-folder-structure   Target dirs will keep the same folder structure as source.
 -d,--dry-run                   Will not copy or extract, only log.
 -h,--help                      Prints this Help Text
```

### Configuration file

Maybe the easiest way to use the CopyExtractor is to provide a configuration file.
The configuration file is written in JSON format.

**Key description:**

-   **fileTypes** List of Strings, Example: `["jpg", "png"]`. File types to scan for and copy.

-   **ignore** List of Strings, Example: `["sample"]`. Folders or files that will be ignored.

-   **includeFolders** List of Strings, Example: `["info", "description"]`. Folders that will be included and is not considered a normal folder to copy or extract from.

-   **folders** List of Objects, Example `[{"inputFolder":"C:/input", "outputFolder":"C:/output"}]`. List of objects containing keys inputFolder and outputFolder.

-   **groupByRegex** String, Example: `"(?<=[0-9]{4}).*"`. GroupByRegex is a regex that when detected will put multiple found items in the same directory. The example `"(?<=[0-9]{4}).*"` would group all items based on the year and name before year.

-   **keepFolder** boolean, Example: `true`. If **true** files extracted or copied from a folder will be copied or extracted to a folder with the same name. If **false** files will be copied or extracted directly to **outputFolder**.

-   **keepFolderStructure** boolean, Example: `false`. only affected if recursive is **true**. If **true** files will be extracted and copied to the same structure as the inputFolder has, if **false** files will be copied to outputFolder unless groupByRegex groups the files.

-   **recursive** boolean, Example: `true`. If **false** only files and folders directly in inputFolder will be scanned and folders inside folders will be ignored, unless in includedFolders list. If **true** each folder with multiple folders inside them that contains files that can be copied or unrared is considered an inputFolder.

-   **dryRun** boolean, Example: `true`. If **true** will only log action but none will be taken.

```JSON
# Note: Only folders are mandatory.
{
    "fileTypes": [
        "jpg",
        "png",
        "PNG",
        "gif",
        "mp4",
        "mkv"
    ],
    "ignore": [
        "sample"
    ],
    "includeFolders": [
        "notes"
    ],
    "folders": [
        {
            "inputFolder": "C:/test",
            "outputFolder": "C:/test/out"
        }
    ],
    "groupByRegex":"([.2018.])",
    "keepFolder": false,
    "keepFolderStructure": false,
    "recursive": true,
    "dryRun": false
}
```
