package com.github.extractor.models;

public class StateConstants {

    private static int successfull = 0;
    private static int alreadyExists = 0;
    private static int failures = 0;

    private StateConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void addSuccess() {
        successfull += 1;
    }

    public static void addAlreadyExists() {
        alreadyExists += 1;
    }

    public static void addFailure() {
        failures += 1;
    }

    public static int getTotal() {
        return successfull + alreadyExists + failures;
    }

    public static int getAlreadyExists() {
        return alreadyExists;
    }

    public static int getFailures() {
        return failures;
    }

    public static int getSuccessfull() {
        return successfull;
    }

    public static void reset() {
        successfull = 0;
        alreadyExists = 0;
        failures = 0;
    }

    public static void print() {
        System.out.println(String.format("Total number of item processed %d", getTotal()));
    }

}
