package com.github.extractor.candidate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.handlers.DirHandler;
import com.github.extractor.handlers.FileHandler;
import com.github.extractor.handlers.RarHandler;
import com.github.extractor.utils.Dirs;

public class CandidateFactory {

    private static final int LAST_MODIFIED_WAIT_TIME = 10000;
    private final FileHandler fileHandler;
    private final DirHandler dirHandler;

    public CandidateFactory(final FileHandler fileHandler, final DirHandler dirHandler) {
        this.fileHandler = fileHandler;
        this.dirHandler = dirHandler;
    }

    public Candidate createCandidate(final File source, final File targetDir) {
        final Candidate candidate = new Candidate(source.getName(), targetDir);

        if (Dirs.lastModifiedLessThen(source, LAST_MODIFIED_WAIT_TIME)) {
            System.out.println("Folder: " + source.getName() + " is currently being written to.");
            return candidate;
        }

        if (source.isFile()) {
            return createSingleFileCandidate(source, targetDir);
        }

        candidate.filesToUnrar.addAll(getFilesToUnrar(source));
        candidate.filesToCopy.addAll(getFilesToCopy(source));

        return candidate;
    }

    public Candidate createSingleFileCandidate(final File file, final File targetDir) {
        final Candidate candidate = new Candidate(file.getName(), targetDir);

        if (RarHandler.fileIsUnrarable(file) && !fileHandler.isIgnored(file)) {
            candidate.filesToUnrar.add(file);
        }

        if (fileHandler.isIncludedFileType(file) && !fileHandler.isIgnored(file)) {
            candidate.filesToCopy.add(file);
        }

        return candidate;
    }

    private List<File> getFilesToUnrar(final File sourceDir) {
        final List<File> files_to_unrar = new ArrayList<>();
        final File[] files = sourceDir.listFiles();
        for (final File file : files) {
            if (RarHandler.fileIsUnrarable(file) && !fileHandler.isIgnored(file)) {
                files_to_unrar.add(file);
                continue;
            }

            if (dirHandler.directoryIncluded(file)) {
                System.out.println("Found included directory, scanning directory for included files.");
                files_to_unrar.addAll(getFilesToUnrar(file));
            }
        }
        return files_to_unrar;
    }

    private List<File> getFilesToCopy(final File sourceDir) {
        final List<File> files_to_copy = new ArrayList<>();
        final File[] files = sourceDir.listFiles();
        for (final File file : files) {
            if (fileHandler.isIncludedFileType(file) && !fileHandler.isIgnored(file)) {
                files_to_copy.add(file);
                continue;
            }

            if (dirHandler.directoryIncluded(file)) {
                System.out.println("Found included directory, scanning directory for included files.");
                files_to_copy.addAll(getFilesToCopy(file));
            }
        }
        return files_to_copy;
    }

}
