package com.github.extractor.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestStateConstants {


    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        StateConstants.reset();
    }

    @Test
    void printShouldShowTotalItemCount() {
        System.setOut(new PrintStream(outputStreamCaptor));
        // Given
        StateConstants.addSuccess();
        StateConstants.addAlreadyExists();
        StateConstants.addFailure();

        // When
        StateConstants.print();

        // Then
        final String expectedOutput = "Total number of item processed 3";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedOutput));
        System.setOut(standardOut);
    }

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
