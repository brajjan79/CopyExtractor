package com.github.extractor.handlers;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.JunrarWrapper;
import com.github.extractor.utils.PrograssBarWrapper;
import com.github.extractor.utils.AutoCloseableIterator;
import com.github.extractor.utils.FileHeaderWrapper;
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

    /*
     * TODO: extract using existing unrar application to support RAR5
     *
     * Windows: WinRAR/UnRAR.exe x /path/to/rar /path/to/target/dir/ Linux: Mac:
     *
     */

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

    private void processFileHeader(final Candidate candidate, AutoCloseableIterator<FileHeaderWrapper> fileHeaders)
            throws FileNotFoundException, InterruptedException, RarException {
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

    private boolean validTargetFileExist(final FileHeaderWrapper fileHeader, final File targetFile) throws FileNotFoundException {
        if (targetFile.exists()) {
            final double existingFileSize = FileSize.getBytes(targetFile);
            if (existingFileSize >= fileHeader.getUnpackedSize()) {
                System.out.println("The file " + targetFile.getName() + " already" + " exists in the target dir " + targetFile.getPath());
                StateConstants.addAlreadyExists();
                return true;
            }
        }
        return false;
    }

    private void extractFileHeader(final FileHeaderWrapper fileHeader, final File targetFile)
            throws InterruptedException, RarException, FileNotFoundException {
        final PrograssBarWrapper progressBar = PrograssBarWrapper.prepare(fileHeader.getUnpackedSize(), targetFile, "Extracting...");
        progressBar.start();

        try {
            fileHeader.extractFile(targetFile);
            StateConstants.addSuccess();
            Thread.sleep(10); // Let progress bar to finish.cd gi
        } finally {
            progressBar.cancel();
        }
    }
}
