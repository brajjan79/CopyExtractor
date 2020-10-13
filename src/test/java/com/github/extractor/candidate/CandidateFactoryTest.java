package com.github.extractor.candidate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.handlers.*;
import com.github.extractor.utils.Dirs;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CandidateFactory.class, RarHandler.class, Dirs.class })
public class CandidateFactoryTest {

    private static final int LAST_MODIFIED_WAIT_TIME = 10000;
    private FileHandler fileHandler;
    private DirHandler dirHandler;
    private CandidateFactory candidateFactory;
    private File rarFile_1;
    private File rarFile_2;
    private File rarFile_3;
    private File subFolder;
    private File[] rarFolderList;
    private File[] subFolderList;
    private File sourceDir;
    private File targetDir;
    private File photoFile_1;
    private File photoFile_2;
    private File photoFile_3;

    @Before
    public void init() throws Exception {
        fileHandler = PowerMockito.mock(FileHandler.class);
        dirHandler = PowerMockito.mock(DirHandler.class);
        setupMockFolderFolders();
        candidateFactory = new CandidateFactory(fileHandler, dirHandler);
    }

    /*
     * createCandidate tests
     */

    @Test
    public void testCreateCandidateWithRarFilesNoSubdirectories() throws Throwable {
        when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);

        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertEquals("Number of rar files should be 2.", 2, candidate.filesToUnrar.size());
        assertEquals("Compare rarfile name.", rarFile_1.getName(),
            candidate.filesToUnrar.get(0).getName());
        assertEquals("Compare rarfile name.", rarFile_2.getName(),
            candidate.filesToUnrar.get(1).getName());
        assertTrue(candidate.filesToCopy.isEmpty());
    }

    @Test
    public void testCreateCandidateWithRarFilesWithSubdirectories() throws Throwable {
        PowerMockito.doReturn(true).when(dirHandler, "directoryIncluded", subFolder);

        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertEquals("Number of rar files should be 3.", 3, candidate.filesToUnrar.size());
        assertEquals("Compare rarfile name.", rarFile_1.getName(),
            candidate.filesToUnrar.get(0).getName());
        assertEquals("Compare rarfile name.", rarFile_2.getName(),
            candidate.filesToUnrar.get(1).getName());
        assertEquals("Compare rarfile name.", rarFile_3.getName(),
            candidate.filesToUnrar.get(2).getName());
        assertTrue(candidate.filesToCopy.isEmpty());
    }

    @Test
    public void testRarsFilesIgnored() throws Throwable {
        PowerMockito.doReturn(true).when(dirHandler, "directoryIncluded", subFolder);
        when(fileHandler.isIgnored(Mockito.any())).thenReturn(true);

        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
    }

    @Test
    public void testCreateCandidateWithFilesNoSubdirectories() throws Throwable {
        when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);
        when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);
        PowerMockito.doReturn(false).when(dirHandler, "directoryIncluded", subFolder);
        addPngFileTypeIncluded();

        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertEquals("Number of .png files should be 2.", 2, candidate.filesToCopy.size());
    }

    @Test
    public void testCreateCandidateWithFilesWithSubdirectories() throws Throwable {
        when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);
        when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);
        PowerMockito.doReturn(true).when(dirHandler, "directoryIncluded", subFolder);
        addPngFileTypeIncluded();

        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertEquals("Number of .png files should be 3.", 3, candidate.filesToCopy.size());
    }

    @Test
    public void testNoRarsAllFilesIgnored() throws Throwable {
        final File[] folderList = new File[] { photoFile_1, photoFile_2 };
        when(sourceDir.listFiles()).thenReturn(folderList);
        when(fileHandler.isIgnored(Mockito.any())).thenReturn(true);
        when(fileHandler.isIncludedFileType(Mockito.any())).thenReturn(true);
        when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);

        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
    }

    @Test
    public void testSubdirRecentlyModified() throws Throwable {
        PowerMockito.doReturn(true).when(Dirs.class, "lastModifiedLessThen", sourceDir, 10000);
        final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
        assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
    }

    /*
     * create createSingleFileCandidate
     */

    @Test
    public void testCreateSingleFileCandidateSubdirRecentlyModified() throws Throwable {
        PowerMockito.doReturn(true).when(Dirs.class, "lastModifiedLessThen", rarFile_1, 10000);
        final Candidate candidate = candidateFactory.createCandidate(rarFile_1, targetDir);
        assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
    }

    @Test
    public void testCreateSingleFileCandidateRarFile() throws Throwable {
        final File file = PowerMockito.mock(File.class);
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn("Test_file.rar");
        PowerMockito.doReturn(true).when(RarHandler.class, "fileIsUnrarable", file);

        when(fileHandler.isIgnored(file)).thenReturn(false);
        when(fileHandler.isIncludedFileType(file)).thenReturn(false);
        PowerMockito.doReturn(false).when(Dirs.class, "lastModifiedLessThen", file, LAST_MODIFIED_WAIT_TIME);

        final Candidate candidate = candidateFactory.createCandidate(file, targetDir);
        assertEquals("Compare rarfile name.", file.getName(), candidate.filesToUnrar.get(0).getName());
    }

    @Test
    public void testCreateSingleFileCandidatePngFile() throws Throwable {
        final File file = PowerMockito.mock(File.class);
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn("Test_file.png");
        PowerMockito.doReturn(false).when(RarHandler.class, "fileIsUnrarable", file);

        when(fileHandler.isIgnored(file)).thenReturn(false);
        when(fileHandler.isIncludedFileType(file)).thenReturn(true);
        PowerMockito.doReturn(false).when(Dirs.class, "lastModifiedLessThen", file, LAST_MODIFIED_WAIT_TIME);

        final Candidate candidate = candidateFactory.createCandidate(file, targetDir);
        assertEquals("Compare rarfile name.", file.getName(), candidate.filesToCopy.get(0).getName());
    }

    @Test
    public void testCreateSingleFileCandidateFileIsIgnored() throws Throwable {
        final File file = PowerMockito.mock(File.class);
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn("Test_file.png");
        PowerMockito.doReturn(true).when(RarHandler.class, "fileIsUnrarable", file);

        when(fileHandler.isIgnored(file)).thenReturn(true);
        when(fileHandler.isIncludedFileType(file)).thenReturn(true);
        PowerMockito.doReturn(false).when(Dirs.class, "lastModifiedLessThen", file, LAST_MODIFIED_WAIT_TIME);

        final Candidate candidate = candidateFactory.createCandidate(file, targetDir);
        assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
    }

    private void setupMockFolderFolders() throws Exception {
        PowerMockito.mockStatic(RarHandler.class);
        PowerMockito.mockStatic(Dirs.class);
        subFolder = PowerMockito.mock(File.class);
        sourceDir = PowerMockito.mock(File.class);
        targetDir = PowerMockito.mock(File.class);

        rarFile_1 = new File("/some_folder/rar1.rar");
        rarFile_2 = new File("/some_folder/rar2.rar");
        rarFile_3 = new File("/some_folder/subFolder/rar3.rar");
        photoFile_1 = new File("/some_folder/photo3.png");
        photoFile_2 = new File("/some_folder/photo3.png");
        photoFile_3 = new File("/some_folder/subFolder/photo3.png");
        subFolderList = new File[] { rarFile_3, photoFile_3 };
        rarFolderList = new File[] { rarFile_1, rarFile_2, subFolder, photoFile_1, photoFile_2 };

        when(sourceDir.listFiles()).thenReturn(rarFolderList);
        when(subFolder.listFiles()).thenReturn(subFolderList);

        PowerMockito.doReturn(true).when(RarHandler.class, "fileIsUnrarable", rarFile_1);
        PowerMockito.doReturn(true).when(RarHandler.class, "fileIsUnrarable", rarFile_2);
        PowerMockito.doReturn(true).when(RarHandler.class, "fileIsUnrarable", rarFile_3);
        PowerMockito.doReturn(false).when(RarHandler.class, "fileIsUnrarable", subFolder);

        PowerMockito.doReturn(false).when(Dirs.class, "lastModifiedLessThen", sourceDir, LAST_MODIFIED_WAIT_TIME);
    }

    private void addPngFileTypeIncluded() throws Exception {
        PowerMockito.doReturn(true).when(fileHandler, "isIncludedFileType", photoFile_1);
        PowerMockito.doReturn(true).when(fileHandler, "isIncludedFileType", photoFile_2);
        PowerMockito.doReturn(true).when(fileHandler, "isIncludedFileType", photoFile_3);
        PowerMockito.doReturn(false).when(fileHandler, "isIncludedFileType", subFolder);
    }
}
