package ru.mts.digital.mock.simplemockapp.be;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class MockController {

    @GetMapping(value = "/external_x/v1/ert/{msisdn}/resp", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> x1(
            @PathVariable String msisdn,
            @RequestParam String name,
            @RequestParam(name = "full_ta_resp", required = false) String fullTaResp,
            @RequestParam(required = false) String en) throws IOException, InterruptedException {
        boolean isMsisdnValid = isValidField((msisdn != null ? msisdn : ""), "^(8|\\+79)[0-9]*$");
        int regionCode = isMsisdnValid ? getRegionCode(msisdn) : -1;

        JSONObject responseData = new JSONObject();

        responseData.put("msisdn", isMsisdnValid ? String.valueOf(msisdn) : "");
        responseData.put("name", !name.isEmpty() ? name : msisdn);
        responseData.put("full_ta_resp", fullTaResp);
        responseData.put("en", isValidField((en != null ? en : ""), "^(x|YY|s)$") ? en : "");
        responseData.put("reg", String.valueOf(regionCode));
        responseData.put("hasStatus", String.valueOf(hasStatus(regionCode)));

        return ResponseEntity.status(HttpStatus.OK).body(responseData.toString());
    }

    private static boolean isValidField(String value, String regex)
    {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    private int getRegionCode(String msisdn)
    {
        Pattern p = Pattern.compile("^(8|\\+79)([0-9]{3})[0-9]*$");
        Matcher m = p.matcher(String.valueOf(msisdn));
        return m.matches() ? Integer.parseInt(m.group(2)) : -1;
    }

    private boolean hasStatus(int code)
    {
        return code <= 100 || code >= 300;
    }
}