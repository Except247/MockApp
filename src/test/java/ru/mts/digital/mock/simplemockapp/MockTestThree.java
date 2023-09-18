package ru.mts.digital.mock.simplemockapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mts.digital.mock.simplemockapp.test_data.RequestData;

import java.io.IOException;
import java.net.http.HttpClient;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest
class MockTestThree {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    @Test
    void contextLoads() throws IOException, InterruptedException {

        var expected = new RequestData("89214956612", "89214956612", null, "", "921", "true");

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8888;
        RestAssured.basePath = "/external/v1/ert/" + expected.getEndpoint();


        var response = given().contentType(ContentType.HTML)
                .when().get(basePath)
                .then().statusCode(200)
                .and().body("msisdn", equalTo(expected.getMsisdn()))
                .and().body("name", equalTo(expected.getName()))
                .and().body("full_ta_resp", equalTo(expected.getFullTaResp()))
                .and().body("en", equalTo(expected.getEn()))
                .and().body("reg", equalTo(expected.getReg()))
                .and().body("hasStatus", equalTo(expected.getHasStatus()))
                .and().extract().response();
    }

}
