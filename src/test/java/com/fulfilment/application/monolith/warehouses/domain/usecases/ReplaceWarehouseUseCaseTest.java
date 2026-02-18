package com.fulfilment.application.monolith.warehouses.domain.usecases;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ReplaceWarehouseUseCaseTest {

    private static final String PATH = "/warehouse";

    @Test
    void shouldReplaceWarehouseSuccessfully() {

        String body = """
                {
                    "location": "ZWOLLE-001",
                    "capacity": 150,
                    "stock": 10
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(PATH + "/MWH.001/replacement")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldFailIfWarehouseNotFound() {

        String body = """
                {
                    "location": "ZWOLLE-001",
                    "capacity": 150,
                    "stock": 10
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(PATH + "/INVALID_CODE/replacement")
                .then()
                .statusCode(500);
    }

    @Test
    void shouldFailIfCapacityTooSmall() {

        String body = """
                {
                    "location": "ZWOLLE-001",
                    "capacity": 5,
                    "stock": 10
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(PATH + "/MWH.001/replacement")
                .then()
                .statusCode(500);
    }

    @Test
    void shouldFailIfStockDoesNotMatch() {

        String body = """
                {
                    "location": "ZWOLLE-001",
                    "capacity": 150,
                    "stock": 20
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(PATH + "/MWH.001/replacement")
                .then()
                .statusCode(500);
    }

}