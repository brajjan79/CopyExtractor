package com.github.extractor.extraction;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.extractor.utils.AutoCloseableIterator;
import com.github.extractor.utils.FileHeaderWrapper;
import com.github.extractor.utils.JunrarWrapper;
import com.github.junrar.exception.RarException;

/**
 * Pure Java fallback backed by Junrar.
 */
public class JunrarExtractor implements ArchiveExtractor {

    @Override
    public String getName() {
        return "Junrar";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public ExtractionResult test(final File archive, final ProgressListener progressListener) {
        progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.TESTING,
                "Testing archive with Junrar..."));
        try (AutoCloseableIterator<FileHeaderWrapper> headers = JunrarWrapper.getFileHeaderIterator(archive);
                OutputStream output = OutputStream.nullOutputStream()) {
            while (headers.hasNext()) {
                headers.next().extractFile(output);
            }
            progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.COMPLETED,
                    "Archive test completed."));
            return new ExtractionResult(ExtractionResult.Status.SUCCESS, 0, "", null);
        } catch (final RarException e) {
            return archiveError(progressListener, e);
        } catch (final Exception e) {
            return executionError(progressListener, e);
        }
    }

    @Override
    public ExtractionResult extract(final File archive, final File destination,
            final ProgressListener progressListener) {
        progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.EXTRACTING,
                "Extracting with Junrar..."));
        try (AutoCloseableIterator<FileHeaderWrapper> headers = JunrarWrapper.getFileHeaderIterator(archive)) {
            while (headers.hasNext()) {
                final FileHeaderWrapper header = headers.next();
                final File targetFile = safeDestination(header, destination);
                progressListener.onProgress(new ExtractionProgress(ExtractionPhase.EXTRACTING, null,
                        targetFile.getName(), "Extracting " + targetFile.getName() + "..."));
                if (targetFile.exists()) {
                    continue;
                }
                createParentDirectories(targetFile);
                header.extractFile(targetFile);
            }
            progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.COMPLETED,
                    "Archive operation completed."));
            return new ExtractionResult(ExtractionResult.Status.SUCCESS, 0, "", null);
        } catch (final RarException e) {
            return archiveError(progressListener, e);
        } catch (final Exception e) {
            return executionError(progressListener, e);
        }
    }

    private File safeDestination(final FileHeaderWrapper header, final File destination) throws IOException {
        final Path destinationPath = destination.toPath().toAbsolutePath().normalize();
        final Path targetPath = header.getDestinationFile(destination).toPath().toAbsolutePath().normalize();
        if (!targetPath.startsWith(destinationPath)) {
            throw new IOException("Archive entry points outside destination: " + targetPath);
        }
        return targetPath.toFile();
    }

    private void createParentDirectories(final File targetFile) throws IOException {
        final File parent = targetFile.getParentFile();
        if (parent != null) {
            Files.createDirectories(parent.toPath());
        }
    }

    private ExtractionResult archiveError(final ProgressListener progressListener, final RarException exception) {
        progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.FAILED,
                "Archive operation failed: " + exception.getMessage()));
        return new ExtractionResult(ExtractionResult.Status.ARCHIVE_ERROR, 2, "", exception);
    }

    private ExtractionResult executionError(final ProgressListener progressListener, final Exception exception) {
        progressListener.onProgress(ExtractionProgress.phase(ExtractionPhase.FAILED,
                "Could not run Junrar: " + exception.getMessage()));
        return ExtractionResult.executionError(exception);
    }
}
