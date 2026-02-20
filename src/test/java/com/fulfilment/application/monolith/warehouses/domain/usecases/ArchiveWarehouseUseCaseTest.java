package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ArchiveWarehouseUseCaseTest {

    private static final String WAREHOUSE_ID = "MWH.001";

    @Inject
    WarehouseRepository warehouseRepository;

    @BeforeEach
    @Transactional
    public void setup() {
        // Clear any existing warehouse and insert a known warehouse
        warehouseRepository.deleteAll();

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = WAREHOUSE_ID;
        warehouse.location = "DEFAULT";
        warehouse.capacity = 100;
        warehouse.stock = 10;

        warehouseRepository.create(warehouse);
    }

    @Test
    public void shouldArchiveWarehouseSuccessfully() {
        // Archive the warehouse (DELETE)
        given()
                .when()
                .delete("/warehouse/" + WAREHOUSE_ID)
                .then()
                .statusCode(204);

        // Verify the warehouse no longer exists
        given()
                .when()
                .get("/warehouse/" + WAREHOUSE_ID)
                .then()
                .statusCode(404); // warehouse removed
    }

    @Test
    public void shouldReturn404WhenWarehouseNotFound() {
        // Attempt to archive a non-existing warehouse
        given()
                .when()
                .delete("/warehouse/UNKNOWN")
                .then()
                .statusCode(404); // Using NotFoundException in your code
    }
}

