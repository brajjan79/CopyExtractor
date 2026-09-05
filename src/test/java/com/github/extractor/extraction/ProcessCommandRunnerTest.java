package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProcessCommandRunnerTest {

    @Test
    void shouldCaptureOutputAndExitCode() throws Exception {
        final List<String> command = isWindows()
                ? List.of("cmd", "/c", "echo command-output")
                : List.of("sh", "-c", "printf command-output");

        final CommandResult result = new ProcessCommandRunner().run(command);

        assertEquals(0, result.exitCode());
        assertTrue(result.output().contains("command-output"));
    }

    @Test
    void shouldLimitCapturedOutputWithoutBlockingTheProcess() throws Exception {
        final List<String> command = isWindows()
                ? List.of("powershell", "-NoProfile", "-Command", "[Console]::Out.Write('x' * 70000)")
                : List.of("sh", "-c", "yes x | head -c 70000");

        final CommandResult result = new ProcessCommandRunner().run(command);

        assertEquals(0, result.exitCode());
        assertTrue(result.output().endsWith("[output truncated]"));
        assertTrue(result.output().length() < 70000);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
