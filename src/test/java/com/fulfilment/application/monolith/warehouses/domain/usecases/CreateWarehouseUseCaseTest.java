package com.fulfilment.application.monolith.warehouses.domain.usecases;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CreateWarehouseUseCaseTest {

    private static final String BASE_PATH = "/warehouse";

    @Test
    public void shouldCreateWarehouseSuccessfully() {

        given()
                .contentType("application/json")
                .body("""
                        {
                          "businessUnitCode": "NEW.001",
                          "location": "AMSTERDAM-001",
                          "capacity": 20,
                          "stock": 10
                        }
                        """)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(anyOf(is(200), is(201)));
    }

    @Test
    public void shouldFailIfBusinessUnitAlreadyExists() {

        // First creation
        given()
                .contentType("application/json")
                .body("""
                        {
                          "businessUnitCode": "DUP.001",
                          "location": "AMSTERDAM-001",
                          "capacity": 20,
                          "stock": 10
                        }
                        """)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(anyOf(is(200), is(201)));

        // Duplicate creation
        given()
                .contentType("application/json")
                .body("""
                        {
                          "businessUnitCode": "DUP.001",
                          "location": "AMSTERDAM-001",
                          "capacity": 20,
                          "stock": 10
                        }
                        """)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(500); // because IllegalStateException → no mapper
    }


}