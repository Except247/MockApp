package ru.mts.digital.mock.simplemockapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest
class MockTestAPI {

    @BeforeAll
    static void setRestAssuredParams(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8888;
    }
    @BeforeEach
    void setEndpointParams(){
        RestAssured.basePath = "/external/v1/ert/";
    }

    @Test
    void positiveTest() throws IOException, InterruptedException {

        RestAssured.basePath += "89262611515/resp?full_ta_resp=x&en=x&name=89262611515";

        given().contentType(ContentType.HTML)
                .when().get(basePath)
                .then().statusCode(200)
                .and().body("msisdn", equalTo("89262611515"))
                .and().body("name", equalTo("89262611515"))
                .and().body("full_ta_resp", equalTo("x"))
                .and().body("en", equalTo("x"))
                .and().body("reg", equalTo("926"))
                .and().body("hasStatus", equalTo("true"))
                .and().extract().response();
    }

    @Test
    void forbiddenRequestTest() throws IOException, InterruptedException {

        RestAssured.basePath += "+791502601010/resp?full_ta_resp=x&en=YY&name=+791502601010";

        given().contentType(ContentType.HTML)
                .when().get(basePath)
                .then().statusCode(403);
    }

    @Test
    void hasStatusIgnoreTest() throws IOException, InterruptedException {

        RestAssured.basePath += "81004956612/resp?full_ta_resp=&en=&name=81004956612";


        given().contentType(ContentType.HTML)
                .when().get(basePath)
                .then().statusCode(200)
                .and().body("msisdn", equalTo("81004956612"))
                .and().body("name", equalTo("81004956612"))
                .and().body("full_ta_resp", equalTo(""))
                .and().body("en", equalTo(""))
                .and().body("reg", equalTo("100"))
                .and().body("hasStatus", equalTo("true"))
                .and().extract().response();
    }

}
