package com.github.extractor.handlers;

import java.io.File;

import com.github.extractor.configuration.Configuration;

public class FileHandler {

    private final static Configuration config = Configuration.getInstance();

    public static boolean isIncludedFileType(final File file) {
        final String fileName = file.getName().toLowerCase();
        return config.getFileTypes().stream().anyMatch(fileEnding -> fileName.endsWith(fileEnding));
    }

    public static boolean isIgnored(final File file) {
        final String fileName = file.getName().toLowerCase();
        return config.getIgnored().stream().anyMatch(ignore -> fileName.contains(ignore));
    }
}
