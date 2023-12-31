package com.github.extractor.candidate.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.github.extractor.models.Candidate;

public class CandiateTest {

    @Test
    public void testCandidateIsEmpty() {
        final Candidate candidate = new Candidate("", new File(""));
        assertTrue(candidate.isEmpty());
    }

    @Test
    public void testCandidateIsNotEmptyHasFile() {
        final Candidate candidate = new Candidate("", new File(""));
        candidate.filesToCopy.add(new File(""));
        assertFalse(candidate.isEmpty());
    }

    @Test
    public void testCandidateIsNotEmptyHasRar() {
        final Candidate candidate = new Candidate("", new File(""));
        candidate.filesToUnrar.add(new File(""));
        assertFalse(candidate.isEmpty());
    }

}
