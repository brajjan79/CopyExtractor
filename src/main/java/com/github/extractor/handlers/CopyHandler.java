package com.github.extractor.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.github.extractor.candidate.models.Candidate;
import com.github.filesize.FileSize;

public class CopyHandler {

    public static boolean copyFiles(final Candidate candidate) {
        boolean success = true;
        for (final File file : candidate.filesToCopy) {
            final File targetFile = new File(candidate.targetDir, file.getName());
            if (canCopy(file, targetFile)) {
                try {
                    performFileCopy(file, targetFile);
                    System.out.println("SUCCESSFULLY COPIED FILE: " + file.getName());
                } catch (final IOException e) {
                    System.out.println("");
                    System.out.println("Failed to copy file: " + file.getName());
                    e.printStackTrace();
                    success = false;
                }
            } else {
                System.out.println(
                    "Failure: File already exist: " + targetFile.getAbsolutePath());
                success = false;
            }

        }
        return success;
    }

    private static boolean canCopy(final File file, final File targetFile) {
        if (targetFile.exists()) {
            try {
                final double sourceFileSize = FileSize.size(file).getBytes();
                final double targetFileSize = FileSize.size(targetFile).getBytes();
                return sourceFileSize > targetFileSize;
            } catch (final FileNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    private static void performFileCopy(final File file, final File targetFile) throws IOException {
        System.out.print("Copying file: " + file.getName() + " ---> " + targetFile.getAbsolutePath());
        Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("   Done!!!");

    }

}
