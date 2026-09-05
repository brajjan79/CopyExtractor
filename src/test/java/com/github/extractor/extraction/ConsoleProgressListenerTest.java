package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ConsoleProgressListenerTest {

    @Test
    void shouldRenderActivityUpdatesAndCompletion() throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ConsoleProgressListener listener = new ConsoleProgressListener(
                new PrintStream(bytes, true, StandardCharsets.UTF_8))) {
            listener.onProgress(ExtractionProgress.phase(ExtractionPhase.TESTING, "Testing archive..."));
            listener.onProgress(ExtractionProgress.phase(ExtractionPhase.EXTRACTING, "Extracting archive..."));
            Thread.sleep(1100);
            listener.onProgress(ExtractionProgress.phase(ExtractionPhase.COMPLETED, "Extraction completed."));
        }

        final String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Testing archive..."));
        assertTrue(output.contains("Extracting archive..."));
        assertTrue(output.contains("00:01"));
        assertTrue(output.contains("Extraction completed."));
    }

    @Test
    void shouldRenderFailureWithoutAnActiveOperation() {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ConsoleProgressListener listener = new ConsoleProgressListener(
                new PrintStream(bytes, true, StandardCharsets.UTF_8))) {
            listener.onProgress(ExtractionProgress.phase(ExtractionPhase.FAILED, "Extraction failed."));
        }

        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("Extraction failed."));
    }

    @Test
    void shouldSupportDefaultOutput() {
        try (ConsoleProgressListener listener = new ConsoleProgressListener()) {
            listener.close();
        }
    }
}
