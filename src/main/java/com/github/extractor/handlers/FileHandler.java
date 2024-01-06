package com.github.extractor.handlers;

import java.io.File;
import java.io.IOException;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.utils.Dirs;
import com.google.common.io.Files;

public class FileHandler {

    private Configuration config;

    public FileHandler() {
        this.config = Configuration.getInstance();
    }

    public boolean isIncludedFileType(final File file) {
        final String fileName = file.getName().toLowerCase();
        return config.getFileTypes().stream().anyMatch(fileEnding -> fileName.endsWith(fileEnding));
    }

    public boolean isIgnored(final File file) {
        final String fileName = file.getName().toLowerCase();
        return config.getIgnored().stream().anyMatch(ignore -> fileName.contains(ignore));
    }

    public File createFile(File path, File file, boolean createFolderInBaseDir) throws IOException {
        File targetPath = path;
        if (createFolderInBaseDir) {
            final String baseDir = Dirs.getBaseDirName(file, config.getGroupByRegex());
            if (baseDir != "") {
                targetPath = new File(targetPath, baseDir);
            }
            if (config.isKeepFolder()) {
                targetPath = new File(targetPath, Files.getNameWithoutExtension(file.getName()));
            }
        }
        Dirs.createDirs(targetPath);
        return new File(targetPath, file.getName());
    }
}
