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

    @GetMapping(value = "/external/v1/ert/{msisdn}/resp", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> x1(
            @PathVariable String msisdn,
            @RequestParam(required = false) String name,
            @RequestParam(name = "full_ta_resp", required = false) String fullTaResp,
            @RequestParam(required = false) String en) throws IOException, InterruptedException {

        if (name == null) {
            name = msisdn;
        }

        String req = "http://localhost:8888/external_x/v1/ert/" + msisdn + "/resp?";
        req += "name=" + name.replace("+", "%2B");
        if (fullTaResp != null) {
            req += "&full_ta_resp=" + fullTaResp;
        }
        if (en != null) {
            req += "&en=" + en;
        }

        HttpRequest get = HttpRequest.newBuilder(
                        URI.create(req))
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

        if (!responseBody.get("full_ta_resp").getAsString().equals(fullTaResp)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        if (!responseBody.get("en").getAsString().equals(en)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        if (responseBody.get("reg").getAsInt() == 100) {
            return ResponseEntity.status(HttpStatus.OK).body(responseBody.toString());
        }

        if (!responseBody.get("hasStatus").getAsBoolean()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseBody.toString());
    }
}
