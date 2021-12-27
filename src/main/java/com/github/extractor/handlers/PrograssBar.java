package com.github.extractor.handlers;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.filesize.FileSize;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

class PrograssBar extends Thread {
    private boolean isCancelled = false;
    private File targetFile;
    private double expectedSize;
    private String action;

    @Override
    public void run() {
        final int startFrom = 1;
        final int initialMax = 100;
        final int maxRenderedLength = 160;

        final ProgressBarBuilder pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII).setInitialMax(initialMax).setTaskName(targetFile.getName())
                .setMaxRenderedLength(maxRenderedLength).startsFrom(startFrom, null);

        try (ProgressBar pb = pbb.build()) {
            pb.setExtraMessage(action);
            do {
                final double currentSize = FileSize.size(targetFile).getBytes();
                final int progress = (int) (currentSize / expectedSize * 100);
                pb.stepTo(progress);
                if (progress >= 100) {
                    break;
                }
            } while (!isCancelled);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void init(double sourceFileSize, File targetFile, String action) {
        expectedSize = sourceFileSize;
        this.targetFile = targetFile;
        this.action = action;
    }

    public void cancel() {
        isCancelled = true;
    }
}