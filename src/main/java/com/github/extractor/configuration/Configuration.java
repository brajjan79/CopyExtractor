package com.github.extractor.configuration;

import java.util.ArrayList;
import java.util.List;

import com.github.extractor.configuration.models.ConfigFolder;
import com.github.extractor.exceptions.ConfigurationException;

public class Configuration {

    private static final String DEFAULT_GROUP_BY_REGEX = "(?!x)x";

    private final List<String> copyFiles;
    private final List<String> ignoredFolders;
    private final List<String> includedFolders;
    private final List<ConfigFolder> folders;

    private final String groupByRegex; // Regex that cannot match anything, ever.

    private final boolean keepFolder;
    private final boolean keepFolderStructure;
    private final boolean recursive;

    /**
     *
     * @param copyFiles           :: List of FolderItem  File types to copy
     * @param ignoredFolders      :: List of String  Folders that should not be scanned.
     * @param includedFolders     :: List of String  Folders to include (extra folders not normally scanned)
     * @param folders             :: List of String  Folders to scan and copy to
     * @param groupByRegex        :: String  If item can be grouped, copy exstract items to a folder with the
     *                            grouped name.
     * @param keepFolder          :: boolean If file is in folder, keep the folder
     * @param keepFolderStructure :: boolean When scanning recursively keep the structure
     * @param recursive           :: boolean Folders should be scanned recursively
     * @throws ConfigurationException
     */
    public Configuration(final List<String> copyFiles, final List<String> ignoredFolders,
        final List<String> includedFolders, final List<ConfigFolder> folders,
        final String groupByRegex, final boolean keepFolder, final boolean keepFolderStructure,
        final boolean recursive) throws ConfigurationException {
        this.folders = folders;
        this.copyFiles = copyFiles;
        this.ignoredFolders = ignoredFolders;
        this.includedFolders = includedFolders;
        this.groupByRegex = groupByRegex;
        this.keepFolder = keepFolder;
        this.keepFolderStructure = keepFolderStructure;
        this.recursive = recursive;
    }

    public List<String> getCopyFiles() {
        if (copyFiles == null)
            return new ArrayList<>();

        return copyFiles;
    }

    public List<String> getIgnoredFolders() {
        if (ignoredFolders == null)
            return new ArrayList<>();

        return ignoredFolders;
    }

    public List<String> getIncludedFolders() {
        if (includedFolders == null)
            return new ArrayList<>();

        return includedFolders;
    }

    public List<ConfigFolder> getFolders() {
        if (folders == null)
            throw new ConfigurationException("No folder configuration provided.");

        return folders;
    }

    public String getGroupByRegex() {
        if (groupByRegex == null)
            return DEFAULT_GROUP_BY_REGEX;

        return groupByRegex;
    }

    public boolean isKeepFolder() {
        return keepFolder;
    }

    public boolean isKeepFolderStructure() {
        return keepFolderStructure;
    }

    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public String toString() {
        return String.format(
            "Configuration:\nfolders: %s\nfileEndingsToCopy: %s\nincludedeFolders: %s\n" +
                "ignoreList: %s\ngroupBy: '%s'\nkeepFolder: %s\nkeepFolderStructure: %s\nrecursive: %s",
            getFolders(), getCopyFiles(), getIncludedFolders(), getIgnoredFolders(), getGroupByRegex(),
            isKeepFolder(), isKeepFolderStructure(), isRecursive());
    }

}
