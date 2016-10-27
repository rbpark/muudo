package io.muudo.metastore.data;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommitLoggerTest {
    private static final Logger log = LoggerFactory.getLogger(CommitLoggerTest.class);

    @Before
    public void setup() throws Exception {
        // Cleanup first file
        File file = new File("doesnotexist");
        if (file.exists()) {
            FileUtils.forceDelete(new File("doesnotexist"));
        }
    }

    @Test
    public void testBootstrapDirectoryCreation() throws IOException {
        CommitLogger commitLogger = CommitLogger.builder()
                .directory("doesnotexist")
                .name("dataset")
                .build();

        Path commitPath = commitLogger.getDirectory();
        assertFalse(Files.exists(commitPath));

        commitLogger.bootstrap();
        commitPath = commitLogger.getDirectory();
        assertTrue(Files.exists(commitPath));

        // Delete the path for cleanup.
        FileUtils.forceDelete(commitPath.toFile());
    }

    @Test
    public void testBootstrap() throws IOException {
        log.info("Creating commit log files");
        Path path = FileSystems.getDefault().getPath("test/testcommitlog");
        Files.createDirectories(path);

        Path logfile1 = Files.createFile(path.resolve("testing-1.log"));
        Path logfile2 = Files.createFile(path.resolve("testing-2.log"));

        CommitLogger commitLogger = CommitLogger.builder()
                .directory("test/testcommitlog")
                .name("testing")
                .build();
        commitLogger.bootstrap();

        log.info("Cleaning up commit log files");
        // Cleanup
        Files.deleteIfExists(logfile1);
        Files.deleteIfExists(logfile2);
        Files.deleteIfExists(path);
    }
}
