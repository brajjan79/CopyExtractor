package com.github.extractor.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.utils.Dirs;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.github.extractor.handlers.*")
public class CopyHandlerTest {

    private File targetDir;

    @Before
    public void init() {
        targetDir = new File("src/test/resources/files/target_CopyHandlerTest");
        Dirs.deleteDirs(targetDir);
    }

    @Test
    public void testCopyFiles() throws Exception {
        Dirs.createDirs(targetDir);
        assertTrue(isEmpty(targetDir));

        final File file = new File("src/test/resources/files/rar_files/rar_file_1.rar");
        final Candidate candidate = new Candidate("name", targetDir);
        candidate.filesToCopy.add(file);
        CopyHandler.copyFiles(candidate);

        assertFalse("Target dir should not be empty after extraction.", isEmpty(targetDir));

        Dirs.deleteDirs(targetDir);
        assertTrue("Target dir should be empty when test finishes.", isEmpty(targetDir));

    }

    private boolean isEmpty(final File dir) {
        if (!dir.exists()) {
            return true;
        }
        return dir.listFiles().length == 0;
    }

}
