package com.github.extractor.extraction;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Runs a command directly without an intermediate shell.
 */
public class ProcessCommandRunner implements CommandRunner {

    private static final int MAX_CAPTURED_OUTPUT_BYTES = 64 * 1024;

    @Override
    public CommandResult run(final List<String> command) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        final Process process = processBuilder.start();
        process.getOutputStream().close();
        final String output;
        try (var input = process.getInputStream()) {
            final ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
            final byte[] buffer = new byte[8192];
            int bytesRead;
            boolean truncated = false;
            while ((bytesRead = input.read(buffer)) != -1) {
                final int remainingCapacity = MAX_CAPTURED_OUTPUT_BYTES - capturedOutput.size();
                if (remainingCapacity > 0) {
                    capturedOutput.write(buffer, 0, Math.min(bytesRead, remainingCapacity));
                }
                truncated |= bytesRead > remainingCapacity;
            }
            output = new String(capturedOutput.toByteArray(), Charset.defaultCharset())
                    + (truncated ? System.lineSeparator() + "[output truncated]" : "");
        }
        final int exitCode = process.waitFor();
        return new CommandResult(exitCode, output);
    }
}
