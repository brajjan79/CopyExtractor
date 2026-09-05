package com.github.extractor.extraction;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface CommandRunner {

    CommandResult run(List<String> command) throws IOException, InterruptedException;
}
