package com.github.extractor.candidate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.DirHandler;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.ConfigFolder;

public class FolderScanner {

    private final List<Candidate> candidates = new ArrayList<>();

    private Configuration config;
    private CandidateFactory candidateFactory;
    private DirHandler dirHandler;

    public FolderScanner() {
        this.config = Configuration.getInstance();
        this.dirHandler = new DirHandler();
        this.candidateFactory = new CandidateFactory();
    }

    public FolderScanner(Configuration config, DirHandler dirHandler, CandidateFactory candidateFactory) {
        this.config = config;
        this.dirHandler = dirHandler;
        this.candidateFactory = candidateFactory;
    }

    public void setvariablesForTest(CandidateFactory candidateFactory, DirHandler dirHandler) {
        this.candidateFactory = candidateFactory;
        this.dirHandler = dirHandler;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void scanFolders(ConfigFolder folderItem, final File inputDir, final File outputDir) throws FolderException {
        if (inputDir.isFile()) {
            throw new FolderException("Folder is not folder.");
        }

        createAndAddInputDirCandidate(inputDir, outputDir);
        scanSubDirectories(inputDir, outputDir);
    }

    private void createAndAddInputDirCandidate(final File dir, final File outputDir) {
        final File targetDir = dirHandler.buildTargetBaseDirFile(outputDir, dir);
        createAndAddCandidate(dir, targetDir);
    }

    private void scanSubDirectories(final File inputDir, final File outputDir) throws FolderException {
        final File[] directories = inputDir.listFiles();
        for (final File dir : directories) {
            if (dirHandler.isValidSubfolder(dir, outputDir)) {
                scanFolderForPossibleCandidates(dir, outputDir);
            }
        }
    }

    private void scanFolderForPossibleCandidates(final File dir, final File outputDir) throws FolderException {
        if (dirHandler.folderHasMultipleFoldersToScan(dir)) {
            handleGroupedDirs(dir, outputDir);
            createAndAddCandidate(dir, outputDir);
        } else {
            final File targetDir = dirHandler.buildTargetSubdirFile(outputDir, dir);
            createAndAddCandidate(dir, targetDir);
        }
    }

    private void handleGroupedDirs(final File dir, final File outputDir) throws FolderException {
        if (config.isRecursive()) {
            if (config.isKeepFolderStructure()) {
                final File childOutputDir = dirHandler.buildFile(outputDir, dir);
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
