package com.github.extractor.extraction;

import java.io.PrintStream;
import java.util.List;

public class ArchiveExtractorReporter {

    private final ArchiveExtractorDiscovery discovery;
    private final PrintStream output;

    public ArchiveExtractorReporter() {
        this(new ArchiveExtractorDiscovery(), System.out);
    }

    ArchiveExtractorReporter(final ArchiveExtractorDiscovery discovery, final PrintStream output) {
        this.discovery = discovery;
        this.output = output;
    }

    public void printAvailableExtractors() {
        final List<ExtractorAvailability> extractors = discovery.probe();
        output.println("Archive extractors:");
        for (final ExtractorAvailability extractor : extractors) {
            final String status = extractor.available() ? "available" : "not found";
            output.printf("  %-8s %-10s %s (%s)%n", status, extractor.name(), extractor.source(),
                    extractor.executable());
        }
    }
}
