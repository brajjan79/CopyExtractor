package com.github.extractor.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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

    public void extractFile(File targetFile) throws FileNotFoundException, RarException {
        final FileOutputStream os = new FileOutputStream(targetFile);
        archive.extractFile(fileHeader, os);
    }

}
