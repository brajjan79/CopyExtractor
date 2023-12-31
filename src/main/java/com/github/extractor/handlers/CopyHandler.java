package com.github.extractor.handlers;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.ProgressBarWrapper;
import com.github.filesize.FileSize;

public class CopyHandler {

    private Configuration config;
    private FileHandler fileHandler;

    public CopyHandler() {
        this.config = Configuration.getInstance();
        this.fileHandler = new FileHandler();
    }

    public CopyHandler(Configuration config, FileHandler fileHandler) {
        this.config = config;
        this.fileHandler = fileHandler;
    }

    public boolean copyFiles(final Candidate candidate) {
        boolean success = true;

        for (final File file : candidate.filesToCopy) {
            final File targetFile = fileHandler.createFile(candidate.targetDir, file.getName());
            if (canCopy(file, targetFile)) {
                success = performFileCopy(file, targetFile);
            } else {
                System.out.println("File already exist: " + targetFile.getAbsolutePath());
                StateConstants.addAlreadyExists();
                success = false;
            }

        }
        return success;
    }

    private boolean canCopy(final File file, final File targetFile) {
        if (targetFile.exists()) {
            final double sourceFileSize = FileSize.getBytes(file);
            final double targetFileSize = FileSize.getBytes(targetFile);
            return sourceFileSize > targetFileSize;
        }
        return true;
    }

    private boolean performFileCopy(final File file, final File targetFile) {
        final double sourceFileSize = FileSize.getBytes(file);
        final ProgressBarWrapper progressBar = ProgressBarWrapper.prepare(sourceFileSize, targetFile, "Copying......");
        try {
            if (!config.isDryRun()) {
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
