package com.github.extractor.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.utils.Dirs;
import com.github.filesize.FileSize;
import com.github.junrar.ContentDescription;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RarHandler.class, Junrar.class, FileSize.class })
public class RarHandlerTest {

    @Test
    public void testInit() {
        try {
            new RarHandler();
        } catch (final Exception e) {
            fail("Failed to initiate RarHandler");
        }
    }

    @Test
    public void testExtractCandidate() throws Throwable {
        final File targetDir = new File("src/test/resources/files/target_RarHandlerTest");
        final File file = new File("src/test/resources/files/rar_files/rar_file_1.rar");

        final Candidate candidate = new Candidate("name", targetDir);
        candidate.filesToUnrar.add(file);

        Dirs.createDirs(targetDir);
        assertTrue("Target dir should be empty before extraction.", isEmpty(targetDir));
        RarHandler.unrarFiles(candidate);
        assertFalse("Target dir should not be empty after extraction.", isEmpty(targetDir));
        Dirs.deleteDirs(targetDir);
        assertTrue("Target dir should be empty when test finishes.", isEmpty(targetDir));
    }

    @Test
    public void testExtractCandidateFailureIOException() throws Throwable {
        final File targetDir = new File("src/test/resources/files/target_RarHandlerTest");
        final File file = new File("src/test/resources/files/rar_files/rar_file_1.rar");

        final Candidate candidate = new Candidate("name", targetDir);
        candidate.filesToUnrar.add(file);

        mockStatic(Junrar.class);
        when(Junrar.getContentsDescription(file)).thenReturn(new ArrayList<>());
        when(Junrar.extract(file, targetDir)).thenThrow(new IOException(""));

        final boolean result = RarHandler.unrarFiles(candidate);
        assertFalse("Unrar should throw IOException", result);
    }

    @Test
    public void testExtractCandidateFailureRARException() throws Throwable {
        final File targetDir = new File("src/test/resources/files/target_RarHandlerTest");
        final File file = new File("src/test/resources/files/rar_files/rar_file_1.rar");

        final Candidate candidate = new Candidate("name", targetDir);
        candidate.filesToUnrar.add(file);

        mockStatic(Junrar.class);
        when(Junrar.getContentsDescription(file)).thenReturn(new ArrayList<>());
        when(Junrar.extract(file, targetDir)).thenThrow(new RarException());

        final boolean result = RarHandler.unrarFiles(candidate);
        assertFalse("Unrar should throw IOException", result);
    }

    @Test
    public void testExtractCandidateFailureFilesExist() throws Throwable {
        final File targetDir = new File("src/test/resources/files/target_RarHandlerTest");
        final File rarFile = new File("src/test/resources/files/rar_files/rar_file_1.rar");

        final Candidate candidate = new Candidate("name", targetDir);
        candidate.filesToUnrar.add(rarFile);

        final ContentDescription description = mock(ContentDescription.class);
        description.path = "";
        final List<ContentDescription> descriptions = new ArrayList<>();
        descriptions.add(description);

        mockStatic(Junrar.class);
        when(Junrar.getContentsDescription(rarFile)).thenReturn(descriptions);

        final File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        whenNew(File.class).withAnyArguments().thenReturn(file);

        final FileSize fileSize = mock(FileSize.class);
        when(fileSize.getBytes()).thenReturn(10000000.0);
        mockStatic(FileSize.class);
        when(FileSize.size(file)).thenReturn(fileSize);

        final boolean result = RarHandler.unrarFiles(candidate);
        assertFalse("Unrar should faild due to file exist.", result);
    }

    @Test
    public void testFileIsUnrarableNormalRar() {
        final File file = new File("/path/to/file/file.rar");
        final boolean result = RarHandler.fileIsUnrarable(file);
        assertTrue("File is RAR.", result);
    }

    @Test
    public void testFileIsNotUnrarableFileNotRar() {
        final File file = new File("/path/to/file/file.png");
        final boolean result = RarHandler.fileIsUnrarable(file);
        assertFalse("File is not RAR.", result);
    }

    @Test
    public void testFileIsUnrarableRarWithPart1() {
        final File file = new File("/path/to/file/file.part001.rar");
        final boolean result = RarHandler.fileIsUnrarable(file);
        assertTrue("File is part001 and RAR.", result);
    }

    @Test
    public void testFileIsUnrarableRarWithPart2() {
        final File file = new File("/path/to/file/file.part01.rar");
        final boolean result = RarHandler.fileIsUnrarable(file);
        assertTrue("File is part01 and RAR.", result);
    }

    @Test
    public void testFileIsNotUnrarableRarWithPartNotFirst1() {
        final File file = new File("/path/to/file/file.part002.rar");
        final boolean result = RarHandler.fileIsUnrarable(file);
        assertFalse("File is part002 and RAR.", result);
    }

    @Test
    public void testFileIsNotUnrarableRarWithPartNotFirst2() {
        final File file = new File("/path/to/file/file.part02.rar");
        final boolean result = RarHandler.fileIsUnrarable(file);
        assertFalse("File is part02 and RAR.", result);
    }

    @Test
    public void testDirContainsRar() {
        final File file = new File("/path/to/file/file.rar");
        final File[] fileList = new File[] { file };

        final File dir = mock(File.class);
        when(dir.listFiles()).thenReturn(fileList);

        final boolean result = RarHandler.dirContainsUnrarable(dir);
        assertTrue("Dir contains rar files.", result);
    }

    @Test
    public void testDirDoesNotContainsRar() {
        final File file = new File("/path/to/file/file.jpg");
        final File[] fileList = new File[] { file };

        final File dir = mock(File.class);
        when(dir.listFiles()).thenReturn(fileList);

        final boolean result = RarHandler.dirContainsUnrarable(dir);
        assertFalse("Dir contains rar files.", result);
    }

    private boolean isEmpty(final File dir) {
        if (!dir.exists()) {
            return true;
        }
        return dir.listFiles().length == 0;
    }
}
