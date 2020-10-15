package com.github.extractor.handlers;

import java.io.File;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.utils.Dirs;

public class DirHandler {

    private final Configuration config;
    private final FileHandler fileHandler;

    public DirHandler(final FileHandler fileHandler, final Configuration config) {
        this.fileHandler = fileHandler;
        this.config = config;
    }

    public boolean folderHasMultipleFoldersToScan(final File source) {
        final int count = numberOfFoldersOfInterest(source);
        return count > 0;
    }

    /**
     * Given folder contains included files to copy files
     *
     * @param folder
     * @return
     */
    public boolean dirContainsIncludedFileTypes(final File folder) {
        final File[] fileItems = folder.listFiles();
        for (final File file : fileItems) {
            if (fileHandler.isIncludedFileType(file) && !fileHandler.isIgnored(file)) {
                return true;
            }
        }

        return false;
    }

    public boolean directoryIncluded(final File dir) {
        if (!dir.isDirectory()) {
            return false;
        }

        final String dirName = dir.getName().toLowerCase();
        return config.getIncludeFolders().stream().anyMatch(include -> dirName.contains(include));
    }

    public String getBaseDir(final File file) {
        return Dirs.getBaseDirName(file, config.getGroupByRegex());
    }

    public String getDirName(final File dir) {
        final String baseDir = Dirs.getBaseDirName(dir, config.getGroupByRegex());
        return Dirs.getTargetDirName(dir, baseDir);
    }

    private int numberOfFoldersOfInterest(final File pathToScan) {
        int count = 0;
        final File[] directories = pathToScan.listFiles();
        if (directories == null) {
            return count;
        }

        for (final File dir : directories) {
            if (!dir.isDirectory()) {
                continue;
            }

            if (fileHandler.isIgnored(dir) || directoryIncluded(dir)) {
                continue;
            }

            if (RarHandler.dirContainsUnrarable(dir) || dirContainsIncludedFileTypes(dir)) {
                count = count + 1;
                continue;
            }
            count = count + numberOfFoldersOfInterest(dir);
        }
        return count;
    }
}
