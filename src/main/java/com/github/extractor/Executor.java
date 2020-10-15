package com.github.extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.extractor.candidate.FolderScanner;
import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.configuration.models.ConfigFolder;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.CopyHandler;
import com.github.extractor.handlers.RarHandler;
import com.github.extractor.utils.Dirs;

public class Executor {

    private final Configuration config;
    private List<Candidate> candidates = new ArrayList<>();

    public Executor(final Configuration config) {
        this.config = config;
        System.out.println(config.toString());
    }

    public void run() {
        scanForCandidates();
        copyAndUnrarCandidates();
    }

    public void scanForCandidates() {
        final FolderScanner folderScanner = new FolderScanner(config);
        for (final ConfigFolder folderItem : config.getFolders()) {
            try {
                folderScanner.scanFolders(new File(folderItem.getInputFolder()),
                                          new File(folderItem.getOutputFolder()));
            } catch (final FolderException e) {
                e.printStackTrace();
            }
        }
        candidates = folderScanner.getCandidates();

    }

    public void copyAndUnrarCandidates() {

        int count = 1;
        int errorCount = 0;
        final int itemsToProcess = candidates.size();
        for (final Candidate candidate : candidates) {
            System.out.println("\nHandling item " + String.valueOf(count) + "\\"
                    + String.valueOf(itemsToProcess));
            count++;

            printProcess("Processing folder:", candidate);
            boolean success;
            if (!config.isDryRun()){
                success = copyAndUnrarCandidate(candidate);
            } else {
                success = true;
                printDryRun(candidate);
            }
            if (!success) {
                errorCount++;
            }
            printProcess("Finished folder:", candidate);
        }

        if (errorCount > 0) {
            printErrorCount(itemsToProcess, errorCount);
        }
    }

    private boolean copyAndUnrarCandidate(final Candidate candidate) {
        boolean success = true;
        try {
            Dirs.createDirs(candidate.targetDir);
            final boolean copyOk = CopyHandler.copyFiles(candidate);
            final boolean unrarOk = RarHandler.unrarFiles(candidate);

            if (!copyOk || !unrarOk) {
                success = false;
            }
        } catch (final IOException e) {
            System.out.println("Failed to process folder: " + candidate.name);
            e.printStackTrace();
        }
        return success;
    }

    private void printProcess(final String message, final Candidate candidate) {
        final String processing = "### " + message + " " + candidate.name + " ###";
        final String stars = getStars(processing);
        System.out.println(stars + "\n" + processing + "\n" + stars);
    }

    private String getStars(final String processing) {
        String stars = "";
        for (int i = 0; i < processing.length(); i++) {
            stars = stars + "#";
        }
        return stars;
    }

    private void printErrorCount(final int itemsToProcess, final int errorCount) {
        final String message = "######################\n" + "### Summary \n" + "### " + errorCount
                + " out of " + itemsToProcess + " failed!\n"
                + "### Please look at the log for more details.";
        System.out.println(message);
    }

    private void printDryRun(final Candidate candidate) {
        for (final File file : candidate.filesToUnrar) {
            System.out.println(String.format("Should have extracted '%s' to folder '%s'", file.getAbsolutePath(), candidate.targetDir));
        }
        for (final File file : candidate.filesToCopy) {
            System.out.println(String.format("Should have copied '%s' to folder '%s'", file.getAbsolutePath(), candidate.targetDir));
        }
    }
}
