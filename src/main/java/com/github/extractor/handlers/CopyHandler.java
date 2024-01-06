package com.github.extractor.handlers;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.FileProgressBar;
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
            try {
                final boolean createFolderInBaseDir = candidate.isBaseDir && config.isCreateFolder();
                System.out.println("File :: " + file.getName() + " createfolder " + String.valueOf(createFolderInBaseDir));
                final File targetFile = fileHandler.createFile(candidate.targetDir, file, createFolderInBaseDir);
                if (canCopy(file, targetFile)) {
                    success = performFileCopy(file, targetFile);
                } else {
                    System.out.println("File already exist: " + targetFile.getAbsolutePath());
                    StateConstants.addAlreadyExists();
                    success = false;
                }
            } catch (final IOException e) {
                StateConstants.addFailure();
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
        final FileProgressBar fpb = FileProgressBar.build().trackedFile(targetFile).expectedSize(sourceFileSize);
        try {
            if (!config.isDryRun()) {
                fpb.start();

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
            fpb.complete();
            return false;
        } finally {
            fpb.waitForCompletion();
        }

    }

}
