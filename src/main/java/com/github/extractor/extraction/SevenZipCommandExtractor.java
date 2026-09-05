package com.github.extractor.extraction;

import java.io.File;
import java.util.List;

/**
 * Archive extractor backed by the 7-Zip command-line application.
 */
public class SevenZipCommandExtractor extends CommandArchiveExtractor {

    public SevenZipCommandExtractor(final String executable) {
        super(executable);
    }

    SevenZipCommandExtractor(final String executable, final CommandRunner commandRunner) {
        super(executable, commandRunner);
    }

    @Override
    public String getName() {
        return "7-Zip";
    }

    @Override
    protected List<String> buildAvailabilityCommand() {
        return List.of(getExecutable());
    }

    @Override
    protected List<String> buildTestCommand(final File archive) {
        return List.of(getExecutable(), "t", "-y", archive.getAbsolutePath());
    }

    @Override
    protected List<String> buildExtractCommand(final File archive, final File destination) {
        return List.of(getExecutable(), "x", "-y", "-aos", "-o" + destination.getAbsolutePath(),
                archive.getAbsolutePath());
    }
}
