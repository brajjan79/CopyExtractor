package com.github.extractor.handlers;

import java.io.File;
import java.util.List;

import com.github.extractor.configuration.Configuration;

public class FileHandler {

    private Configuration config = Configuration.getInstance();

    public FileHandler() {
        this.config = Configuration.getInstance();
    }

    public FileHandler(Configuration config) {
        this.config = config;
    }

    public boolean isIncludedFileType(final File file) {
        final String fileName = file.getName().toLowerCase();
        return config.getFileTypes().stream().anyMatch(fileEnding -> fileName.endsWith(fileEnding));
    }

    public boolean isIgnored(final File file) {
        final String fileName = file.getName().toLowerCase();
        return config.getIgnored().stream().anyMatch(ignore -> fileName.contains(ignore));
    }
}
