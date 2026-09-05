package com.github.extractor.extraction;

/**
 * Exit code and combined output from an external command.
 */
public record CommandResult(int exitCode, String output) {
}
