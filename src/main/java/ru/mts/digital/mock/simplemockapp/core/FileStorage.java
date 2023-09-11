package ru.mts.digital.mock.simplemockapp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.mts.digital.mock.simplemockapp.errors.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Component
public class FileStorage {
    private final static Logger log = LoggerFactory.getLogger(FileStorage.class);

    public String readFile(Path path) throws IOException {
        log.debug("File search in {}", path);
        if (!Files.exists(path)) {
            throw new NotFoundException("Not found :" + path);
        }
        return Files.readAllLines(path).stream().map(String::trim).collect(Collectors.joining(" "));
    }
}