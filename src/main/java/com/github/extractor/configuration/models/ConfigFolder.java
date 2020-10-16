package com.github.extractor.configuration.models;

import com.github.extractor.exceptions.ConfigurationException;

public class ConfigFolder {

	private final String inputFolder;
	private final String outputFolder;

	public ConfigFolder(final String inputFolder, final String outputFolder) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    public String getInputFolder() {
        if (inputFolder == null)
            throw new ConfigurationException("No inputFolder configuration provided.");
        return inputFolder;
    }

    public String getOutputFolder() {
        if (outputFolder == null)
            throw new ConfigurationException("No outputFolder configuration provided.");
        return outputFolder;
    }

    @Override
    public String toString() {
	    return String.format("{inputFolder: %s, outputFolder: %s}", inputFolder, outputFolder);
	}
}
