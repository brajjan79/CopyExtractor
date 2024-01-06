package com.github.extractor.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.filesize.FileSize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FileProgressBarTest {
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Mock
    private FileSize mockedFileSize;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        System.setOut(new PrintStream(outContent));
        when(mockedFileSize.getBytes()).thenReturn(0.0, 25.0, 50.0, 75.0, 100.0);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testProgressBarOutput() {
        final File mockFile = new File("test.txt");
        final double totalSize = 100.0;

        final FileProgressBar progressBar = FileProgressBar.build().trackedFile(mockFile).expectedSize(totalSize)
                .setFileSizeInstance(mockedFileSize);

        Assertions.assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            progressBar.start();
            progressBar.waitForCompletion();

            final String output = outContent.toString();
            Assertions.assertTrue(output.contains("100%"));
        });
    }

    @Test
    public void testProgressBarOutputAborted() {
        final File mockFile = new File("test.txt");
        final double totalSize = 100.0;

        final FileProgressBar progressBar = FileProgressBar.build().trackedFile(mockFile).expectedSize(totalSize)
                .setFileSizeInstance(mockedFileSize);

        Assertions.assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            progressBar.start();
            Thread.sleep(5);
            progressBar.complete();

            final String output = outContent.toString();
            Assertions.assertTrue(output.contains("%"));
            Assertions.assertTrue(!output.contains("100%"));
        });
    }

    @Test
    public void testTruncatePathLong() {
        final String fileName = "some_file.txt";
        final String truncatedPath = FileProgressBar.truncateFileName(fileName, 8);
        assertEquals(truncatedPath, fileName.substring(0, 5) + "...");

        final String truncatedFullPath = FileProgressBar.truncateFileName(fileName, 15);
        assertEquals(truncatedFullPath, fileName + "  ");
    }
}

