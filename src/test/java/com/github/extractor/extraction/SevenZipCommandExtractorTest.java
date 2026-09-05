package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class SevenZipCommandExtractorTest {

    @Test
    void shouldExposeExtractorName() {
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("7z",
                command -> new CommandResult(0, ""));

        assertEquals("7-Zip", extractor.getName());
    }

    @Test
    void shouldSupportDefaultProcessRunner() {
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("7z");

        assertEquals("7-Zip", extractor.getName());
    }

    @Test
    void shouldReportUnavailableWhenExecutableCannotBeStarted() {
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("missing", command -> {
            throw new IOException("not found");
        });

        assertFalse(extractor.isAvailable());
    }

    @Test
    void shouldBuildTestCommand() {
        final RecordingCommandRunner runner = new RecordingCommandRunner(new CommandResult(0, "Everything is Ok"));
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("7z", runner);
        final File archive = new File("archive.rar");

        final ExtractionResult result = extractor.test(archive);

        assertTrue(result.isSuccess());
        assertEquals(List.of("7z", "t", "-y", archive.getAbsolutePath()), runner.lastCommand);
    }

    @Test
    void shouldBuildExtractCommand() {
        final RecordingCommandRunner runner = new RecordingCommandRunner(new CommandResult(0, "Everything is Ok"));
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("7z", runner);
        final File archive = new File("archive.rar");
        final File destination = new File("output");

        final ExtractionResult result = extractor.extract(archive, destination);

        assertTrue(result.isSuccess());
        assertEquals(List.of("7z", "x", "-y", "-aos", "-o" + destination.getAbsolutePath(),
                archive.getAbsolutePath()), runner.lastCommand);
    }

    @Test
    void shouldReturnExecutionErrorWhenCommandFailsToStart() {
        final IOException failure = new IOException("failed to start");
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("7z", command -> {
            throw failure;
        });
        final List<ExtractionProgress> progressEvents = new ArrayList<>();

        final ExtractionResult result = extractor.test(new File("archive.rar"), progressEvents::add);

        assertFalse(result.isSuccess());
        assertEquals(ExtractionResult.Status.EXECUTION_ERROR, result.status());
        assertEquals(-1, result.exitCode());
        assertEquals(failure, result.cause());
        assertEquals(List.of(ExtractionPhase.TESTING, ExtractionPhase.FAILED),
                progressEvents.stream().map(ExtractionProgress::phase).toList());
    }

    @Test
    void shouldRestoreInterruptWhenExtractionIsInterrupted() {
        final InterruptedException failure = new InterruptedException("interrupted");
        final SevenZipCommandExtractor extractor = new SevenZipCommandExtractor("7z", command -> {
            throw failure;
        });

        try {
            final ExtractionResult result = extractor.extract(new File("archive.rar"), new File("output"));

            assertEquals(ExtractionResult.Status.EXECUTION_ERROR, result.status());
            assertEquals(failure, result.cause());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
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
