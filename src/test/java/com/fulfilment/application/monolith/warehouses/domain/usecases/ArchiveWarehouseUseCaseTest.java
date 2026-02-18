package com.fulfilment.application.monolith.warehouses.domain.usecases;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ArchiveWarehouseUseCaseTest {

    @Test
    public void shouldArchiveWarehouseSuccessfully() {

        given()
                .when()
                .get("/warehouse/MWH.001")
                .then()
                .statusCode(200);

        given()
                .when()
                .delete("/warehouse/MWH.001")
                .then()
                .statusCode(204);
    }

    @Test
    public void shouldReturnErrorWhenWarehouseNotFound() {

        given()
                .when()
                .delete("/warehouse/UNKNOWN")
                .then()
                .statusCode(anyOf(is(404), is(500)));
    }

}