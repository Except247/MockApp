package ru.mts.digital.mock.simplemockapp.be;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class MockController {

    private final HashMap<String, String> responseData = new HashMap<String, String>();
    @GetMapping(value = "/external_x/v1/ert/{msisdn}/resp", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> x1(
            @PathVariable String msisdn,
            @RequestParam String name,
            @RequestParam(required = false) String ful_ta_resp,
            @RequestParam(required = false) String en) throws IOException, InterruptedException {
        boolean msisdnValid = isValidReg(String.valueOf(msisdn), "^(8|\\+79)[0-9]*$");
        boolean enValid = isValidReg(String.valueOf(en), "^(x|YY|s)$");
        String regionCode = msisdnValid ? getRegionCode(msisdn) : "";
        boolean hasStatus = hasStatus(Integer.parseInt(regionCode));
        responseData.put("msisdn", msisdnValid ? String.valueOf(msisdn) : "");
        responseData.put("name", !Objects.equals(name, "") ? name : msisdn);
        responseData.put("ful_ta_resp", ful_ta_resp);
        /* Убрать отрицание в условии en но в Be будет 500 ошибка */
        responseData.put("en", enValid ? en : "");
        responseData.put("reg", regionCode);
        /* В ТЗ поле reg, а в Be классе поле code. */
        // responseData.put("code", regionCode);
        responseData.put("hasStatus", String.valueOf(hasStatus));
        Gson gson = new Gson();
        Type typeObject = new TypeToken<HashMap<String, String>>() {}.getType();
        String gsonData = gson.toJson(responseData, typeObject);
        return new ResponseEntity<>(gsonData, HttpStatus.OK);
    }

    private static boolean isValidReg(String value, String regex)
    {
        Pattern p = Pattern.compile(regex);
        if (value == null) {
            return false;
        }
        Matcher m = p.matcher(value);
        return m.matches();
    }

    private String getRegionCode(String msisdn)
    {
        Pattern p = Pattern.compile("^(8|\\+7)([0-9]{3})[0-9]*$");
        Matcher m = p.matcher(String.valueOf(msisdn));
        return m.matches() ? m.group(2) : "";
    }

    private boolean hasStatus(int code)
    {
        return code <= 100 || code >= 300;
    }
}
