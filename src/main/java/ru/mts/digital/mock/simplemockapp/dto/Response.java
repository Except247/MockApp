package ru.mts.digital.mock.simplemockapp.dto;

import org.springframework.http.ResponseEntity;

public class Response {
    private int status;
    private String body;

    public Response(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public ResponseEntity<?> toResponseEntity() {
        return ResponseEntity
                .status(status)
                .body(body);
    }
}