package com.github.extractor.candidate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.DirHandler;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.ConfigFolder;

public class FolderScannerTest {


    @Mock
    private ConfigFolder configFolder;
    @Mock
    private Candidate mockedCandidate;
    @Mock
    private Candidate mockedEmptyCandidate;
    @Mock
    private CandidateFactory candidateFactory;
    @Mock
    private DirHandler dirHandler;
    @Mock
    private Configuration config;

    private FolderScanner folderScanner;

    @BeforeEach
    public void init() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockedCandidate.isEmpty()).thenReturn(false);
        when(mockedEmptyCandidate.isEmpty()).thenReturn(true);

        folderScanner = new FolderScanner(config, dirHandler, candidateFactory);
        setupMockFolderFolders();
    }

    /**
     * Scan folders with recursive off.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFoldersRecursiveOff() throws Throwable {
        when(config.isRecursive()).thenReturn(false);

        folderScanner.scanFolders(configFolder);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 3 folders to be valid candidates.", 3, candidates.size());
    }

    /**
     * Scan folders with recursive on.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFoldersRecursiveOn() throws Throwable {
        when(config.isRecursive()).thenReturn(true);
        when(config.isKeepFolderStructure()).thenReturn(false);

        folderScanner.scanFolders(configFolder);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 3 folders to be valid candidates.", 6, candidates.size());
    }

    /**
     * Scan folders with recursive and KeepFolderStructure on.
     *
     * @throws Throwable
     */
    @Test
    public void testScanFoldersRecursiveOnKeepStructureOn() throws Throwable {
        when(config.isRecursive()).thenReturn(true);
        when(config.isKeepFolderStructure()).thenReturn(true);

        folderScanner.scanFolders(configFolder);
        final List<Candidate> candidates = folderScanner.getCandidates();
        assertEquals("Expect 3 folders to be valid candidates.", 6, candidates.size());
    }

    /**
     * Invalid input folder throws RuntimeException.
     *
     * @throws Throwable
     */
    @Test
    public void testScanInvalidDir() throws Throwable {
        final File mockedFile = mock(File.class);
        when(mockedFile.isFile()).thenReturn(true);
        when(mockedFile.isDirectory()).thenReturn(false);
        doThrow(new FolderException("Test")).when(configFolder).validate();
        assertThrows(FolderException.class, () -> {
            folderScanner.scanFolders(configFolder);
        });
    }

    private void setupMockFolderFolders() throws Exception {
        // Folder structure:
        // /input_dir/sub_dir_a/dir_no_files/
        // ..................../dir_with_file/mock_file
        // ..................../mock_file
        // ........../dir_with_file/mock_file
        // ........../dir_no_files/
        // ........../sub_dir_b/sub_dir_with_file/mock_file
        // .....................sub_dir_with_file/mock_file
        // ........../mock_file

        // non recursive should provide 3 candidates
        // recursive should provide 6 candidates

        // Init mocks
        final File outputDir = mock(File.class);
        final File inputDir = mock(File.class);

        final File subDirA = mock(File.class);
        final File subDirB = mock(File.class);
        final File dirWithFile = mock(File.class);
        final File dirWithNoFiles = mock(File.class);

        final File mockedFile = mock(File.class);

        // setup file lists
        final File[] inputDirFileList = new File[] { subDirA, dirWithFile, dirWithNoFiles, subDirB, mockedFile };
        final File[] subDirAFileList = new File[] { dirWithNoFiles, dirWithFile, mockedFile };
        final File[] subDirBFileList = new File[] { dirWithFile, dirWithFile };
        final File[] listWithFile = new File[] { mockedFile };

        // inputDir setup
        when(inputDir.isDirectory()).thenReturn(true);
        when(inputDir.getAbsolutePath()).thenReturn("/input_dir/");
        when(inputDir.getName()).thenReturn("input_dir");
        when(inputDir.listFiles()).thenReturn(inputDirFileList);
        when(candidateFactory.createCandidate(inputDir, outputDir)).thenReturn(mockedCandidate);
        when(dirHandler.folderHasMultipleFoldersToScan(inputDir)).thenReturn(true);

        // subDirA setup
        when(subDirA.isDirectory()).thenReturn(true);
        when(subDirA.getAbsolutePath()).thenReturn("/input_dir/sub_dir_a");
        when(subDirA.getName()).thenReturn("sub_dir_a");
        when(subDirA.listFiles()).thenReturn(subDirAFileList);
        when(candidateFactory.createCandidate(subDirA, outputDir)).thenReturn(mockedCandidate);
        when(dirHandler.folderHasMultipleFoldersToScan(subDirA)).thenReturn(true);
        when(dirHandler.isValidSubfolder(subDirA, outputDir)).thenReturn(true);

        // subDirB setup
        when(subDirB.isDirectory()).thenReturn(true);
        when(subDirB.getAbsolutePath()).thenReturn("/input_dir/sub_dir_b");
        when(subDirB.getName()).thenReturn("sub_dir_b");
        when(subDirB.listFiles()).thenReturn(subDirBFileList);
        when(candidateFactory.createCandidate(subDirB, outputDir)).thenReturn(mockedEmptyCandidate);
        when(dirHandler.folderHasMultipleFoldersToScan(subDirB)).thenReturn(true);
        when(dirHandler.isValidSubfolder(subDirB, outputDir)).thenReturn(true);

        // dirWithFile setup
        when(dirWithFile.isDirectory()).thenReturn(true);
        when(dirWithFile.getAbsolutePath()).thenReturn("/something/dir_with_file");
        when(dirWithFile.getName()).thenReturn("dir_with_file");
        when(dirWithFile.listFiles()).thenReturn(listWithFile);
        when(candidateFactory.createCandidate(dirWithFile, outputDir)).thenReturn(mockedCandidate);
        when(dirHandler.folderHasMultipleFoldersToScan(dirWithFile)).thenReturn(false);
        when(dirHandler.isValidSubfolder(dirWithFile, outputDir)).thenReturn(true);

        // dirWithNoFiles setup
        when(dirWithNoFiles.isDirectory()).thenReturn(true);
        when(dirWithNoFiles.getAbsolutePath()).thenReturn("/something/dir_with_no_files");
        when(dirWithNoFiles.getName()).thenReturn("dir_with_no_files");
        when(dirWithNoFiles.listFiles()).thenReturn(new File[] {});
        when(candidateFactory.createCandidate(dirWithNoFiles, outputDir)).thenReturn(mockedEmptyCandidate);
        when(dirHandler.folderHasMultipleFoldersToScan(dirWithNoFiles)).thenReturn(false);
        when(dirHandler.isValidSubfolder(dirWithNoFiles, outputDir)).thenReturn(true);

        // mockedFile setup
        when(mockedFile.isFile()).thenReturn(true);
        when(mockedFile.isDirectory()).thenReturn(false);
        when(mockedFile.getAbsolutePath()).thenReturn("/something/dir_with_file/file.jpg");
        when(mockedFile.getName()).thenReturn("file1.jpg");

        when(dirHandler.buildTargetSubdirFile(Mockito.any(), Mockito.any(File.class))).thenReturn(outputDir);
        when(dirHandler.buildTargetBaseDirFile(Mockito.any(), Mockito.any(File.class))).thenReturn(outputDir);
        when(dirHandler.buildFile(Mockito.any(), Mockito.any(File.class))).thenReturn(outputDir);

        when(outputDir.getAbsolutePath()).thenReturn("/out/");

        when(configFolder.getInputFolder()).thenReturn(inputDir);
        when(configFolder.getOutputFolder()).thenReturn(outputDir);
    }

}
