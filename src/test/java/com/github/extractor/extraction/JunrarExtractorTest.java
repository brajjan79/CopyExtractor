package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import com.github.extractor.utils.AutoCloseableIterator;
import com.github.extractor.utils.FileHeaderWrapper;
import com.github.extractor.utils.JunrarWrapper;
import com.github.junrar.exception.RarException;

class JunrarExtractorTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExposeNameAndAvailability() {
        final JunrarExtractor extractor = new JunrarExtractor();

        assertEquals("Junrar", extractor.getName());
        assertTrue(extractor.isAvailable());
    }

    @Test
    void shouldTestEveryArchiveEntry() throws Exception {
        final FileHeaderWrapper header = mock(FileHeaderWrapper.class);
        final AutoCloseableIterator<FileHeaderWrapper> headers = iterator(header);
        final List<ExtractionProgress> progress = new ArrayList<>();

        try (MockedStatic<JunrarWrapper> junrar = mockStatic(JunrarWrapper.class)) {
            junrar.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(headers);

            final ExtractionResult result = new JunrarExtractor().test(new File("archive.rar"), progress::add);

            assertTrue(result.isSuccess());
            verify(header).extractFile(any(OutputStream.class));
            assertEquals(List.of(ExtractionPhase.TESTING, ExtractionPhase.COMPLETED), phases(progress));
        }
    }

    @Test
    void shouldExtractArchiveEntry() throws Exception {
        final FileHeaderWrapper header = mock(FileHeaderWrapper.class);
        final File targetFile = tempDir.resolve("folder/file.txt").toFile();
        when(header.getDestinationFile(any(File.class))).thenReturn(targetFile);
        final AutoCloseableIterator<FileHeaderWrapper> headers = iterator(header);
        final List<ExtractionProgress> progress = new ArrayList<>();

        try (MockedStatic<JunrarWrapper> junrar = mockStatic(JunrarWrapper.class)) {
            junrar.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(headers);

            final ExtractionResult result = new JunrarExtractor().extract(new File("archive.rar"), tempDir.toFile(),
                    progress::add);

            assertTrue(result.isSuccess());
            verify(header).extractFile(targetFile);
            assertTrue(targetFile.getParentFile().isDirectory());
            assertEquals(List.of(ExtractionPhase.EXTRACTING, ExtractionPhase.EXTRACTING,
                    ExtractionPhase.COMPLETED), phases(progress));
        }
    }

    @Test
    void shouldNotOverwriteExistingArchiveEntry() throws Exception {
        final FileHeaderWrapper header = mock(FileHeaderWrapper.class);
        final File targetFile = tempDir.resolve("existing.txt").toFile();
        assertTrue(targetFile.createNewFile());
        when(header.getDestinationFile(any(File.class))).thenReturn(targetFile);
        final AutoCloseableIterator<FileHeaderWrapper> headers = iterator(header);

        try (MockedStatic<JunrarWrapper> junrar = mockStatic(JunrarWrapper.class)) {
            junrar.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(headers);

            final ExtractionResult result = new JunrarExtractor().extract(new File("archive.rar"), tempDir.toFile(),
                    ProgressListener.NONE);

            assertTrue(result.isSuccess());
            verify(header, never()).extractFile(any(File.class));
        }
    }

    @Test
    void shouldRejectEntryOutsideDestination() throws Exception {
        final FileHeaderWrapper header = mock(FileHeaderWrapper.class);
        when(header.getDestinationFile(any(File.class))).thenReturn(tempDir.resolve("../outside.txt").toFile());
        final AutoCloseableIterator<FileHeaderWrapper> headers = iterator(header);

        try (MockedStatic<JunrarWrapper> junrar = mockStatic(JunrarWrapper.class)) {
            junrar.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(headers);

            final ExtractionResult result = new JunrarExtractor().extract(new File("archive.rar"), tempDir.toFile(),
                    ProgressListener.NONE);

            assertEquals(ExtractionResult.Status.EXECUTION_ERROR, result.status());
            verify(header, never()).extractFile(any(File.class));
        }
    }

    @Test
    void shouldClassifyRarFailureAsArchiveError() throws Exception {
        final FileHeaderWrapper header = mock(FileHeaderWrapper.class);
        final File targetFile = tempDir.resolve("file.txt").toFile();
        when(header.getDestinationFile(any(File.class))).thenReturn(targetFile);
        org.mockito.Mockito.doThrow(new RarException()).when(header).extractFile(targetFile);
        final AutoCloseableIterator<FileHeaderWrapper> headers = iterator(header);

        try (MockedStatic<JunrarWrapper> junrar = mockStatic(JunrarWrapper.class)) {
            junrar.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class))).thenReturn(headers);

            final ExtractionResult result = new JunrarExtractor().extract(new File("archive.rar"), tempDir.toFile(),
                    ProgressListener.NONE);

            assertFalse(result.isSuccess());
            assertEquals(ExtractionResult.Status.ARCHIVE_ERROR, result.status());
        }
    }

    @Test
    void shouldClassifyOpenFailureAsExecutionError() throws Exception {
        try (MockedStatic<JunrarWrapper> junrar = mockStatic(JunrarWrapper.class)) {
            junrar.when(() -> JunrarWrapper.getFileHeaderIterator(any(File.class)))
                    .thenThrow(new IOException("cannot open"));

            final ExtractionResult result = new JunrarExtractor().test(new File("archive.rar"),
                    ProgressListener.NONE);

            assertEquals(ExtractionResult.Status.EXECUTION_ERROR, result.status());
        }
    }

    @SuppressWarnings("unchecked")
    private AutoCloseableIterator<FileHeaderWrapper> iterator(final FileHeaderWrapper header) {
        final AutoCloseableIterator<FileHeaderWrapper> iterator = mock(AutoCloseableIterator.class);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(header);
        return iterator;
    }

    private List<ExtractionPhase> phases(final List<ExtractionProgress> progress) {
        return progress.stream().map(ExtractionProgress::phase).toList();
    }
}
