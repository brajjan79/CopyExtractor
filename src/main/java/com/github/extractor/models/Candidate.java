package com.github.extractor.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Candidate {

    public final String name;
    public final File targetDir;
    public final List<File> filesToUnrar = new ArrayList<>();
    public final List<File> filesToCopy =  new ArrayList<>();
    public boolean isBaseDir = false;

    public Candidate (final String name, final File targetDir) {
        this.name = name;
        this.targetDir = targetDir;
    }

    public boolean isEmpty() {
        return filesToUnrar.isEmpty() && filesToCopy.isEmpty();
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setIsBaseDir(boolean isBaseDir) {
        this.isBaseDir = isBaseDir;
    }
}
