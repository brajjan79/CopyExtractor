package com.github.extractor.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

public class FileHeaderWrapper {

    private final FileHeader fileHeader;
    private final Archive archive;

    public FileHeaderWrapper(Archive archive, FileHeader fileHeader) {
        this.fileHeader = fileHeader;
        this.archive = archive;
    }

    public File getDestinationFile(File targetDir) {
        return new File(targetDir, fileHeader.getFileName());
    }

    public double getUnpackedSize() {
        return fileHeader.getFullUnpackSize();
    }

    public void extractFile(final File targetFile) throws IOException, RarException {
        try (FileOutputStream output = new FileOutputStream(targetFile)) {
            extractFile(output);
        }
    }

    public void extractFile(final OutputStream output) throws RarException {
        archive.extractFile(fileHeader, output);
    }

}
