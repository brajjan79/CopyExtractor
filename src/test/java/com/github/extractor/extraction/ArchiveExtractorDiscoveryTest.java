package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class ArchiveExtractorDiscoveryTest {

    @Test
    void shouldReportAvailablePathCommandsOnWindows() {
        final ArchiveExtractorDiscovery discovery = new ArchiveExtractorDiscovery("Windows 11",
                factory(true, "UnRAR"), factory(true, "7-Zip"));

        final List<ExtractorAvailability> result = discovery.probe();

        assertEquals(3, result.size());
        assertEquals("UnRAR", result.get(0).name());
        assertEquals("unrar.exe", result.get(0).executable());
        assertTrue(result.get(0).available());
        assertEquals("7z.exe", result.get(1).executable());
        assertTrue(result.get(2).available());
        assertEquals("built-in Java fallback", result.get(2).source());
    }

    @Test
    void shouldTryLinuxSevenZipAliasesAndReportMissingPrograms() {
        final Function<String, ArchiveExtractor> unavailable = factory(false, "missing");
        final Function<String, ArchiveExtractor> sevenZip = executable -> extractor("7z".equals(executable), "7-Zip");
        final ArchiveExtractorDiscovery discovery = new ArchiveExtractorDiscovery("Linux", unavailable, sevenZip);

        final List<ExtractorAvailability> result = discovery.probe();

        assertFalse(result.get(0).available());
        assertEquals("not found", result.get(0).source());
        assertTrue(result.get(1).available());
        assertEquals("7z", result.get(1).executable());
    }

    @Test
    void shouldReturnAvailableExtractorsInPriorityOrder() {
        final ArchiveExtractorDiscovery discovery = new ArchiveExtractorDiscovery("Ubuntu Linux",
                factory(true, "UnRAR"), factory(false, "7-Zip"));

        final List<ArchiveExtractor> result = discovery.availableExtractors();

        assertEquals(2, result.size());
        assertEquals("UnRAR", result.get(0).getName());
        assertEquals("Junrar", result.get(1).getName());
    }

    @Test
    void shouldSupportDefaultEnvironmentDiscovery() {
        final ArchiveExtractorDiscovery discovery = new ArchiveExtractorDiscovery();

        assertTrue(discovery.probe().stream().anyMatch(item -> item.name().equals("Junrar") && item.available()));
    }

    private Function<String, ArchiveExtractor> factory(final boolean available, final String name) {
        return executable -> extractor(available, name);
    }

    private ArchiveExtractor extractor(final boolean available, final String name) {
        final ArchiveExtractor extractor = mock(ArchiveExtractor.class);
        when(extractor.isAvailable()).thenReturn(available);
        when(extractor.getName()).thenReturn(name);
        return extractor;
    }
}
