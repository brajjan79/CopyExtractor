package com.github.extractor.configuration;

import java.util.ArrayList;
import java.util.List;

import com.github.extractor.exceptions.ConfigurationException;
import com.github.extractor.models.ConfigFolder;

public class Configuration {

    private static final String DEFAULT_GROUP_BY_REGEX = "(?!x)x";

    private static Configuration instance;

    private final List<String> fileTypes;
    private final List<String> ignore;
    private final List<String> includeFolders;
    private final List<ConfigFolder> folders;

    private final String groupByRegex;

    private final boolean keepFolder;
    private final boolean keepFolderStructure;
    private final boolean recursive;
    private boolean dryRun = false;

    /**
     *
     * @param fileTypes           :: List of FolderItem  File types to copy
     * @param ignore              :: List of String  Folders that should not be scanned.
     * @param includeFolders      :: List of String  Folders to include (extra folders not normally scanned)
     * @param folders             :: List of String  Folders to scan and copy to
     * @param groupByRegex        :: String  If item can be grouped, copy exstract items to a folder with the
     *                               grouped name.
     * @param keepFolder          :: boolean If file is in folder, keep the folder
     * @param keepFolderStructure :: boolean When scanning recursively keep the structure
     * @param recursive           :: boolean Folders should be scanned recursively
     * @throws ConfigurationException
     */
    public Configuration(final List<String> fileTypes, final List<String> ignore,
            final List<String> includeFolders, final List<ConfigFolder> folders,
            final String groupByRegex, final boolean keepFolder, final boolean keepFolderStructure,
            final boolean recursive) throws ConfigurationException {
        this.folders = folders;
        this.fileTypes = fileTypes;
        this.ignore = ignore;
        this.includeFolders = includeFolders;
        this.groupByRegex = groupByRegex;
        this.keepFolder = keepFolder;
        this.keepFolderStructure = keepFolderStructure;
        this.recursive = recursive;
    }

    public static synchronized Configuration getInstance() {
        return instance;
    }

    // Method to set a custom (mock) instance for testing
    public static synchronized void setInstance(Configuration mockInstance) {
        Configuration.instance = mockInstance;
    }

    public List<String> getFileTypes() {
        if (fileTypes == null) {
            return new ArrayList<>();
        }

        return fileTypes;
    }

    public List<String> getIgnored() {
        if (ignore == null) {
            return new ArrayList<>();
        }

        return ignore;
    }

    public List<String> getIncludeFolders() {
        if (includeFolders == null) {
            return new ArrayList<>();
        }

        return includeFolders;
    }

    public List<ConfigFolder> getFolders() {
        if (folders == null) {
            throw new ConfigurationException("No folder configuration provided.");
        }

        return folders;
    }

    public String getGroupByRegex() {
        if (groupByRegex == null) {
            return DEFAULT_GROUP_BY_REGEX;
        }

        return groupByRegex;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
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
        return String.format("Configuration:\n"
                + "folders: %s\n"
                + "fileEndingsToCopy: %s\n"
                + "includedeFolders: %s\n"
                + "ignoreList: %s\n"
                + "groupBy: '%s'\n"
                + "keepFolder: %s\n"
                + "keepFolderStructure: %s\n"
                + "recursive: %s\n"
                + "dryRun: %s",
                getFolders(),
                getFileTypes(),
                getIncludeFolders(),
                getIgnored(),
                getGroupByRegex(),
                isKeepFolder(),
                isKeepFolderStructure(),
                isRecursive(),
                isDryRun());
    }

}
