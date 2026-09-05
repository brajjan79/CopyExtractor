package com.github.extractor.extraction;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Archive extractor backed by the official UnRAR command-line application.
 */
public class UnrarCommandExtractor extends CommandArchiveExtractor {

    public UnrarCommandExtractor(final String executable) {
        super(executable);
    }

    UnrarCommandExtractor(final String executable, final CommandRunner commandRunner) {
        super(executable, commandRunner);
    }

    @Override
    public String getName() {
        return "UnRAR";
    }

    @Override
    protected List<String> buildAvailabilityCommand() {
        return List.of(getExecutable());
    }

    @Override
    protected List<String> buildTestCommand(final File archive) {
        return List.of(getExecutable(), "t", "-idq", "-y", archive.getAbsolutePath());
    }

    @Override
    protected List<String> buildExtractCommand(final File archive, final File destination) {
        return List.of(getExecutable(), "x", "-o-", "-idq", "-y", archive.getAbsolutePath(),
                destination.getAbsolutePath() + File.separator);
    }

    @Override
    protected boolean isCompatible(final CommandResult result) {
        final String output = result.output().toLowerCase(Locale.ROOT);
        return output.contains("alexander roshal") && output.contains("freeware");
    }
}
