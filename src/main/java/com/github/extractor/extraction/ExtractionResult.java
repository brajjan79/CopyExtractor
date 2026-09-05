package com.github.extractor.extraction;

/**
 * Result from invoking an archive extractor.
 */
public record ExtractionResult(Status status, int exitCode, String output, Throwable cause) {

    public enum Status {
        SUCCESS,
        ARCHIVE_ERROR,
        EXECUTION_ERROR
    }

    public static ExtractionResult fromCommand(final CommandResult result) {
        final Status status = result.exitCode() == 0 ? Status.SUCCESS : Status.ARCHIVE_ERROR;
        return new ExtractionResult(status, result.exitCode(), result.output(), null);
    }

    public static ExtractionResult executionError(final Throwable cause) {
        return new ExtractionResult(Status.EXECUTION_ERROR, -1, "", cause);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
