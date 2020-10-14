[![Build Status](https://travis-ci.com/brajjan79/CopyExtractor.svg?branch=main)](https://travis-ci.com/brajjan79/CopyExtractor) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f3aae20186f04dc0984c5b3c74211411)](https://app.codacy.com/gh/brajjan79/CopyExtractor?utm_source=github.com&utm_medium=referral&utm_content=brajjan79/CopyExtractor&utm_campaign=Badge_Grade) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/074f22e39d4c4bbaa99c7fc660ef2e8c)](https://www.codacy.com/gh/brajjan79/CopyExtractor/dashboard?utm_source=github.com&utm_medium=referral&utm_content=brajjan79/CopyExtractor&utm_campaign=Badge_Coverage)

# CopyExtractor

CopyExtractor scans a given folder for files to unrar or copy depending on the
given configuration.

**NOTE:** Application is work in progress, it is possible to use but changes may
suddenly be introduced.

**CopyExtractor can**:

-   extraxt rar files up to RAR 4

-   copy any files based on the configured file types

-   ignore folders and files user does not want copied or extracted

-   include files or folders that user want to have copied or extracted to the
    same place

**CopyExtractor currently does not support**:

-   copying only

-   configuration via command line only

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
with the name CopyExtractor-\\&lt;version>.jar where \\<version> is the current
version on main branch.

## Usage

Ensure you have at least JAVA 8 installed.

```java
# Display help text.
java -jar \<some path\> CopyExtractor-\<version\>.jar --help

# Execute configuration
java -jar \<some path\> CopyExtractor-\<version\>.jar -f path/to/config_file.json
```

### CLI

Currently the command line interface is under construction and will be updated.

```java
usage: java --jar FileRenamer.jar  [options value]

Options:
 -f,--config-file-path <Config>   Local path to configuration file.
 -h,--help                        Prints this Help Text
 <Not Working> -k,--keep-folder-structure       Target dirs will keep the same folder structure as source.
 <Not Working> -R,--recursive                   Extract recursively.
 <Not Working> -s,--source-folder <Source>      Folder to extract from.
 <Not Working> -t,--target-folder <Target>      Folder to extract to.
```

### Configuration file

The easiest way to use the CopyExtractor is to provide a configuration file.
This is currently the only way since no GUI or command lines work.

The configuration file is written in JSON format.

**Key description:**

-   **copyFiles** List of Strings, Example: `["jpg", "png"]`. File types to scan for and copy.

-   **ignoredFolders** List of Strings, Example: `["sample"]`. Folders or files that will be ignored.

-   **includedFolders** List of Strings, Example: `["info", "description"]`. Folders that will be included and is not considered a normal folder to copy or extract from.

-   **folders** List of Objects, Example `[{"inputFolder":"C:/input", "outputFolder":"C:/output"}]`. List of objects containing keys inputFolder and outputFolder.

-   **groupByRegex** String, Example: `"(?<=[0-9]{4}).*"`. GroupByRegex is a regex that when detected will put multiple found items in the same directory. The example `"(?<=[0-9]{4}).*"` would group all items based on the year and name before year.

-   **keepFolder** boolean, Example: `true`. If **true** files extracted or copied from a folder will be copied or extracted to a folder with the same name. If **false** files will be copied or extracted directly to **outputFolder**.

-   **keepFolderStructure** boolean, Example: `false`. Only affected if recursive is **true**. If **true** files will be extracted and copied to the same structure as the inputFolder has, if **false** files will be copied to outputFolder unless groupByRegex groups the files.

-   **recursive** boolean, Example: `true`. If **false** only files and folders directly in inputFolder will be scanned and folders inside folders will be ignored, unless in includedFolders list. If **true** each folder with multiple folders inside them that contains files that can be copied or unrared is considered an inputFolder.

```JSON
# Note: Only folders are mandatory.
{
    "copyFiles": [
        "jpg",
        "png",
        "PNG",
        "gif",
        "mp4",
        "mkv"
    ],
    "ignoredFolders": [
        "sample"
    ],
    "includedFolders": [
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
    "recursive": true
}
```
