package com.github.extractor.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestState {

    @Test
    public void testState() {
        State.addAlreadyExists();
        State.addFailure();
        State.addSuccess();

        // assertEquals("AlreadyExists should be 1", 1, State.getAlreadyExists());
        // assertEquals("Failures should be 1", 1, State.getFailures());
        // assertEquals("Successfull should be 1", 1, State.getSuccessfull());
        // assertEquals("Total should be 3", 3, State.getTotal());

    }

}
