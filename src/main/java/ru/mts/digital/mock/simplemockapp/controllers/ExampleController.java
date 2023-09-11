package ru.mts.digital.mock.simplemockapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mts.digital.mock.simplemockapp.core.DataService;
import ru.mts.digital.mock.simplemockapp.dto.DtoExample;
import ru.mts.digital.mock.simplemockapp.enums.Extension;

import java.io.IOException;

@RestController
@RequestMapping("example/api/v1")
public class ExampleController {

    @Autowired
    private DataService dataService;

    private final String BASE_PATH = "target/data/example_file_response";

    @GetMapping(value = "/external/v1/ert/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> example(
            @RequestBody DtoExample dtoExample,
            @PathVariable int id,
            @RequestParam(defaultValue = "test") String msisdn,
            @RequestHeader(required = false) HttpHeaders headers,
            @CookieValue(name = "user-id", defaultValue = "default-user-id") String userId) throws IOException {

        if (msisdn.startsWith("911")) {
            return dataService.getData(msisdn, BASE_PATH, Extension.XML.getExtension()).toResponseEntity();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                dtoExample.name + " " + dtoExample.age + " " + dtoExample.types.toString() + " | "
                        + msisdn + " | " + headers + " | " + userId);
    }
}
