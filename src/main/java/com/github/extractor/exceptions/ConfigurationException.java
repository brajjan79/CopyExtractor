package com.github.extractor.exceptions;

public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConfigurationException(final String errorMessage) {
        super(errorMessage);
    }

}
