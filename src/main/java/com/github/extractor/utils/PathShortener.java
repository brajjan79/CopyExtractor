package com.github.extractor.utils;

public class PathShortener {

    public static String shortenPath(String path, int maxLength) {
        if (path.length() <= maxLength) {
            return path;
        }

        final String[] parts = path.split("[\\\\/]+"); // Corrected split for Windows paths

        final StringBuilder shortenedPath = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0 && !parts[i].isEmpty()) {
                shortenedPath.append("/");
            }
            String part = parts[i];
            if (i < parts.length - 1 && part.length() > 3) {
                part = part.substring(0, 3) + "...";
            }
            shortenedPath.append(part);

            if (shortenedPath.length() >= maxLength) {
                break;
            }
        }

        // If still longer than maxLength, truncate and add '...'
        if (shortenedPath.length() > maxLength) {
            return shortenedPath.substring(0, maxLength - 3) + "...";
        }

        return shortenedPath.toString();
    }
}


