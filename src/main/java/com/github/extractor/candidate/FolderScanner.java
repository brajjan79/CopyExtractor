package com.github.extractor.candidate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.DirHandler;
import com.github.extractor.handlers.FileHandler;

public class FolderScanner {

    private final Configuration config;
    private final List<Candidate> candidates = new ArrayList<Candidate>();
    private final CandidateFactory candidateFactory;
    private final DirHandler dirHandler;

    public FolderScanner(final Configuration config) {
        final FileHandler fileHandler = new FileHandler(config);
        this.dirHandler = new DirHandler(fileHandler, config);
        this.candidateFactory = new CandidateFactory(fileHandler, dirHandler);
        this.config = config;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void scanFolders(final File inputDir, final File outputDir) throws FolderException {
        if (inputDir.isFile()) {
            throw new FolderException("Folder is not folder.");
        }

        createAndAddInputDirCandidate(inputDir, outputDir);
        scanSubDirectories(inputDir, outputDir);
    }

    private void createAndAddInputDirCandidate(final File dir, final File outputDir) {
        final File[] fileList = dir.listFiles();
        for (final File file : fileList) {
            if (file.isFile()) {
                final File targetDir = new File(outputDir, dirHandler.getBaseDir(file));
                createAndAddCandidate(file, targetDir);
            }
        }
    }

    private void scanSubDirectories(final File inputDir, final File outputDir) throws FolderException {
        final File[] directories = inputDir.listFiles();
        for (final File dir : directories) {
            if (!dir.isDirectory()) {
                continue;
            }

            if (dir.equals(outputDir)) {
                System.out.println("Ignoring output folder");
                continue;
            }

            scanFolderForPossibleCandidates(dir, outputDir);
        }
    }

    private void scanFolderForPossibleCandidates(final File dir, final File outputDir) throws FolderException {
        if (dirHandler.folderHasMultipleFoldersToScan(dir)) {
            handleGroupedDirs(dir, outputDir);
            createAndAddCandidate(dir, outputDir);
        } else {
            final File targetDir = new File(outputDir, dirHandler.getDirName(dir));
            createAndAddCandidate(dir, targetDir);
        }
    }

    private void handleGroupedDirs(final File dir, final File outputDir) throws FolderException {
        if (config.isRecursive()) {
            if (config.isKeepFolderStructure()) {
                final File childOutputDir = new File(outputDir, dir.getName());
                System.out.println("Keep structure! new output == [" + childOutputDir.getAbsolutePath() + "]");
                scanSubDirectories(dir, childOutputDir);
            } else {
                System.out.println("Dont keep structure");
                scanSubDirectories(dir, outputDir);
            }
        }
    }

    private void createAndAddCandidate(final File file, final File targetDir) {
        final Candidate candidate = candidateFactory.createCandidate(file, targetDir);
        if (!candidate.isEmpty()) {
            candidates.add(candidate);
        }
    }

}
