package com.github.extractor.utils;

import java.io.File;

public class Rars {

    /**
     * Check if the folder contains any file that can be unrared, file must end with
     * .rar and if is part must be part 001 or 01.
     *
     * @param dir
     * @return
     */
    public static boolean dirContainsUnrarable(final File dir) {
        final File[] files = dir.listFiles();
        for (final File file : files) {
            if (fileIsUnrarable(file)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the file can be unrared, file must end with .rar and if is part must
     * be part 001 or 01.
     *
     * @param file
     * @return
     */
    public static boolean fileIsUnrarable(final File file) {
        final String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".rar")) {
            return false;
        }

        final boolean fileNameContainParts = fileName.matches(".*part[0-9][0-9].*");
        final boolean fileIsFirstOfParts = fileName.contains(".part001.") || fileName.contains(".part01.");

        return !fileNameContainParts || fileIsFirstOfParts;
    }

}
