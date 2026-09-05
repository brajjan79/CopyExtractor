package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.extractor.exceptions.ConfigurationException;

class ArchiveExtractorSelectorTest {

    @Test
    void shouldSelectFirstAvailableExtractorForAuto() {
        final ArchiveExtractorDiscovery discovery = mock(ArchiveExtractorDiscovery.class);
        final ArchiveExtractor unrar = extractor("UnRAR");
        final ArchiveExtractor junrar = extractor("Junrar");
        when(discovery.availableExtractors()).thenReturn(List.of(unrar, junrar));

        assertSame(unrar, new ArchiveExtractorSelector(discovery).select("auto"));
    }

    @Test
    void shouldSelectRequestedExtractorAndAcceptAliases() {
        final ArchiveExtractorDiscovery discovery = mock(ArchiveExtractorDiscovery.class);
        final ArchiveExtractor sevenZip = extractor("7-Zip");
        final ArchiveExtractor junrar = extractor("Junrar");
        when(discovery.availableExtractors()).thenReturn(List.of(sevenZip, junrar));

        assertSame(sevenZip, new ArchiveExtractorSelector(discovery).select("sevenzip"));
    }

    @Test
    void shouldUseAutoForBlankConfiguration() {
        final ArchiveExtractorDiscovery discovery = mock(ArchiveExtractorDiscovery.class);
        final ArchiveExtractor junrar = extractor("Junrar");
        when(discovery.availableExtractors()).thenReturn(List.of(junrar));

        assertSame(junrar, new ArchiveExtractorSelector(discovery).select(null));
    }

    @Test
    void shouldRejectUnavailableOrUnknownExtractor() {
        final ArchiveExtractorDiscovery discovery = mock(ArchiveExtractorDiscovery.class);
        final ArchiveExtractor junrar = extractor("Junrar");
        when(discovery.availableExtractors()).thenReturn(List.of(junrar));

        assertThrows(ConfigurationException.class,
                () -> new ArchiveExtractorSelector(discovery).select("unrar"));
    }

    private ArchiveExtractor extractor(final String name) {
        final ArchiveExtractor extractor = mock(ArchiveExtractor.class);
        when(extractor.getName()).thenReturn(name);
        return extractor;
    }
}
