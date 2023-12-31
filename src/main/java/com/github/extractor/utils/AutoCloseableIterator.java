package com.github.extractor.utils;

import java.util.Iterator;

public interface AutoCloseableIterator<T> extends Iterator<T>, AutoCloseable {
    // This interface combines Iterator and AutoCloseable without adding new methods
}
