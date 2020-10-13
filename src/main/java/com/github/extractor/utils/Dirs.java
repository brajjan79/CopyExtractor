package com.github.extractor.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.github.stringreformat.RegexFormat;
import com.github.stringreformat.exceptions.MatchNotFoundException;


public class Dirs {

    /**
     * Returns the part of the filename before provided regex.
     *
     * @param file
     * @param groupByRegex
     * @return
     */
    public static String getBaseDirName(final File file, final String groupByRegex) {
        final String name = FilenameUtils.getBaseName(file.getName());
        String baseFolder = "";

        final boolean hasMatch = RegexFormat.hasMatch(name, groupByRegex);

        if (hasMatch) {
            try {
                baseFolder = RegexFormat.reformat(name, groupByRegex + ".*", "").toLowerCase();
            } catch (final MatchNotFoundException e) {}
        }

        return baseFolder;
    }

    /**
     * Formats a path using base dir of any with file name.
     *
     * @param dir
     * @param baseDir
     * @return
     */
    public static String getTargetDirName(final File dir, final String baseDir) {
        String targetDir = "";
        if (!baseDir.isEmpty()) {
            targetDir = String.format("%s/", baseDir);
        }

        targetDir = String.format("%s%s", targetDir, dir.getName());

        return targetDir;
    }

    /**
     * Return boolean weather the last modified date is less then the provided milliseconds.
     *
     * @param file
     * @param millisec
     * @return
     */
    public static boolean lastModifiedLessThen(final File file, final int millisec) {
        final long lastModified = file.lastModified();
        final long currentTime = System.currentTimeMillis();
        final long timeSinceModified = currentTime - lastModified;

        if (timeSinceModified < millisec) {
            return true;
        }
        return false;
    }

    /**
     * Creates all dirs needed for the provided path.
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static boolean createDirs(final File dir) throws IOException {
        if (!dir.exists()) {
            return createDir(dir);
        }

        if (!dir.isDirectory()) {
            throw new IOException("Target directory is not a directory: " + dir.getAbsolutePath());
        }
        return true;
    }

    /**
     * Deletes all dirs sub dirs and files in the provided folder.
     *
     * @param dir
     * @return
     */
    public static boolean deleteDirs(final File dir) {
        if (!dir.exists()) {
            return true;
        }

        return deleteDir(dir);
    }

    private static boolean createDir(final File dir) throws IOException {
        if (!dir.getParentFile().exists()) {
            createDir(dir.getParentFile());
        }

        if (!dir.mkdir()) {
            return false;
        }

        System.out.println("Created directory " + dir.getAbsolutePath());
        return true;
    }

    private static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            final File[] children = dir.listFiles();
            for (final File child : children) {
                if (!deleteDir(child)) {
                    return false;
                }
            }
        }

        System.out.println("Removing file or directory : " + dir.getName());
        return dir.delete();
    }

}
