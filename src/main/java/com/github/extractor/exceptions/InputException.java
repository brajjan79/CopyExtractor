package com.github.extractor.exceptions;

public class InputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InputException(final String errorMessage) {
        super(errorMessage);
    }

}
