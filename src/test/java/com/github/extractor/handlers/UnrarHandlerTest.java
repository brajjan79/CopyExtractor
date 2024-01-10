package com.github.extractor.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.AutoCloseableIterator;
import com.github.extractor.utils.FileHeaderWrapper;
import com.github.extractor.utils.FileProgressBar;
import com.github.extractor.utils.JunrarWrapper;
import com.github.filesize.FileSize;
import com.github.junrar.exception.RarException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
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
    @Mock
    private FileProgressBar mockedFileProgressBar;

    private MockedStatic<JunrarWrapper> mockedJunrarWrapper;
    private MockedStatic<FileProgressBar> mockedFileProgressBarClass;
    private MockedStatic<FileSize> mockedFileSize;
    private MockedStatic<StateConstants> mockedStateConstants;

    private UnrarHandler unrarHandler;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedJunrarWrapper = mockStatic(JunrarWrapper.class);
        mockedFileProgressBarClass = mockStatic(FileProgressBar.class);
        mockedFileProgressBarClass.when(() -> FileProgressBar.build()).thenReturn(mockedFileProgressBar);
        when(mockedFileProgressBar.expectedSize(anyDouble())).thenReturn(mockedFileProgressBar);
        when(mockedFileProgressBar.trackedFile(any())).thenReturn(mockedFileProgressBar);
        when(mockedFileProgressBar.setAction(any())).thenReturn(mockedFileProgressBar);

        mockedFileSize = mockStatic(FileSize.class);
        mockedStateConstants = mockStatic(StateConstants.class);

        when(mockFileHeaderIterator.hasNext()).thenReturn(true, false);
        when(mockFileHeaderIterator.next()).thenReturn(mockFileHeaderWrapper);
        mockedJunrarWrapper.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(mockFileHeaderIterator);

        unrarHandler = new UnrarHandler(mockConfig);

        candidate = new Candidate("name", new File("targetDir"));
        candidate.filesToUnrar.add(new File("archive.rar"));
    }

    @AfterEach
    void tearDown() {
        mockedJunrarWrapper.close();
        mockedFileSize.close();
        mockedFileProgressBarClass.close();
        mockedStateConstants.close();
    }

    @Test
    void unrarFilesShouldExtractFiles() throws Exception {
        when(mockFileHeaderWrapper.getDestinationFile(any(File.class))).thenReturn(new File("unpackedFile.txt"));
        when(mockFileHeaderWrapper.getUnpackedSize()).thenReturn(1000.0);
        when(mockConfig.isDryRun()).thenReturn(false);

        unrarHandler.unrarFiles(candidate);

        verify(mockFileHeaderWrapper).extractFile(any(File.class));
        verify(mockFileHeaderIterator, times(1)).next();
        verify(mockFileHeaderWrapper, times(1)).getUnpackedSize();
    }

    @Test
    void unrarFilesShouldSkipExistingFiles() throws FileNotFoundException {
        final File existingFile = mock(File.class);
        when(mockFileHeaderWrapper.getDestinationFile(any(File.class))).thenReturn(existingFile);
        when(existingFile.exists()).thenReturn(true);
        mockedFileSize.when(() -> FileSize.getBytes(existingFile)).thenReturn(1000.0);
        when(mockFileHeaderWrapper.getUnpackedSize()).thenReturn(500.0);

        unrarHandler.unrarFiles(candidate);

        verify(mockFileHeaderIterator, times(1)).next();
        verify(mockFileHeaderWrapper, times(1)).getUnpackedSize();
    }

    @Test
    void unrarFilesShouldHandleRarException() throws Exception {
        when(mockFileHeaderWrapper.getDestinationFile(any(File.class))).thenReturn(new File("unpackedFile.txt"));
        when(mockFileHeaderWrapper.getUnpackedSize()).thenReturn(1000.0);
        when(mockConfig.isDryRun()).thenReturn(false);

        doThrow(new RarException()).when(mockFileHeaderWrapper).extractFile(any(File.class));

        unrarHandler.unrarFiles(candidate);

        verify(mockFileHeaderWrapper).extractFile(any(File.class));
        verify(mockFileHeaderIterator, times(1)).next();
        verify(mockFileHeaderWrapper, times(1)).getUnpackedSize();

    }
}

