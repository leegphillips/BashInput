package com.github.leegphillips.BashLink;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class BashLinkTest {

    private final static String FILE_CONTENTS = "the quick brown fox jumped over the lazy dog";
    private final static int FILES_COUNT = 10;
    private final static String[] EXTENSIONS = {".txt", "", ".xml"};

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void createFiles() throws IOException {
        for (int i = 0; i < FILES_COUNT; i++) {
            File test = folder.newFile(i + EXTENSIONS[i % EXTENSIONS.length]);
            Files.write(test.toPath(), FILE_CONTENTS.substring(0, FILE_CONTENTS.length() - (2 * i)).getBytes());
        }
    }

    @Test
    public void test() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (File file : folder.getRoot().listFiles()) {
            builder.append(file.getName());
            builder.append("\n");
        }

        InputStream in = new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));

        assertEquals(FILES_COUNT, new BashLink().process(folder.getRoot(), in).size());
    }
}