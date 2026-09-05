package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class UnrarCommandExtractorTest {

    @Test
    void shouldExposeNameAndSupportDefaultProcessRunner() {
        final UnrarCommandExtractor extractor = new UnrarCommandExtractor("unrar");

        assertEquals("UnRAR", extractor.getName());
    }

    @Test
    void shouldReportAvailableWhenExecutableCanBeStarted() {
        final RecordingCommandRunner runner = new RecordingCommandRunner(
                new CommandResult(7, "UNRAR freeware Copyright Alexander Roshal"));
        final UnrarCommandExtractor extractor = new UnrarCommandExtractor("unrar", runner);

        assertTrue(extractor.isAvailable());
        assertEquals(List.of("unrar"), runner.lastCommand);
    }

    @Test
    void shouldRejectUnrarFreeAsIncompatible() {
        final RecordingCommandRunner runner = new RecordingCommandRunner(
                new CommandResult(0, "unrar 0.0.2 Copyright Ben Asselstine, Jeroen Dekkers"));

        assertFalse(new UnrarCommandExtractor("unrar", runner).isAvailable());
    }

    @Test
    void shouldReportUnavailableWhenExecutableCannotBeStarted() {
        final UnrarCommandExtractor extractor = new UnrarCommandExtractor("missing", command -> {
            throw new IOException("not found");
        });

        assertFalse(extractor.isAvailable());
    }

    @Test
    void shouldRestoreInterruptWhenAvailabilityCheckIsInterrupted() {
        final UnrarCommandExtractor extractor = new UnrarCommandExtractor("unrar", command -> {
            throw new InterruptedException("interrupted");
        });

        try {
            assertFalse(extractor.isAvailable());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void shouldBuildTestCommandAndReturnSuccess() {
        final RecordingCommandRunner runner = new RecordingCommandRunner(new CommandResult(0, "All OK"));
        final UnrarCommandExtractor extractor = new UnrarCommandExtractor("unrar", runner);
        final File archive = new File("archive.rar");
        final List<ExtractionProgress> progressEvents = new ArrayList<>();

        final ExtractionResult result = extractor.test(archive, progressEvents::add);

        assertTrue(result.isSuccess());
        assertEquals(List.of("unrar", "t", "-idq", "-y", archive.getAbsolutePath()), runner.lastCommand);
        assertEquals("All OK", result.output());
        assertEquals(List.of(ExtractionPhase.TESTING, ExtractionPhase.COMPLETED),
                progressEvents.stream().map(ExtractionProgress::phase).toList());
    }

    @Test
    void shouldBuildExtractCommandAndPreserveArchiveError() {
        final RecordingCommandRunner runner = new RecordingCommandRunner(new CommandResult(3, "CRC failed"));
        final UnrarCommandExtractor extractor = new UnrarCommandExtractor("unrar", runner);
        final File archive = new File("archive.rar");
        final File destination = new File("output");
        final List<ExtractionProgress> progressEvents = new ArrayList<>();

        final ExtractionResult result = extractor.extract(archive, destination, progressEvents::add);

        assertEquals(ExtractionResult.Status.ARCHIVE_ERROR, result.status());
        assertEquals(3, result.exitCode());
        assertEquals(List.of("unrar", "x", "-o-", "-idq", "-y", archive.getAbsolutePath(),
                destination.getAbsolutePath() + File.separator), runner.lastCommand);
        assertEquals(List.of(ExtractionPhase.EXTRACTING, ExtractionPhase.FAILED),
                progressEvents.stream().map(ExtractionProgress::phase).toList());
    }

    private static class RecordingCommandRunner implements CommandRunner {

        private final CommandResult result;
        private List<String> lastCommand = new ArrayList<>();

        RecordingCommandRunner(final CommandResult result) {
            this.result = result;
        }

        @Override
        public CommandResult run(final List<String> command) {
            lastCommand = command;
            return result;
        }
    }
}
