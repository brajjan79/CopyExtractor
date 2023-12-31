package com.github.extractor.models;

import com.github.extractor.exceptions.ConfigurationException;

import java.io.File;

public class ConfigFolder {

    protected final File inputFolder;
    protected final File outputFolder;

    public ConfigFolder(final String inputFolder, final String outputFolder) {
        if (inputFolder == null) {
            throw new ConfigurationException("No inputFolder configuration provided.");
        }
        if (outputFolder == null) {
            throw new ConfigurationException("No outputFolder configuration provided.");
        }
        this.inputFolder = new File(inputFolder);
        this.outputFolder = new File(outputFolder);
    }

    public File getInputFolder() {
        return inputFolder;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    @Override
    public String toString() {
        return String.format("{inputFolder: %s, outputFolder: %s}", inputFolder, outputFolder);
    }
}
