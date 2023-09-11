package ru.mts.digital.mock.simplemockapp.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mts.digital.mock.simplemockapp.dto.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DataService {

    @Autowired
    private FileStorage storage;

    public Response getData(String name, String pathToFolder, String fileType) throws IOException {
        Path path = Paths.get(pathToFolder, name + fileType);
        var body = Files.exists(path) ? storage.readFile(path) : storage.readFile(Paths.get(pathToFolder, "default" + fileType));
        return new Response(200, body);
    }


}
