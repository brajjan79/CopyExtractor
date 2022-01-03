package com.github.extractor.handlers;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.filesize.FileSize;

public class CopyHandler {

    public static boolean copyFiles(final Candidate candidate, boolean isDryrun) {
        boolean success = true;

        for (final File file : candidate.filesToCopy) {
            final File targetFile = new File(candidate.targetDir, file.getName());
            if (canCopy(file, targetFile)) {
                success = performFileCopy(file, targetFile, isDryrun);
            } else {
                System.out.println("File already exist: " + targetFile.getAbsolutePath());
                StateConstants.addAlreadyExists();
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

    private static boolean performFileCopy(final File file, final File targetFile, boolean isDryrun) {
        final PrograssBar progressBar = new PrograssBar();
        try {
            if (!isDryrun) {
                final double sourceFileSize = FileSize.size(file).getBytes();
                progressBar.init(sourceFileSize, targetFile, "Copying......");
                progressBar.start();

                Files.copy(file, targetFile);
                Thread.sleep(10); // Let progress bar to finish.
            } else {
                System.out.println(
                        "Should have copied " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
            }
            StateConstants.addSuccess();
            return true;
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
            StateConstants.addFailure();
            progressBar.cancel();
            return false;
        }

    }

}
