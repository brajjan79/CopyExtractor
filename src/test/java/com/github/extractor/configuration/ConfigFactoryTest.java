package com.github.extractor.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigFactory.class, ConfigFileUtil.class })
public class ConfigFactoryTest {

    @Test
    public void testConstructor() throws Throwable {
        try {
            new ConfigFactory();
        } catch (final Exception e) {
            fail("Failed to initiate ConfigFactory");
        }
    }

    @Test
    public void testCreateFromInputargs() throws Throwable {
        final JsonObject inputArgs = new JsonObject();
        inputArgs.addProperty(CliKeys.SOURCE_FOLDER.name, "/some/path");
        inputArgs.addProperty(CliKeys.TARGET_FOLDER.name, "/some/other/path");
        inputArgs.addProperty(CliKeys.FILE_TYPES.name, "jpg,png");
        inputArgs.addProperty(CliKeys.INCLUDE_FOLDERS.name, "n**es, ,,,test");
        inputArgs.addProperty(CliKeys.GROUP_BY_REGEX.name, "abc");
        inputArgs.add(CliKeys.RECURSIVE.name, null);
        inputArgs.add(CliKeys.KEEP_FOLDER.name, null);
        inputArgs.add(CliKeys.KEEP_FOLDER_STRUCTURE.name, null);
        inputArgs.add(CliKeys.DRY_RUN.name, null);

        final Configuration config = ConfigFactory.createFromInputArgs(inputArgs);

        assertEquals(1, config.getFolders().size());
        assertEquals(2, config.getFileTypes().size());
        assertEquals(0, config.getIgnored().size());
        assertEquals(2, config.getIncludeFolders().size());
        assertEquals("abc", config.getGroupByRegex());
        assertTrue(config.isKeepFolder());
        assertTrue(config.isKeepFolderStructure());
        assertTrue(config.isRecursive());
    }

    @Test
    public void testCreateConfigFromFileAllParams() throws Throwable {
        mockStatic(ConfigFileUtil.class);
        final JsonObject jsonConfig = createJsonConfig();

        doReturn(jsonConfig).when(ConfigFileUtil.class, "readConfigurationFile", "mocked");

        final JsonObject inputArgs = new JsonObject();
        inputArgs.addProperty("config-file-path", "mocked");
        inputArgs.add("dry-run", null);
        final Configuration config = ConfigFactory.createFromInputArgs(inputArgs);

        assertEquals(1, config.getFolders().size());
        assertEquals(2, config.getFileTypes().size());
        assertEquals(3, config.getIgnored().size());
        assertEquals(4, config.getIncludeFolders().size());
        assertEquals("2012", config.getGroupByRegex());
        assertTrue(config.isKeepFolder());
        assertTrue(config.isKeepFolderStructure());
        assertTrue(config.isRecursive());
    }

    private JsonObject createJsonConfig() {
        final JsonObject jsonConfig = new JsonObject();
        final JsonObject folder = new JsonObject();
        folder.addProperty("inputFolder", "/input");
        folder.addProperty("outputFolder", "/output");

        final JsonArray folders = new JsonArray();
        folders.add(folder);
        jsonConfig.add("folders", folders);

        final JsonArray fileEndings = new JsonArray();
        fileEndings.add("jpg");
        fileEndings.add("gif");
        jsonConfig.add("fileTypes", fileEndings);

        final JsonArray ignoreList = new JsonArray();
        ignoreList.add("PNG");
        ignoreList.add("tif");
        ignoreList.add("zip");
        jsonConfig.add("ignore", ignoreList);

        final JsonArray includedDirs = new JsonArray();
        includedDirs.add("thumbs");
        includedDirs.add("thimpnails");
        includedDirs.add("mini");
        includedDirs.add("miniatyres");
        jsonConfig.add("includeFolders", includedDirs);

        jsonConfig.addProperty("groupByRegex", "2012");
        jsonConfig.addProperty("keepFolder", true);
        jsonConfig.addProperty("keepFolderStructure", true);
        jsonConfig.addProperty("recursive", true);
        return jsonConfig;
    }

}
