package com.pm.integrationtests;

import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthIntegrationTest {

    @BeforeAll
    static void setup() {
        int apiGatewayPort = Integer.parseInt(
                System.getenv().getOrDefault("API_GATEWAY_PORT", "4003"));
        RestAssured.baseURI = "http://localhost:" + apiGatewayPort;
    }

    // should + Return{Expected_Outcome} + With{Data}
    @Test
    void shouldReturnOkWithValidLogin() { // the happy path: all the input, response data are valid

        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """;

        Response response = RestAssured.given()
                // 1. Arrange: setup the environment so that the test can work 100% of the time
                .contentType("application/json")
                .body(loginPayload)
                // 2. Act: trigger the thing to test
                .when()
                .post("/auth/login")
                // 3. Assert: the result from stage 2
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract() // the response from the request
                .response(); // get the actual response object

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidLogin() { // the unhappy path

        String loginPayload = """
                    {
                        "email": "invalidtestuser@test.com",
                        "password": "wrongpassword"
                    }
                """;

        RestAssured.given()
                // 1. Arrange
                .contentType("application/json")
                .body(loginPayload)
                // 2. Act
                .when()
                .post("/auth/login")
                // 3. Assert
                .then()
                .statusCode(401);
    }
}