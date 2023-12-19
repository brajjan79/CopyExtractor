package com.github.extractor.models;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class TestStateConstants {

    @Test
    public void testStateConstants() {
        StateConstants.addAlreadyExists();
        StateConstants.addFailure();
        StateConstants.addSuccess();

        assertEquals("AlreadyExists should be 1", 1, StateConstants.getAlreadyExists());
        assertEquals("Failures should be 1", 1, StateConstants.getFailures());
        assertEquals("Successfull should be 1", 1, StateConstants.getSuccessfull());
        assertEquals("Total should be 3", 3, StateConstants.getTotal());

    }

}
