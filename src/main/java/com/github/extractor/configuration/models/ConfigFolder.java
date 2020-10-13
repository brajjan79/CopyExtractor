package com.github.extractor.configuration.models;

public class ConfigFolder {

	private final String inputFolder;
	private final String outputFolder;

	public ConfigFolder(final String inputFolder, final String outputFolder) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    @Override
    public String toString() {
	    return String.format("{inputFolder: %s, outputFolder: %s}", inputFolder, outputFolder);
	}
}
