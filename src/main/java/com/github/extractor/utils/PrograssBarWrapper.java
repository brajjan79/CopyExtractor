package com.github.extractor.utils;

import java.io.File;

import com.github.filesize.FileSize;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class PrograssBarWrapper extends Thread {
    private boolean isCancelled = false;
    private File targetFile;
    private double expectedSize;
    private String action;
    private final int startFrom = 1;
    private final int initialMax = 100;
    private final int maxRenderedLength = 160;
    private ProgressBarBuilder pbb;

    public PrograssBarWrapper(double sourceFileSize, File targetFile, String action) {
        this.expectedSize = sourceFileSize;
        this.targetFile = targetFile;
        this.action = action;
    }

    public static PrograssBarWrapper prepare(double sourceFileSize, File targetFile, String action) {
        final PrograssBarWrapper progresBarWrapper = new PrograssBarWrapper(sourceFileSize, targetFile, action);
        progresBarWrapper.init();
        return progresBarWrapper;
    }

    public void init() {
        pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII).setInitialMax(initialMax).setTaskName(targetFile.getName())
                .setMaxRenderedLength(maxRenderedLength).startsFrom(startFrom, null).setUpdateIntervalMillis(10);
    }

    public void init(ProgressBarBuilder pbb) {
        this.pbb = pbb;
    }

    @Override
    public void run() {
        try (ProgressBar pb = pbb.build()) {
            pb.setExtraMessage(action);
            do {
                final int progress = getProgress();
                pb.stepTo(progress);
                if (progress >= 100) {
                    break;
                }
            } while (!isCancelled);
        }
    }

    public int getProgress() {
        final double currentSize = FileSize.getBytes(targetFile);
        final int progress = (int) (currentSize / expectedSize * 100);
        return progress;
    }

    public void cancel() {
        isCancelled = true;
    }
}