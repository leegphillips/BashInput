package com.github.leegphillips.BashLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

public class BashLink
{
    private static final Logger LOG = LoggerFactory.getLogger(BashLink.class);

    private static final ExecutorService POOL = Executors.newFixedThreadPool(256);

    public List<File> process(File root, InputStream in) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(in))) {
            return buffer.lines()
                    .map(line -> addFile(root, line))
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(toList());
        }
    }

    private CompletableFuture<File> addFile(File root, String line) {
        LOG.debug("Supplied file: " + line);
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(root, line);
            LOG.debug("File found: " + file.getAbsolutePath());
            LOG.debug("Exists: " + file.exists());
            return file.exists() ? file : null;
        }, POOL);
    }

    public static void main( String[] args ) throws IOException {
        new BashLink().process(new File("."), System.in);
        POOL.shutdown();
    }
}
