package com.github.extractor.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.utils.AutoCloseableIterator;
import com.github.extractor.utils.FileHeaderWrapper;
import com.github.extractor.utils.JunrarWrapper;
import com.github.filesize.FileSize;
import com.github.junrar.exception.RarException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;

class UnrarHandlerTest {

    @Mock
    private Configuration mockConfig;

    @Mock
    private AutoCloseableIterator<FileHeaderWrapper> mockFileHeaderIterator;

    @Mock
    private FileHeaderWrapper mockFileHeaderWrapper;

    private UnrarHandler unrarHandler;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        unrarHandler = new UnrarHandler(mockConfig);

        candidate = new Candidate("name", new File("targetDir"));
        candidate.filesToUnrar.add(new File("archive.rar"));
    }

    @Test
    void unrarFilesShouldExtractFiles() throws Exception {
        try (MockedStatic<JunrarWrapper> mockedJunrarWrapper = mockStatic(JunrarWrapper.class);
                MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class)) {

            mockedJunrarWrapper.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(mockFileHeaderIterator);
            when(mockFileHeaderIterator.hasNext()).thenReturn(true, false);
            when(mockFileHeaderIterator.next()).thenReturn(mockFileHeaderWrapper);
            when(mockFileHeaderWrapper.getDestinationFile(any(File.class))).thenReturn(new File("unpackedFile.txt"));
            when(mockFileHeaderWrapper.getUnpackedSize()).thenReturn(1000.0);
            when(mockConfig.isDryRun()).thenReturn(false);

            unrarHandler.unrarFiles(candidate);

            verify(mockFileHeaderWrapper).extractFile(any(File.class));
            verify(mockFileHeaderIterator, times(1)).next();
            verify(mockFileHeaderWrapper, times(1)).getUnpackedSize();
            // Verify other interactions and StateConstants.addSuccess()
        }
    }

    @Test
    void unrarFilesShouldSkipExistingFiles() throws FileNotFoundException {
        try (MockedStatic<JunrarWrapper> mockedJunrarWrapper = mockStatic(JunrarWrapper.class);
                MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class)) {

            mockedJunrarWrapper.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(mockFileHeaderIterator);
            when(mockFileHeaderIterator.hasNext()).thenReturn(true, false);
            when(mockFileHeaderIterator.next()).thenReturn(mockFileHeaderWrapper);
            final File existingFile = mock(File.class);
            when(mockFileHeaderWrapper.getDestinationFile(any(File.class))).thenReturn(existingFile);
            when(existingFile.exists()).thenReturn(true);
            mockedFileSize.when(() -> FileSize.getBytes(existingFile)).thenReturn(1000.0);
            when(mockFileHeaderWrapper.getUnpackedSize()).thenReturn(500.0);

            unrarHandler.unrarFiles(candidate);

            verify(mockFileHeaderIterator, times(1)).next();
            verify(mockFileHeaderWrapper, times(1)).getUnpackedSize();
            // Verify System.out.println and StateConstants.addAlreadyExists()
        }
    }

    @Test
    void unrarFilesShouldHandleRarException() throws Exception {
        try (final MockedStatic<JunrarWrapper> mockedJunrarWrapper = mockStatic(JunrarWrapper.class);
                final MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class)) {

            mockedJunrarWrapper.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(mockFileHeaderIterator);
            when(mockFileHeaderIterator.hasNext()).thenReturn(true, false);
            when(mockFileHeaderIterator.next()).thenReturn(mockFileHeaderWrapper);
            when(mockFileHeaderWrapper.getDestinationFile(any(File.class))).thenReturn(new File("unpackedFile.txt"));
            when(mockFileHeaderWrapper.getUnpackedSize()).thenReturn(1000.0);
            when(mockConfig.isDryRun()).thenReturn(false);

            doThrow(new RarException()).when(mockFileHeaderWrapper).extractFile(any(File.class));

            unrarHandler.unrarFiles(candidate);

            verify(mockFileHeaderWrapper).extractFile(any(File.class));
            verify(mockFileHeaderIterator, times(1)).next();
            verify(mockFileHeaderWrapper, times(1)).getUnpackedSize();
            // Verify other interactions and StateConstants.addSuccess()
        }
    }
}

