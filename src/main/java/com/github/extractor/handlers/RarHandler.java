package com.github.extractor.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.filesize.FileSize;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

public class RarHandler {

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
     * @throws IOException
     */
    public static void unrarFiles(final Candidate candidate, boolean isDryRun) throws IOException {
        for (final File file : candidate.filesToUnrar) {
            scanAndExtractArchive(file, candidate.targetDir, isDryRun);
        }

    }

    /**
     * Check if the folder contains any file that can be unrared, file must end with
     * .rar and if is part must be part 001 or 01.
     *
     * @param dir
     * @return
     */
    public static boolean dirContainsUnrarable(final File dir) {
        final File[] files = dir.listFiles();
        for (final File file : files) {
            if (fileIsUnrarable(file)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the file can be unrared, file must end with .rar and if is part must
     * be part 001 or 01.
     *
     * @param file
     * @return
     */
    public static boolean fileIsUnrarable(final File file) {
        final String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".rar")) {
            return false;
        }

        final boolean fileNameContainParts = fileName.matches(".*part[0-9][0-9].*");
        final boolean fileIsFirstOfParts = fileName.contains(".part001.") || fileName.contains(".part01.");

        return !fileNameContainParts || fileIsFirstOfParts;
    }

    private static void scanAndExtractArchive(final File file, final File targetDir, boolean isDryRun) {
        try (final Archive archive = new Archive(file)) {
            for (final FileHeader fileHeader : archive) {
                final File targetFile = new File(targetDir, fileHeader.getFileName());
                if (targetFile.exists()) {
                    final long existingFileSize = (long) FileSize.size(targetFile).getBytes();
                    if (existingFileSize >= fileHeader.getFullUnpackSize()) {
                        System.out.println("The file " + fileHeader.getFileName() + " already"
                                + " exists in the target dir " + targetDir.getAbsolutePath());
                        StateConstants.addAlreadyExists();
                        continue;
                    }
                }
                if (!isDryRun) {
                    extractFileHeader(archive, fileHeader, targetFile);
                } else {
                    System.out.println("Should have extracted " + fileHeader.getFileName() + " to "
                            + targetFile.getAbsolutePath());
                }
                StateConstants.addSuccess();
            }

        } catch (IOException | RarException e) {
            System.out.println("Failed to extract files: " + file.getName());
            StateConstants.addFailure();
            e.printStackTrace();
        }
    }

    private static void extractFileHeader(final Archive archive, final FileHeader fileHeader, final File targetFile) {
        final PrograssBar progressBar = new PrograssBar();
        progressBar.init(fileHeader.getFullUnpackSize(), targetFile, "Extracting...");
        progressBar.start();

        try {
            final FileOutputStream os = new FileOutputStream(targetFile);
            archive.extractFile(fileHeader, os);
            Thread.sleep(10); // Let progress bar to finish.
        } catch (IOException | RarException | InterruptedException e) {
            progressBar.cancel();
            System.out.println("Failed to extract files: " + fileHeader.getFileName());
            e.printStackTrace();
        }
    }

}
