package com.github.extractor.extraction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Base class for archive extractors backed by a command-line application.
 */
public abstract class CommandArchiveExtractor implements ArchiveExtractor {

    private final String executable;
    private final CommandRunner commandRunner;

    protected CommandArchiveExtractor(final String executable) {
        this(executable, new ProcessCommandRunner());
    }

    protected CommandArchiveExtractor(final String executable, final CommandRunner commandRunner) {
        this.executable = Objects.requireNonNull(executable);
        this.commandRunner = Objects.requireNonNull(commandRunner);
    }

    protected String getExecutable() {
        return executable;
    }

    protected abstract List<String> buildAvailabilityCommand();

    protected abstract List<String> buildTestCommand(File archive);

    protected abstract List<String> buildExtractCommand(File archive, File destination);

    @Override
    public boolean isAvailable() {
        try {
            return isCompatible(commandRunner.run(buildAvailabilityCommand()));
        } catch (final IOException e) {
            return false;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    protected boolean isCompatible(final CommandResult result) {
        return true;
    }

    @Override
    public ExtractionResult test(final File archive, final ProgressListener progressListener) {
        Objects.requireNonNull(archive);
        Objects.requireNonNull(progressListener);
        return run(buildTestCommand(archive), progressListener, ExtractionPhase.TESTING,
                "Testing archive with " + getName() + "...");
    }

    @Override
    public ExtractionResult extract(final File archive, final File destination,
            final ProgressListener progressListener) {
        Objects.requireNonNull(archive);
        Objects.requireNonNull(destination);
        Objects.requireNonNull(progressListener);
        return run(buildExtractCommand(archive, destination), progressListener, ExtractionPhase.EXTRACTING,
                "Extracting with " + getName() + "...");
    }

    private ExtractionResult run(final List<String> command, final ProgressListener progressListener,
            final ExtractionPhase phase, final String startMessage) {
        progressListener.onProgress(ExtractionProgress.phase(phase, startMessage));
        try {
            final ExtractionResult result = ExtractionResult.fromCommand(commandRunner.run(command));
            final ExtractionPhase resultPhase = result.isSuccess() ? ExtractionPhase.COMPLETED : ExtractionPhase.FAILED;
            final String resultMessage = result.isSuccess() ? "Archive operation completed."
                    : "Archive operation failed with exit code " + result.exitCode() + ".";
            progressListener.onProgress(ExtractionProgress.phase(resultPhase, resultMessage));
            return result;
        } catch (final IOException e) {
            return executionError(progressListener, e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return executionError(progressListener, e);
        }
    }

    private ExtractionResult executionError(final ProgressListener progressListener, final Exception exception) {
        progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.FAILED,
                "Could not run " + getName() + ": " + exception.getMessage()));
        return ExtractionResult.executionError(exception);
    }
}
