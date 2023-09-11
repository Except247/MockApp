package ru.mts.digital.mock.simplemockapp.be;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class Be {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    @GetMapping(value = "/external/v1/ert/{msisdn}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> x1(
            @PathVariable int msisdn,
            @RequestParam(defaultValue = "msisdn") String name,
            @RequestParam(required = false) String fullTaResp,
            @RequestParam(required = false, defaultValue = "x") String en) throws IOException, InterruptedException {

        HttpRequest get = HttpRequest.newBuilder(
                URI.create("http://localhost:8888/external_x/v1/ert/" + msisdn + "?name=" + name + "&full_ta_resp=" + fullTaResp + "&en=" + en.toLowerCase()))
                .header("Content-Type", "text/xml")
                .GET().build();

        HttpResponse<String> response = httpClient.send(get, HttpResponse.BodyHandlers.ofString());

        JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();

        if (!responseBody.get("msisdn").getAsString().equals(String.valueOf(msisdn))) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        if (!responseBody.get("name").getAsString().equals(String.valueOf(name))) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }

        if (responseBody.get("en").getAsString().equals(en)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        if (fullTaResp != null && !responseBody.get("fullTaResp").isJsonNull() && !responseBody.get("fullTaResp").getAsString().equals(fullTaResp)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        if (!responseBody.get("hasStatus").getAsBoolean()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        if (responseBody.get("code").getAsInt() == 100 && responseBody.get("hasStatus").getAsBoolean()) {
            return ResponseEntity.status(HttpStatus.OK).body("");
        }


        return ResponseEntity.status(HttpStatus.OK).body(responseBody.toString());
    }
}
