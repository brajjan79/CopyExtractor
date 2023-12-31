package com.github.extractor.utils;

import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RarsTest {

    /**
     * Just for 100% test coverage.
     */
    @Test
    public void testInit() {
        try {
            new Rars();
        } catch (final Exception e) {
            fail("Failed to initiate");
        }
    }

    @Test
    void dirContainsUnrarableShouldReturnTrueForValidFiles() {
        final File mockDir = mock(File.class);
        final File[] mockFiles = new File[] { new File("valid.part01.rar"), new File("invalid.txt") };
        when(mockDir.listFiles()).thenReturn(mockFiles);

        final boolean result = Rars.dirContainsUnrarable(mockDir);

        assertTrue(result);
    }

    @Test
    void dirContainsUnrarableShouldReturnFalseForInvalidFiles() {
        final File mockDir = mock(File.class);
        final File[] mockFiles = new File[] { new File("invalid.part02.rar"), new File("invalid.txt") };
        when(mockDir.listFiles()).thenReturn(mockFiles);

        final boolean result = Rars.dirContainsUnrarable(mockDir);

        assertFalse(result);
    }

    @Test
    void fileIsUnrarableShouldReturnTrueForValidRarFile() {
        final File validRarFile = new File("valid.rar");

        final boolean result = Rars.fileIsUnrarable(validRarFile);

        assertTrue(result);
    }

    @Test
    void fileIsUnrarableShouldReturnTrueForValidRarFile01() {
        final File validRarFile = new File("valid.part01.rar");

        final boolean result = Rars.fileIsUnrarable(validRarFile);

        assertTrue(result);
    }

    @Test
    void fileIsUnrarableShouldReturnTrueForValidRarFile001() {
        final File validRarFile = new File("valid.part001.rar");

        final boolean result = Rars.fileIsUnrarable(validRarFile);

        assertTrue(result);
    }

    @Test
    void fileIsUnrarableShouldReturnFalseForInvalidRarFile() {
        final File invalidRarFile = new File("invalid.part02.rar");

        final boolean result = Rars.fileIsUnrarable(invalidRarFile);

        assertFalse(result);
    }

    // Additional test methods for other scenarios
}