package com.katbrew.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class FilesystemHelper {

    public static void deleteAll(final Path path) {
        if (Files.exists(path)) {
            try {
                if (path.toFile().isFile()) {
                    Files.delete(path);
                } else {
                    for (final File single : Objects.requireNonNullElse(path.toFile().listFiles(), new File[]{})) {
                        Files.delete(single.toPath());
                    }
                    Files.delete(path);
                }
            } catch (final IOException e) {
                log.error("Failed to delete file");
            }
        }
    }
}
