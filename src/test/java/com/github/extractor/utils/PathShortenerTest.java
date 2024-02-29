package com.github.extractor.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PathShortenerTest {

    @Test
    public void testInitPathShortener() {
        try {
            new PathShortener();
        } catch (final Exception e) {
            fail("Failed to initialize PathShortener class");
        }

    }

    @Test
    public void testShortenPathWithLongPath() {
        final String longPath = "/somepath/anotherlongerpath/somenamesomething/filename.txt";
        final String expected = "/som.../ano.../som.../filename.txt";
        assertEquals(expected, PathShortener.shortenPath(longPath, 40));
    }

    @Test
    public void testShortenPathWithShortPath() {
        final String shortPath = "/short/path/file.txt";
        // Assuming the max length allows the full path
        assertEquals(shortPath, PathShortener.shortenPath(shortPath, 40));
    }

    @Test
    public void testShortenPathWithMaxLengthExceeded() {
        final String path = "/path/to/some/very/long/directory/structure/filename.txt";
        // This should result in a path that's exactly 30 characters long
        final String shortened = PathShortener.shortenPath(path, 30);
        assertEquals(30, shortened.length());
    }

    @Test
    public void testShortenPathWithExactMaxLength() {
        final String path = "/this/is/an/exact/path/filename.txt";
        // When the path is exactly at max length
        assertEquals(path, PathShortener.shortenPath(path, path.length()));
    }

    @Test
    public void testShortenPathWithWindowsPath() {
        final String windowsPath = "C:\\somepath\\anotherpath\\filename.txt";
        final String expected = "C:/som.../ano.../filename.txt";
        assertEquals(expected, PathShortener.shortenPath(windowsPath, 30));
    }

}
