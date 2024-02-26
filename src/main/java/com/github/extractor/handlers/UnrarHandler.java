package com.github.extractor.handlers;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.AutoCloseableIterator;
import com.github.extractor.utils.FileHeaderWrapper;
import com.github.extractor.utils.FileProgressBar;
import com.github.extractor.utils.JunrarWrapper;
import com.github.extractor.utils.PathShortener;
import com.github.filesize.FileSize;
import com.github.junrar.exception.RarException;

public class UnrarHandler {

    private Configuration config;

    public UnrarHandler() {
        this.config = Configuration.getInstance();
    }

    public UnrarHandler(Configuration config) {
        this.config = config;
    }

    /**
     * Extract rar-files in a candidate if any. Currently does not support RAR5.
     *
     * @param candidate
     * @param dryRun
     * @return
     */
    public void unrarFiles(final Candidate candidate) {
        for (final File file : candidate.filesToUnrar) {
            try (AutoCloseableIterator<FileHeaderWrapper> fileHeaders = JunrarWrapper.getFileHeaderIterator(file)) {
                processFileHeader(candidate, fileHeaders);
            } catch (final Exception e) {
                System.out.println("Failed to extract file: " + file.getName());
                StateConstants.addFailure();
                e.printStackTrace();
                continue;
            }

        }

    }

    private void processFileHeader(final Candidate candidate, AutoCloseableIterator<FileHeaderWrapper> fileHeaders) throws RarException {
        while (fileHeaders.hasNext()) {
            final FileHeaderWrapper fileHeader = fileHeaders.next();
            final File targetFile = fileHeader.getDestinationFile(candidate.targetDir);

            if (validTargetFileExist(fileHeader, targetFile)) {
                continue;
            }
            if (!config.isDryRun()) {
                extractFileHeader(fileHeader, targetFile);
            }
        }
    }

    private boolean validTargetFileExist(final FileHeaderWrapper fileHeader, final File targetFile) {
        if (targetFile.exists()) {
            final double existingFileSize = FileSize.getBytes(targetFile);
            if (existingFileSize >= fileHeader.getUnpackedSize()) {
                System.out.println("The file " +
                        targetFile.getName() +
                        " already exists " +
                        PathShortener.shortenPath(targetFile.getPath(), 30));
                StateConstants.addAlreadyExists();
                return true;
            }
        }
        return false;
    }

    private void extractFileHeader(final FileHeaderWrapper fileHeader, final File targetFile) throws RarException {
        final FileProgressBar fpb = FileProgressBar.build().trackedFile(targetFile).setAction("Extracting")
                .expectedSize(fileHeader.getUnpackedSize());
        fpb.start();

        try {
            fileHeader.extractFile(targetFile);
            StateConstants.addSuccess();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            fpb.complete();
        } finally {
            fpb.waitForCompletion();
        }
    }
}
