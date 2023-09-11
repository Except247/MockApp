package ru.mts.digital.mock.simplemockapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class SimpleMockAppApplication {
    public static void main(String[] args) throws IOException, URISyntaxException {
        new ApplicationStartup().init();
        SpringApplication.run(SimpleMockAppApplication.class, args);
    }
}
