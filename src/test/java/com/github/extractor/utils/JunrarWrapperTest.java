package com.github.extractor.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

class JunrarWrapperTest {

    /**
     * Just for 100% test coverage.
     */
    @Test
    public void testInit() {
        try {
            new JunrarWrapper();
        } catch (final Exception e) {
            fail("Failed to initiate Dirs");
        }
    }

    @Test
    void getFileHeaderIteratorShouldReturnHeadersForValidRarFile() throws Exception {
        // Get the file from the resources folder
        final File rarFile = getFileFromResources("files/rar_files/rar_file_1.rar");

        final AutoCloseableIterator<FileHeaderWrapper> iterator = JunrarWrapper.getFileHeaderIterator(rarFile);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());

        final FileHeaderWrapper header = iterator.next();
        assertNotNull(header);

        iterator.close();
    }

    @Test
    void getFileHeaderIteratorShouldReturnHeadersForValidRarFileNoNext() throws Exception {
        // Get the file from the resources folder
        final File rarFile = getFileFromResources("files/rar_files/rar_file_1.rar");

        final AutoCloseableIterator<FileHeaderWrapper> iterator = JunrarWrapper.getFileHeaderIterator(rarFile);
        assertNotNull(iterator);

        while (iterator.hasNext()) {
            iterator.next();
        }
        assertThrows(NoSuchElementException.class, () -> {
            iterator.next();
        });
        assertFalse(iterator.hasNext());

        iterator.close();
    }

    private File getFileFromResources(String resourcePath) throws URISyntaxException {
        return new File(getClass().getClassLoader().getResource(resourcePath).toURI());
    }

    // Additional tests for different scenarios like empty RAR files, corrupted RAR files, etc.
}
