package com.github.extractor.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class FileTypeAdapterTest {

    @Test
    void readReturnsNullWhenJsonTokenIsNull() throws IOException {
        final JsonReader reader = mock(JsonReader.class);
        when(reader.peek()).thenReturn(JsonToken.NULL);

        final FileTypeAdapter adapter = new FileTypeAdapter();
        final File result = adapter.read(reader);

        assertNull(result);
        verify(reader).nextNull(); // Verify nextNull() was called
    }

    @Test
    void readReturnsFileWhenJsonTokenIsString() throws IOException {
        final String testPath = new File("/path/to/testfile.txt").getPath();
        final JsonReader reader = mock(JsonReader.class);
        when(reader.peek()).thenReturn(JsonToken.STRING);
        when(reader.nextString()).thenReturn(testPath);

        final FileTypeAdapter adapter = new FileTypeAdapter();
        final File result = adapter.read(reader);

        assertNotNull(result);
        assertEquals(testPath, result.getPath());
    }

    @Test
    void writeShouldHandleNullFile() throws IOException {
        final JsonWriter writer = mock(JsonWriter.class);
        final File file = null;

        final FileTypeAdapter adapter = new FileTypeAdapter();
        adapter.write(writer, file);

        verify(writer).nullValue(); // Verify that nullValue() is called on the writer
    }

    @Test
    void writeShouldHandleNonNullFile() throws IOException {
        final JsonWriter writer = mock(JsonWriter.class);
        final File file = new File("/path/to/file.txt");

        final FileTypeAdapter adapter = new FileTypeAdapter();
        adapter.write(writer, file);

        verify(writer).value(file.getAbsolutePath()); // Verify that value() is called with the file's path
    }

}
