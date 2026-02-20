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
public class ReplaceWarehouseUseCaseTest {

    private static final String PATH = "/warehouse";
    private static final String WAREHOUSE_ID = "MWH.001";

    @Inject
    WarehouseRepository warehouseRepository;

    @BeforeEach
    @Transactional
    public void setup() {
        warehouseRepository.deleteAll();

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = WAREHOUSE_ID;
        warehouse.location = "DEFAULT";
        warehouse.capacity = 100;
        warehouse.stock = 10;

        warehouseRepository.create(warehouse);
    }

    @Test
    void shouldReplaceWarehouseSuccessfully() {

        String body = """
                {
                    "location": "ZWOLLE-001",
                    "capacity": 150,
                    "stock": 10
                }
                """;

        given().contentType("application/json").body(body).when().post(PATH + "/" + WAREHOUSE_ID + "/replacement").then().statusCode(200);
    }

    @Test
    void shouldReturn404IfWarehouseNotFound() {

        String body = """
                {
                    "location": "ZWOLLE-001",
                    "capacity": 150,
                    "stock": 10
                }
                """;

        given().contentType("application/json").body(body).when().post(PATH + "/INVALID_CODE/replacement").then().statusCode(404);
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

        given().contentType("application/json").body(body).when().post(PATH + "/" + WAREHOUSE_ID + "/replacement").then().statusCode(500);
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

        given().contentType("application/json").body(body).when().post(PATH + "/" + WAREHOUSE_ID + "/replacement").then().statusCode(500);
    }
}