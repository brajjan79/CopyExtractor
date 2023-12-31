package com.github.extractor.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.github.filesize.FileSize;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProgressBarWrapperTest {

    @Mock
    private File mockFile;

    @Mock
    private ProgressBarBuilder mockProgressBarBuilder;

    @Mock
    private ProgressBar mockProgressBar;

    private ProgressBarWrapper progressBar;
    private MockedStatic<FileSize> mockedFileSize;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedFileSize = mockStatic(FileSize.class);
        when(mockFile.getName()).thenReturn("testFile.rar");
        progressBar = new ProgressBarWrapper(1000, mockFile, "Extracting");
        progressBar.init(mockProgressBarBuilder);
        when(mockProgressBarBuilder.build()).thenReturn(mockProgressBar);
    }

    @AfterEach
    void tearDown() {
        mockedFileSize.close();
    }

    @Test
    void testProgressCalculation() {
        mockedFileSize.when(() -> FileSize.getBytes(mockFile)).thenReturn(500.0);
        assertEquals(50, progressBar.getProgress());
    }

    @Test
    void testRunMethodUpdatesProgressBar() {
        mockedFileSize.when(() -> FileSize.getBytes(mockFile)).thenReturn(500.0, 1000.0);
        when(mockProgressBar.stepTo(anyInt())).thenReturn(mockProgressBar);

        progressBar.run();

        verify(mockProgressBar, times(1)).stepTo(50);
        verify(mockProgressBar, times(1)).stepTo(100);
    }

    @Test
    void testRunMethodRespectsCancellation() {
        mockedFileSize.when(() -> FileSize.getBytes(mockFile)).thenReturn(500.0);
        progressBar.cancel();

        progressBar.run();

        verify(mockProgressBar, never()).stepTo(100);
    }

    // Additional tests as required
}
