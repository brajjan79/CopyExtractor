package com.github.extractor.utils;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FileHeaderWrapperTest {

    @Mock
    private FileHeader mockFileHeader;
    @Mock
    private Archive mockArchive;
    private FileHeaderWrapper fileHeaderWrapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileHeaderWrapper = new FileHeaderWrapper(mockArchive, mockFileHeader);
    }

    @Test
    void getDestinationFileShouldReturnCorrectFile() {
        final File targetDir = new File("targetDir");
        final String fileName = "testFile.rar";
        when(mockFileHeader.getFileName()).thenReturn(fileName);

        final File result = fileHeaderWrapper.getDestinationFile(targetDir);

        assertEquals(new File(targetDir, fileName), result);
    }

    @Test
    void getUnpackedSizeShouldReturnCorrectSize() {
        final long expectedSize = 1000L;
        when(mockFileHeader.getFullUnpackSize()).thenReturn(expectedSize);

        final double result = fileHeaderWrapper.getUnpackedSize();

        assertEquals(expectedSize, result);
    }

    @Test
    void extractFileShouldExtractCorrectly() throws IOException, RarException {
        final Path tempDir = Files.createTempDirectory(null);
        try {
            final File targetFile = new File(tempDir.toFile(), "targetFile");
            doNothing().when(mockArchive).extractFile(any(FileHeader.class), any(FileOutputStream.class));

            fileHeaderWrapper.extractFile(targetFile);

            verify(mockArchive).extractFile(eq(mockFileHeader), any(FileOutputStream.class));
        } finally {
            tempDir.toFile().delete();
        }
    }

    // Additional tests as required
}
