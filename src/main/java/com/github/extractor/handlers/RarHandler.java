package com.github.extractor.handlers;

import java.io.File;
import java.io.IOException;

import com.github.extractor.candidate.models.Candidate;
import com.github.filesize.FileSize;
import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

public class RarHandler {

    /*
     * TODO: extract using existing unrar application to support RAR5
     *
     * Windows: WinRAR/UnRAR.exe x /path/to/rar /path/to/target/dir/
     * Linux:
     * Mac:
     *
     */

    /**
     * Extract rar-files in a candidate if any.
     * Currently does not support RAR5.
     *
     * @param candidate
     * @return
     * @throws IOException
     */
    public static boolean unrarFiles(final Candidate candidate) throws IOException {
        int errors = 0;
        for (final File file : candidate.filesToUnrar) {
            if (!performExtraction(file, candidate.targetDir)) {
                errors++;
            }
        }
        return errors == 0;

    }

    /**
     * Check if the folder contains any file that can be unrared, file must end with .rar
     * and if is part must be part 001 or 01.
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
     * Check if the file can be unrared, file must end with .rar and if is part must be
     * part 001 or 01.
     *
     * @param file
     * @return
     */
    public static boolean fileIsUnrarable(final File file) {
        final String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".rar")) {
            return false;
        }

        final boolean fileIsFirstOfParts = fileName.contains(".part001.")
                || fileName.contains(".part01.");

        return !fileName.contains(".part") || fileIsFirstOfParts;
    }

    private static boolean performExtraction(final File file, final File targetDir) {
        try {
            System.out.println("Extracting file: " + file.getName() + " ---> " + targetDir.getAbsolutePath());
            if (canExtract(file, targetDir)) {
                Junrar.extract(file, targetDir);
                return true;
            }
            return false;
        } catch (RarException | IOException e) {
            System.out.println("");
            System.out.println("Failed to extract files: " + file.getName());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean canExtract(final File file, final File targetDir) throws IOException, RarException {
        try (final Archive archive = new Archive(file)) {
            for (final FileHeader fileHeader : archive) {
                final File targetFile = new File(targetDir, fileHeader.getFileName());
                if (targetFile.exists()) {
                    final long existingFileSize = (long) FileSize.size(targetFile).getBytes();
                    if (existingFileSize < fileHeader.getFullUnpackSize()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }

        System.out.println(
            "All files in the archive " + file.getName() +
            " exists in the target dir " + targetDir.getAbsolutePath());
        return false;
    }
}
