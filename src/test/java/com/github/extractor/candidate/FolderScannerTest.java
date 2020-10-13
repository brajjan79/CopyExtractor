package com.github.extractor.candidate;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.handlers.DirHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FolderScanner.class })
public class FolderScannerTest {

    private File dir_level_2;
    private File dir_level_3;
    private File inputDirSingleSubDir;
    private File inputDirWithFiles;

    private File mock_file_1;
    private File mock_file_2;
    private File mock_file_3;

    private File outputDir;

    private Candidate candidate;
    private Candidate emptyCandidate;
    private FolderScanner folderScanner;
    private CandidateFactory candidateFactory;
    private DirHandler dirHandler;
    private Configuration config;

    @Before
    public void init() throws Exception {
        dirHandler = PowerMockito.mock(DirHandler.class);
        config = PowerMockito.mock(Configuration.class);
        candidateFactory = PowerMockito.mock(CandidateFactory.class);

        whenNew(CandidateFactory.class).withAnyArguments().thenReturn(candidateFactory);
        whenNew(DirHandler.class).withAnyArguments().thenReturn(dirHandler);

        candidate = new Candidate("name", dir_level_2);
        candidate.filesToCopy.add(new File(""));

        emptyCandidate = new Candidate("name", dir_level_2);

        folderScanner = new FolderScanner(config);
        setupMockFolderFolders();
    }

    /**
     * 2 files and dir_level_2 should be valid candidates.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFolderNoSubFolders() throws Throwable {
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_2)).thenReturn(false);
        when(candidateFactory.createCandidate(dir_level_2, outputDir)).thenReturn(candidate);
        when(config.isRecursive()).thenReturn(false);

        folderScanner.scanFolders(inputDirSingleSubDir, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 3 files/folders to be valid candidates.", 3, candidates.size());
    }

    /**
     * 2 files from level 1, 3 files from level 2 and dir_level_3 should be valid candidates.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFolderOneLevelSubfolder() throws Throwable {
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_2)).thenReturn(true);
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_3)).thenReturn(false);
        when(candidateFactory.createCandidate(dir_level_3, outputDir)).thenReturn(candidate);
        when(config.isRecursive()).thenReturn(true);
        when(config.isKeepFolderStructure()).thenReturn(false);

        folderScanner.scanFolders(inputDirSingleSubDir, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 6 files/folders to be valid candidates.", 6, candidates.size());
    }

    /**
     * 2 files from level 1, 3 files from level 2 and 3 files in level 3.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFoldertwoLevelSubfolders() throws Throwable {
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_2)).thenReturn(true);
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_3)).thenReturn(true);
        when(config.isRecursive()).thenReturn(true);
        when(config.isKeepFolderStructure()).thenReturn(true);

        folderScanner.scanFolders(inputDirSingleSubDir, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 8 files/folders to be valid candidates.", 8, candidates.size());
    }

    /**
     * 2 files from level 1.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFoldertwoLevelSubfoldersBurRecursiveIsOff() throws Throwable {
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_2)).thenReturn(true);
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_3)).thenReturn(true);
        when(config.isRecursive()).thenReturn(false);

        folderScanner.scanFolders(inputDirSingleSubDir, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 2 files/folders to be valid candidates.", 2, candidates.size());
    }

    /**
     * 2 files from level 1, 3 files from level 2 and 3 files in level 3.
     *
     * mock_file_1 is invalid.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFoldertwoLevelSubfoldersButEmptyCandidates() throws Throwable {
        when(candidateFactory.createCandidate(mock_file_1, outputDir)).thenReturn(emptyCandidate);
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_2)).thenReturn(true);
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_3)).thenReturn(true);
        when(config.isRecursive()).thenReturn(true);
        when(config.isKeepFolderStructure()).thenReturn(false);

        folderScanner.scanFolders(inputDirSingleSubDir, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 5 files/folders to be valid candidates.", 5, candidates.size());
    }

    /**
     * Test where the scanned dir_level_2 is the same as output dir.
     * 2 files in base dir should be candidates.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFolderWithSubfolderBeingOutputDir() throws Throwable {
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_2)).thenReturn(true);
        when(dirHandler.folderHasMultipleFoldersToScan(dir_level_3)).thenReturn(true);
        when(config.isRecursive()).thenReturn(false);

        when(dir_level_2.getAbsolutePath()).thenReturn("/b/");
        when(dir_level_2.equals(outputDir)).thenReturn(true);

        folderScanner.scanFolders(inputDirSingleSubDir, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 2 folder to be valid candidate.", 2, candidates.size());
    }

    /**
     * Test for 3 files only, no dirs.
     *
     * @throws Throwable
     */
    @Test
    public void testScanInputDirFilesOnly() throws Throwable {
        folderScanner.scanFolders(inputDirWithFiles, outputDir);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 3 files to be valid candidate.", 3, candidates.size());
    }

    /**
     * Invalid base dir throws RuntimeException.
     *
     * @throws Throwable
     */
    @Test(expected = RuntimeException.class)
    public void testScanInvalidDir() throws Throwable {
        folderScanner.scanFolders(mock_file_1, outputDir);
    }

    private void setupMockFolderFolders() throws Exception {
        inputDirSingleSubDir = PowerMockito.mock(File.class);
        inputDirWithFiles = PowerMockito.mock(File.class);
        outputDir = PowerMockito.mock(File.class);

        dir_level_2 = PowerMockito.mock(File.class);
        dir_level_3 = PowerMockito.mock(File.class);

        mock_file_1 = PowerMockito.mock(File.class);
        mock_file_2 = PowerMockito.mock(File.class);
        mock_file_3 = PowerMockito.mock(File.class);

        final File[] level_1_list = new File[] { dir_level_2, mock_file_1, mock_file_2 };
        final File[] level_2_list = new File[] { dir_level_3, mock_file_1, mock_file_2, mock_file_3 };
        final File[] fileList = new File[] { mock_file_1, mock_file_2, mock_file_3 };
        when(inputDirSingleSubDir.listFiles()).thenReturn(level_1_list);
        when(dir_level_2.listFiles()).thenReturn(level_2_list);
        when(dir_level_3.listFiles()).thenReturn(fileList);
        when(inputDirWithFiles.listFiles()).thenReturn(fileList);

        when(dir_level_2.isDirectory()).thenReturn(true);
        when(dir_level_3.isDirectory()).thenReturn(true);

        when(mock_file_1.isFile()).thenReturn(true);
        when(mock_file_1.isDirectory()).thenReturn(false);
        when(mock_file_2.isFile()).thenReturn(true);
        when(mock_file_2.isDirectory()).thenReturn(false);
        when(mock_file_3.isFile()).thenReturn(true);
        when(mock_file_3.isDirectory()).thenReturn(false);

        when(dir_level_2.getAbsolutePath()).thenReturn("/a/");
        when(dir_level_3.getAbsolutePath()).thenReturn("/c/");
        when(outputDir.getAbsolutePath()).thenReturn("/b/");

        whenNew(File.class).withAnyArguments().thenReturn(outputDir);

        when(candidateFactory.createCandidate(mock_file_1, outputDir)).thenReturn(candidate);
        when(candidateFactory.createCandidate(mock_file_2, outputDir)).thenReturn(candidate);
        when(candidateFactory.createCandidate(mock_file_3, outputDir)).thenReturn(candidate);
    }

}
