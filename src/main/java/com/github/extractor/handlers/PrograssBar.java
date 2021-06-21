package com.github.extractor.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.temporal.ChronoUnit;

import com.github.filesize.FileSize;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

class PrograssBar extends Thread {
    boolean showProgress = true;

    int start = 0;
    int end = 100;

    private boolean isCancelled = false;
    private File targetFile;
    private double expectedSize;
    private String action;

    @Override
    public void run() {

        final ProgressBarBuilder pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII).setInitialMax(end).setTaskName(targetFile.getName())
                .setMaxRenderedLength(150).startsFrom(1, null);

        try (ProgressBar pb = pbb.build()) {

            pb.setExtraMessage(action);
            do {
                final double currentSize = FileSize.size(targetFile).getBytes();
                final int progress = (int) (currentSize / expectedSize * 100);
                pb.stepTo(progress);
                if (progress >= 100) {
                    break;
                }
                // Thread.sleep(10);
            } while (!isCancelled);
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
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