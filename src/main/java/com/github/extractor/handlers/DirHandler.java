package com.github.extractor.handlers;

import java.io.File;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.utils.Dirs;

public class DirHandler {

    private final Configuration config = Configuration.getInstance();

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
            if (FileHandler.isIncludedFileType(file) && !FileHandler.isIgnored(file)) {
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

    public boolean isValidSubfolder(File dir, File outputDir) {
        if (!dir.isDirectory()) {
            return false;
        }

        if (dir.equals(outputDir)) {
            System.out.println("Ignoring output folder");
            return false;
        }
        return true;
    }

    /**
     * Builds a File based on the dir and the name of the provided file.
     *
     * @param dir
     * @param file
     * @return File
     */
    public File buildFile(File dir, File file) {
        return new File(dir, file.getName());
    }

    /**
     * Buils a File based on the dir and the name of the provided file and the
     * directory name the file is in. This file will be grouped if groupBy regex is
     * provided.
     *
     * @param dir
     * @param file
     * @return File
     */
    public File buildTargetSubdirFile(File dir, File file) {
        final String dirName = getDirName(file);
        return new File(dir, dirName);
    }

    /**
     * Buils a File based on the dir and the name of the provided file. This file
     * will be grouped if groupBy regex is provided.
     *
     * @param dir
     * @param file
     * @return File
     */
    public File buildTargetBaseDirFile(File dir, File file) {
        return new File(dir, getBaseDir(file));
    }

    private String getDirName(final File file) {
        final String baseDir = Dirs.getBaseDirName(file, config.getGroupByRegex());
        if (config.isKeepFolder() || config.noLooseFilesInTargetBaseDir()) {
            return Dirs.getTargetDirName(file, baseDir);
        }
        return baseDir;
    }

    private String getBaseDir(final File file) {
        return Dirs.getBaseDirName(file, config.getGroupByRegex());
    }

    private int numberOfFoldersOfInterest(final File pathToScan) {
        int count = 0;
        final File[] directories = pathToScan.listFiles();
        if (directories == null) {
            return count;
        }

        for (final File dir : directories) {
            if (!dir.isDirectory() || FileHandler.isIgnored(dir) || directoryIncluded(dir)) {
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
