package com.github.extractor;

import java.io.IOException;

import com.github.extractor.candidate.FolderScanner;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.CopyHandler;
import com.github.extractor.handlers.UnrarHandler;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.ConfigFolder;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.Dirs;

public class Executor {

    private Configuration config;
    private FolderScanner folderScanner;
    private CopyHandler copyHandler;
    private UnrarHandler unrarHandler;

    public Executor() {
        config = Configuration.getInstance();
        folderScanner = new FolderScanner();
        copyHandler = new CopyHandler();
        unrarHandler = new UnrarHandler();
    }

    public Executor(Configuration config, FolderScanner folderScanner, CopyHandler copyHandler, UnrarHandler unrarHandler) {
        this.config = config;
        this.folderScanner = folderScanner;
        this.copyHandler = copyHandler;
        this.unrarHandler = unrarHandler;
    }

    public void run() {
        scanForCandidates();
        copyAndUnrarCandidates();
    }

    public void scanForCandidates() {
        for (final ConfigFolder configFolder : config.getFolders()) {
            try {
                folderScanner.scanFolders(configFolder);
            } catch (final FolderException e) {
                e.printStackTrace();
            }
        }
    }

    public void copyAndUnrarCandidates() {

        for (final Candidate candidate : folderScanner.getCandidates()) {
            printProcess("Processing folder: ", candidate);
            copyAndUnrarCandidate(candidate);
        }

        printStateReport();
    }

    private void copyAndUnrarCandidate(final Candidate candidate) {
        try {
            Dirs.createDirs(candidate.getTargetDir());
            copyHandler.copyFiles(candidate);
            unrarHandler.unrarFiles(candidate);
        } catch (final IOException e) {
            System.out.println("Failed to process folder: " + candidate.name);
            e.printStackTrace();
        }
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

    private void printStateReport() {
        final String message = String.format(
                "Files processed:....%s\nFiles successfull:...%s\nFiles ignored:......%s\nFiles failed:........%s",
                StateConstants.getTotal(), StateConstants.getSuccessfull(), StateConstants.getAlreadyExists(), StateConstants.getFailures());
        System.out.println(message);
    }
}
