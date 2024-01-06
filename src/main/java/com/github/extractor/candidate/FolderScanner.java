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

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void scanFolders(ConfigFolder configFolder) throws FolderException {
        if (configFolder.getInputFolder().isFile()) {
            throw new FolderException("Folder is not folder.");
        }

        createAndAddInputDirCandidate(configFolder);
        scanSubDirectories(configFolder.getInputFolder(), configFolder.getOutputFolder());
    }

    private void createAndAddInputDirCandidate(ConfigFolder configFolder) {
        final File targetDir = dirHandler.buildTargetBaseDirFile(configFolder.getOutputFolder(), configFolder.getInputFolder());
        createAndAddCandidate(configFolder.getInputFolder(), targetDir, true);
    }

    private void scanSubDirectories(final File inputDir, final File outputDir) throws FolderException {
        // configFolder.getInputFolder(), configFolder.getOutputFolder()
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
            createAndAddCandidate(dir, outputDir, false);
        } else {
            final File targetDir = dirHandler.buildTargetSubdirFile(outputDir, dir);
            createAndAddCandidate(dir, targetDir, false);
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

    private void createAndAddCandidate(final File file, final File targetDir, boolean isBaseDir) {
        final Candidate candidate = candidateFactory.createCandidate(file, targetDir);
        if (!candidate.isEmpty()) {
            candidate.setIsBaseDir(isBaseDir);
            candidates.add(candidate);
        }
    }

}
