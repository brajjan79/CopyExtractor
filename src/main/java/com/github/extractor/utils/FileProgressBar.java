package com.github.extractor.utils;

import java.io.File;

import com.github.filesize.FileSize;
import com.github.filesize.DataSizeFormatter;

public class FileProgressBar {
    private static Thread thread;
    private double totalSize;
    private FileSize fileSize;
    private final int barWidth = 50; // Width of the progress bar
    private final long updateInterval = 5; // Update interval in milliseconds
    private boolean running;
    private String fileName;
    private String action = "";
    private String indicator = "#";

    public static FileProgressBar build() {
        final FileProgressBar instance = new FileProgressBar();
        thread = new Thread(instance::updateProgressBar);
        return instance;
    }

    public FileProgressBar trackedFile(File trackedFile) {
        this.fileSize = new FileSize(trackedFile);
        fileName = FileProgressBar.truncateFileName(trackedFile.getName(), 50);
        return this;
    }

    public FileProgressBar expectedSize(double totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    public FileProgressBar setFileSizeInstance(FileSize fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public FileProgressBar setAction(String action) {
        this.action = action;
        return this;
    }

    public void start() {
        running = true;
        thread.start();
    }

    private void updateProgressBar() {
        while (running) {
            final double processedSize = fileSize.getBytes();
            update(processedSize);
            if (processedSize >= totalSize) {
                update(totalSize, true);
                break;
            }
            try {
                Thread.sleep(updateInterval);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Progress update interrupted: " + e.getMessage());
                break;
            }
        }
        running = false;
    }

    private void update(double processedSizeMB) {
        update(processedSizeMB, false);
    }

    private void update(double processedSizeMB, boolean finalPrint) {
        final double progressPercentage = processedSizeMB / totalSize;
        final int filledLength = (int) (barWidth * progressPercentage);

        final String bar = "[" + indicator.repeat(filledLength) + " ".repeat(barWidth - filledLength) + "]";
        final String output = String.format("\r%s %s %3d%% %s/%s %s", fileName, bar, (int) (progressPercentage * 100),
                DataSizeFormatter.formatBytes(processedSizeMB, 4, 2), DataSizeFormatter.formatBytes(totalSize, 4, 2), action);

        if (finalPrint) {
            System.out.println(output);
        } else {
            System.out.print(output);
        }
    }

    public void complete() {
        running = false;
        if (thread.isAlive()) {
            try {
                Thread.sleep(updateInterval);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForCompletion() {
        while (running) {
            try {
                Thread.sleep(updateInterval);
            } catch (final InterruptedException e) {
            }
        }
    }

    public static String truncateFileName(String fileName, int width) {
        if (fileName.length() <= width) {
            return String.format("%-" + width + "s", fileName);
        }
        final String start = fileName.substring(0, width - 3);
        return start + "...";
    }

}

