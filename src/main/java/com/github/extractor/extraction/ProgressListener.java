package com.github.extractor.extraction;

@FunctionalInterface
public interface ProgressListener {

    ProgressListener NONE = progress -> {
    };

    void onProgress(ExtractionProgress progress);
}
