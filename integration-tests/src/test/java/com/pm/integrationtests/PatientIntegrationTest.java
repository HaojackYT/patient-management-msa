package com.pm.integrationtests;

import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

public class PatientIntegrationTest {

    @BeforeAll
    static void setup() {
        int apiGatewayPort = Integer.parseInt(
                System.getenv().getOrDefault("API_GATEWAY_PORT", "4003"));
        RestAssured.baseURI = "http://localhost:" + apiGatewayPort;
    }

    @Test
    void shouldReturnPatientsWithValidToken() {

        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """;

        String token = RestAssured.given()
                // 1. Arrange
                .contentType("application/json")
                .body(loginPayload)
                // 2. Act
                .when()
                .post("/auth/login")
                // 3. Assert
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        RestAssured.given()
                // 1. Arrange
                .header("Authorization", "Bearer " + token)
                // 2. Act
                .when()
                .get("/api/patients")
                // 3. Assert
                .then()
                .statusCode(200)
                .body("patients", notNullValue());
    }
}
