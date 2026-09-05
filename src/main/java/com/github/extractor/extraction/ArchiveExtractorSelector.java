package com.github.extractor.extraction;

import java.util.List;
import java.util.Locale;

import com.github.extractor.exceptions.ConfigurationException;

/** Selects an available archive extractor from configuration. */
public class ArchiveExtractorSelector {

    private final ArchiveExtractorDiscovery discovery;

    public ArchiveExtractorSelector() {
        this(new ArchiveExtractorDiscovery());
    }

    ArchiveExtractorSelector(final ArchiveExtractorDiscovery discovery) {
        this.discovery = discovery;
    }

    public ArchiveExtractor select(final String requestedExtractor) {
        final String requested = normalize(requestedExtractor);
        final List<ArchiveExtractor> available = discovery.availableExtractors();
        if ("auto".equals(requested)) {
            return available.get(0);
        }

        return available.stream()
                .filter(extractor -> normalize(extractor.getName()).equals(requested))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException(
                        "Archive extractor '" + requestedExtractor + "' is not available. "
                                + "Use --list-extractors to see available alternatives."));
    }

    private String normalize(final String value) {
        if (value == null || value.isBlank()) {
            return "auto";
        }
        final String normalized = value.trim().toLowerCase(Locale.ROOT).replace("-", "");
        return switch (normalized) {
            case "7zip", "sevenzip" -> "7z";
            default -> normalized;
        };
    }
}
