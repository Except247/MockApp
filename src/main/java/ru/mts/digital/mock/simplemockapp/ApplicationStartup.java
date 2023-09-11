package ru.mts.digital.mock.simplemockapp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;

public class ApplicationStartup {

    public void init() throws IOException, URISyntaxException {
        extractResource("example_file_response");
    }

    private void extractResource(String resource) throws IOException, URISyntaxException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url != null) {
            final Path targetDir = Paths.get("target", "data", resource);
            if (Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            if (url.toString().startsWith("file:")) {
                final Path dir = Paths.get(url.toURI());
                Files.list(dir).forEach(path -> {
                    try {
                        final Path dest = Paths.get(targetDir.toString(), path.getFileName().toString());
                        if (Files.notExists(dest)) {
                            Files.copy(path, dest);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            } else {
                FileSystem fs;
                try {
                    fs = FileSystems.getFileSystem(url.toURI());
                } catch (FileSystemNotFoundException ee) {
                    fs = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap());
                }

                Files.list(fs.getPath("BOOT-INF/classes", resource)).forEach(path -> {
                    try {
                        final Path dest = Paths.get(targetDir.toString(), path.getFileName().toString());
                        if (Files.notExists(dest)) {
                            Files.copy(path, dest);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
