package com.github.extractor.extraction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

/**
 * Detects built-in and system-installed archive extractors.
 */
public class ArchiveExtractorDiscovery {

    private final String osName;
    private final Function<String, ArchiveExtractor> unrarFactory;
    private final Function<String, ArchiveExtractor> sevenZipFactory;

    public ArchiveExtractorDiscovery() {
        this(System.getProperty("os.name"), UnrarCommandExtractor::new, SevenZipCommandExtractor::new);
    }

    ArchiveExtractorDiscovery(final String osName, final Function<String, ArchiveExtractor> unrarFactory,
            final Function<String, ArchiveExtractor> sevenZipFactory) {
        this.osName = osName.toLowerCase(Locale.ROOT);
        this.unrarFactory = unrarFactory;
        this.sevenZipFactory = sevenZipFactory;
    }

    public List<ExtractorAvailability> probe() {
        final List<ExtractorAvailability> result = new ArrayList<>();
        result.add(probeCandidates("UnRAR", unrarCandidates(), unrarFactory));
        result.add(probeCandidates("7-Zip", sevenZipCandidates(), sevenZipFactory));
        result.add(new ExtractorAvailability("Junrar", true, "built-in Java fallback", "classpath"));
        return List.copyOf(result);
    }

    public List<ArchiveExtractor> availableExtractors() {
        final List<ArchiveExtractor> result = new ArrayList<>();
        addFirstAvailable(result, unrarCandidates(), unrarFactory);
        addFirstAvailable(result, sevenZipCandidates(), sevenZipFactory);
        result.add(new JunrarExtractor());
        return List.copyOf(result);
    }

    private ExtractorAvailability probeCandidates(final String name, final List<Candidate> candidates,
            final Function<String, ArchiveExtractor> factory) {
        for (final Candidate candidate : candidates) {
            if (candidate.requiresExistingFile() && !Files.isRegularFile(Path.of(candidate.executable()))) {
                continue;
            }
            if (factory.apply(candidate.executable()).isAvailable()) {
                return new ExtractorAvailability(name, true, candidate.source(), candidate.executable());
            }
        }
        return new ExtractorAvailability(name, false, "not found", "-");
    }

    private void addFirstAvailable(final List<ArchiveExtractor> result, final List<Candidate> candidates,
            final Function<String, ArchiveExtractor> factory) {
        for (final Candidate candidate : candidates) {
            if (candidate.requiresExistingFile() && !Files.isRegularFile(Path.of(candidate.executable()))) {
                continue;
            }
            final ArchiveExtractor extractor = factory.apply(candidate.executable());
            if (extractor.isAvailable()) {
                result.add(extractor);
                return;
            }
        }
    }

    private List<Candidate> unrarCandidates() {
        final Set<Candidate> candidates = new LinkedHashSet<>();
        candidates.add(new Candidate(isWindows() ? "unrar.exe" : "unrar", "system PATH", false));
        if (isWindows()) {
            addWindowsInstallation(candidates, "ProgramFiles", "WinRAR", "UnRAR.exe");
            addWindowsInstallation(candidates, "ProgramFiles(x86)", "WinRAR", "UnRAR.exe");
        }
        return List.copyOf(candidates);
    }

    private List<Candidate> sevenZipCandidates() {
        final Set<Candidate> candidates = new LinkedHashSet<>();
        if (isWindows()) {
            candidates.add(new Candidate("7z.exe", "system PATH", false));
            addWindowsInstallation(candidates, "ProgramFiles", "7-Zip", "7z.exe");
            addWindowsInstallation(candidates, "ProgramFiles(x86)", "7-Zip", "7z.exe");
        } else {
            candidates.add(new Candidate("7zz", "system PATH", false));
            candidates.add(new Candidate("7z", "system PATH", false));
            candidates.add(new Candidate("7za", "system PATH", false));
        }
        return List.copyOf(candidates);
    }

    private void addWindowsInstallation(final Set<Candidate> candidates, final String environmentVariable,
            final String directory, final String executable) {
        final String baseDirectory = System.getenv(environmentVariable);
        if (baseDirectory != null && !baseDirectory.isBlank()) {
            final String path = Path.of(baseDirectory, directory, executable).toString();
            candidates.add(new Candidate(path, environmentVariable, true));
        }
    }

    private boolean isWindows() {
        return osName.contains("win");
    }

    private record Candidate(String executable, String source, boolean requiresExistingFile) {
    }
}
