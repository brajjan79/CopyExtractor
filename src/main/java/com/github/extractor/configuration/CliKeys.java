package com.github.extractor.configuration;

public enum CliKeys {
    CONFIG_FILE_PATH ("f", "config-file-path", true, "path", "Local path to configuration file."),
    SOURCE_FOLDER ("s", "source-folder", true, "path", "Folder to extract from."),
    TARGET_FOLDER ("t", "target-folder", true, "path", "Folder to extract to."),
    IGNORE ("i", "ignore", true, "list", "Files and or folders to ignore Example: 'sample, proof'."),
    FILE_TYPES ("ft", "file-types", true, "list", "File types to copy. Example: 'jpg, bmp'"),
    INCLUDE_FOLDERS ("if", "include-folders", true, "list", "Folders to include. Example: 'thumbs'"),
    GROUP_BY_REGEX ("rx", "group-by-regex", true, "list", "Regex to group files or fodlers."),
    RECURSIVE ("R", "recursive", "Extract recursively."),
    KEEP_FOLDER ("kf", "keep-folder", "Source folder will be kept."),
    CREATE_FOLDER("cf", "create-folder", "Create a folder if none exist for a file (only applicable for files in root dir)."),
    KEEP_FOLDER_STRUCTURE ("kfs", "keep-folder-structure", "Target dirs will keep the same folder structure as source."),
    DRY_RUN("d", "dry-run", "This means that no files are impacted or changed."),
    QUIET("q", "quiet", "Execute copyExtractor in quiet mode."),
    VERSION("v", "version", "Print CopyExtractor version."),
    HELP("h", "help", "Prints this Help Text.");

    public String shortName;
    public String name;
    public String description;
    public String argumentName;
    public boolean hasArgs;

    /**
     * Constructor for CliKeys.
     *
     * @param shortName
     * @param fullName
     * @param description
     */
    CliKeys(final String shortName, final String fullName, final String description) {
        this(shortName, fullName, false, "", description);
    }

    /**
     * Constructor for CliKeys.
     *
     * @param shortName
     * @param name
     * @param hasArgs
     * @param argumentName
     * @param description
     */
    CliKeys(final String shortName, final String name, final boolean hasArgs, final String argumentName,
            final String description) {
        this.shortName = shortName;
        this.name = name;
        this.description = description;
        this.argumentName = argumentName;
        this.hasArgs = hasArgs;
    }

    /**
     * isRequired returns true or false depending on if CONFIG_FILE_PATH is required or not.
     *
     * @param isConfigFileRequired
     * @return
     */
    public boolean isRequired(final boolean isConfigFileRequired) {
        final boolean configFileOptionRequired = isConfigFileRequired && this.equals(CONFIG_FILE_PATH);
        final boolean inpudFolderAndOutputFolderRequired = !isConfigFileRequired && (this.equals(TARGET_FOLDER) || this.equals(SOURCE_FOLDER));

        return configFileOptionRequired || inpudFolderAndOutputFolderRequired;
    }
}
