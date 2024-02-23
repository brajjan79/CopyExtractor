package com.github.extractor.configuration;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import com.github.extractor.exceptions.ArgsExitGivenException;
import com.github.extractor.exceptions.InputException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CliTest {

    private static final String FULL_NAME_DASHES = "--";
    private static final String SHORT_NAME_DASHES = "-";

    @Test
    public void testConstructor() {
        try {
            new Cli();
        } catch (final Exception e) {
            fail("Failed to initiate Cli");
        }
    }

    @Test
    public void testHelpOptionProvided() throws Throwable {
        final String[] args = {FULL_NAME_DASHES + CliKeys.HELP.name};
        assertThrows(ArgsExitGivenException.class, () -> {
            Cli.parseArgs(args);
        });
    }

    @Test
    public void testShortHelpOptionProvided() throws Throwable {
        final String[] args = {SHORT_NAME_DASHES + CliKeys.HELP.shortName};
        assertThrows(ArgsExitGivenException.class, () -> {
            Cli.parseArgs(args);
        });
    }

    @Test
    public void testNoArgsProvided() throws Throwable {
        final String[] args = {};
        assertThrows(InputException.class, () -> {
            Cli.parseArgs(args);
        });
    }

    @Test
    public void testInvalidArgsProvided() throws Throwable {
        final String[] args = {"--invalid"};
        assertThrows(InputException.class, () -> {
            Cli.parseArgs(args);
        });

    }

    @Test
    public void testConfigFilePathProvided() throws Throwable {
        final String json = String.format("{\"%s\":\"%s\"}", CliKeys.CONFIG_FILE_PATH.name, "/some/path");
        final JsonObject expectedJson = (JsonObject) JsonParser.parseString(json);

        final String[] args = {FULL_NAME_DASHES + CliKeys.CONFIG_FILE_PATH.name, "/some/path"};
        final JsonObject inputConfig = Cli.parseArgs(args);

        assertTrue(expectedJson.equals(inputConfig));
    }

    @Test
    public void testConfigFilePathProvidedShortName() throws Throwable {
        final String json = String.format("{\"%s\":\"%s\"}", CliKeys.CONFIG_FILE_PATH.name, "/some/path");
        final JsonObject expectedJson = (JsonObject) JsonParser.parseString(json);

        final String[] args = {SHORT_NAME_DASHES + CliKeys.CONFIG_FILE_PATH.shortName, "/some/path"};
        final JsonObject inputConfig = Cli.parseArgs(args);

        assertTrue(expectedJson.equals(inputConfig));
    }

    @Test
    public void testSourceAndTargetProvidedEtc() throws Throwable {
        final JsonObject expectedJson = getExpectedJson();

        final String[] args = {
                FULL_NAME_DASHES + CliKeys.SOURCE_FOLDER.name, "/some/path",
                FULL_NAME_DASHES + CliKeys.TARGET_FOLDER.name, "/some/other/path",
                FULL_NAME_DASHES + CliKeys.FILE_TYPES.name, "jpg,png",
                FULL_NAME_DASHES + CliKeys.INCLUDE_FOLDERS.name, "n**es",
                FULL_NAME_DASHES + CliKeys.IGNORE.name, "test",
                FULL_NAME_DASHES + CliKeys.GROUP_BY_REGEX.name, "abc",
                FULL_NAME_DASHES + CliKeys.RECURSIVE.name,
                FULL_NAME_DASHES + CliKeys.KEEP_FOLDER.name,
                FULL_NAME_DASHES + CliKeys.CREATE_FOLDER.name,
                FULL_NAME_DASHES + CliKeys.KEEP_FOLDER_STRUCTURE.name,
                FULL_NAME_DASHES + CliKeys.DRY_RUN.name
        };
        final JsonObject inputConfig = Cli.parseArgs(args);

        assertTrue(expectedJson.equals(inputConfig));
    }

    @Test
    public void testSourceAndTargetProvidedEtcShortNames() throws Throwable {
        final JsonObject expectedJson = getExpectedJson();

        final String[] args = {
                SHORT_NAME_DASHES + CliKeys.SOURCE_FOLDER.shortName, "/some/path",
                SHORT_NAME_DASHES + CliKeys.TARGET_FOLDER.shortName, "/some/other/path",
                SHORT_NAME_DASHES + CliKeys.FILE_TYPES.shortName, "jpg,png",
                SHORT_NAME_DASHES + CliKeys.INCLUDE_FOLDERS.shortName, "n**es",
                SHORT_NAME_DASHES + CliKeys.IGNORE.shortName, "test",
                SHORT_NAME_DASHES + CliKeys.GROUP_BY_REGEX.shortName, "abc",
                SHORT_NAME_DASHES + CliKeys.RECURSIVE.shortName,
                SHORT_NAME_DASHES + CliKeys.KEEP_FOLDER.shortName,
                SHORT_NAME_DASHES + CliKeys.CREATE_FOLDER.shortName,
                SHORT_NAME_DASHES + CliKeys.KEEP_FOLDER_STRUCTURE.shortName,
                SHORT_NAME_DASHES + CliKeys.DRY_RUN.shortName
        };
        final JsonObject inputConfig = Cli.parseArgs(args);

        assertTrue(expectedJson.equals(inputConfig));
    }

    private JsonObject getExpectedJson() {
        final JsonObject expectedJson = new JsonObject();
        expectedJson.addProperty(CliKeys.SOURCE_FOLDER.name, "/some/path");
        expectedJson.addProperty(CliKeys.TARGET_FOLDER.name, "/some/other/path");
        expectedJson.addProperty(CliKeys.FILE_TYPES.name, "jpg,png");
        expectedJson.addProperty(CliKeys.INCLUDE_FOLDERS.name, "n**es");
        expectedJson.addProperty(CliKeys.IGNORE.name, "test");
        expectedJson.addProperty(CliKeys.GROUP_BY_REGEX.name, "abc");
        expectedJson.add(CliKeys.RECURSIVE.name, null);
        expectedJson.add(CliKeys.RECURSIVE.name, null);
        expectedJson.add(CliKeys.KEEP_FOLDER.name, null);
        expectedJson.add(CliKeys.CREATE_FOLDER.name, null);
        expectedJson.add(CliKeys.KEEP_FOLDER_STRUCTURE.name, null);
        expectedJson.add(CliKeys.DRY_RUN.name, null);
        return expectedJson;
    }

}
