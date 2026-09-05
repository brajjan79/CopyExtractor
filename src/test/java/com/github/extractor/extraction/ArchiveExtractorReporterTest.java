package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

class ArchiveExtractorReporterTest {

    @Test
    void shouldPrintAvailableAndMissingExtractors() {
        final ArchiveExtractorDiscovery discovery = mock(ArchiveExtractorDiscovery.class);
        when(discovery.probe()).thenReturn(List.of(
                new ExtractorAvailability("UnRAR", true, "system PATH", "unrar"),
                new ExtractorAvailability("7-Zip", false, "not found", "-"),
                new ExtractorAvailability("Junrar", true, "built-in Java fallback", "classpath")));
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);

        new ArchiveExtractorReporter(discovery, output).printAvailableExtractors();

        final String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("Archive extractors:"));
        assertTrue(report.contains("available UnRAR"));
        assertTrue(report.contains("not found 7-Zip"));
        assertTrue(report.contains("available Junrar"));
    }

    @Test
    void shouldSupportDefaultReporter() {
        assertNotNull(new ArchiveExtractorReporter());
    }
}
