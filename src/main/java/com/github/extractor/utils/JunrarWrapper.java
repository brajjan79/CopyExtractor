package com.github.extractor.utils;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

public class JunrarWrapper {

    public static AutoCloseableIterator<FileHeaderWrapper> getFileHeaderIterator(File rarFile) throws IOException, RarException {
        final Archive archive = new Archive(rarFile);
        return new AutoCloseableIterator<>() {
            private FileHeader nextFileHeader = archive.nextFileHeader();

            public boolean hasNext() {
                return nextFileHeader != null;
            }

            public FileHeaderWrapper next() {
                if (nextFileHeader == null) {
                    throw new NoSuchElementException();
                }
                final FileHeaderWrapper current = new FileHeaderWrapper(archive, nextFileHeader);
                nextFileHeader = archive.nextFileHeader();
                return current;
            }

            public void close() throws IOException {
                archive.close();
            }
        };
    }

}
