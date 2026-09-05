package com.github.extractor.extraction;

import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Displays activity and elapsed time without claiming an unreliable percentage.
 */
public class ConsoleProgressListener implements ProgressListener, AutoCloseable {

    private static final char[] SPINNER = { '|', '/', '-', '\\' };

    private final PrintStream output;
    private ScheduledExecutorService scheduler;
    private Instant startedAt;
    private ExtractionProgress currentProgress;
    private int spinnerIndex;

    public ConsoleProgressListener() {
        this(System.out);
    }

    ConsoleProgressListener(final PrintStream output) {
        this.output = Objects.requireNonNull(output);
    }

    @Override
    public synchronized void onProgress(final ExtractionProgress progress) {
        Objects.requireNonNull(progress);
        switch (progress.phase()) {
            case TESTING, EXTRACTING -> startOrUpdate(progress);
            case COMPLETED, FAILED -> finish(progress);
        }
    }

    private void startOrUpdate(final ExtractionProgress progress) {
        currentProgress = progress;
        if (scheduler != null) {
            return;
        }

        startedAt = Instant.now();
        spinnerIndex = 0;
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            final Thread thread = new Thread(runnable, "archive-progress");
            thread.setDaemon(true);
            return thread;
        });
        renderActivity();
        scheduler.scheduleAtFixedRate(this::renderSafely, 1, 1, TimeUnit.SECONDS);
    }

    private synchronized void renderSafely() {
        if (scheduler != null) {
            renderActivity();
        }
    }

    private void renderActivity() {
        final long elapsedSeconds = Duration.between(startedAt, Instant.now()).toSeconds();
        final String message = currentProgress.message() == null ? currentProgress.phase().name()
                : currentProgress.message();
        output.printf("\r%c %s %02d:%02d", SPINNER[spinnerIndex++ % SPINNER.length], message,
                elapsedSeconds / 60, elapsedSeconds % 60);
        output.flush();
    }

    private void finish(final ExtractionProgress progress) {
        stopScheduler();
        final String message = progress.message() == null ? progress.phase().name() : progress.message();
        output.printf("\r%s%n", message);
        output.flush();
        currentProgress = null;
        startedAt = null;
    }

    private void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    @Override
    public synchronized void close() {
        stopScheduler();
    }
}
