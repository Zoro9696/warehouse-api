package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class StoreResourceTest {

    // --------------------------------------------------
    // Helper Record
    // --------------------------------------------------

    record StoreData(Long id, String name) {}

    // --------------------------------------------------
    // Helper Method (creates unique store)
    // --------------------------------------------------

    private StoreData createStore() {

        String uniqueName = "Store-" + System.nanoTime();

        Long id =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                {
                                  "name":"%s",
                                  "quantityProductsInStock":5
                                }
                                """.formatted(uniqueName))
                        .when()
                        .post("/store")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        return new StoreData(id, uniqueName);
    }

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void shouldCreateStore() {

        String name = "Store-" + System.nanoTime();

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name":"%s",
                          "quantityProductsInStock":10
                        }
                        """.formatted(name))
                .when()
                .post("/store")
                .then()
                .statusCode(201)
                .body("name", equalTo(name));
    }

    // --------------------------------------------------
    // GET ALL
    // --------------------------------------------------

    @Test
    void shouldGetAllStores() {

        createStore();

        given()
                .when()
                .get("/store")
                .then()
                .statusCode(200);
    }

    // --------------------------------------------------
    // GET SINGLE SUCCESS
    // --------------------------------------------------

    @Test
    void shouldGetSingleStore() {

        StoreData store = createStore();

        given()
                .when()
                .get("/store/" + store.id())
                .then()
                .statusCode(200)
                .body("name", equalTo(store.name()));
    }

    // --------------------------------------------------
    // GET SINGLE NOT FOUND
    // --------------------------------------------------

    @Test
    void shouldReturn404IfStoreMissing() {

        given()
                .when()
                .get("/store/999999")
                .then()
                .statusCode(404);
    }

    // --------------------------------------------------
    // UPDATE
    // --------------------------------------------------

    @Test
    void shouldUpdateStore() {

        StoreData store = createStore();

        String updatedName = "Updated-" + System.nanoTime();

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name":"%s",
                          "quantityProductsInStock":20
                        }
                        """.formatted(updatedName))
                .when()
                .put("/store/" + store.id())
                .then()
                .statusCode(200)
                .body("name", equalTo(updatedName));
    }

    // --------------------------------------------------
    // PATCH
    // --------------------------------------------------

    @Test
    void shouldPatchStore() {

        StoreData store = createStore();

        String patchedName = "Patched-" + System.nanoTime();

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name":"%s"
                        }
                        """.formatted(patchedName))
                .when()
                .patch("/store/" + store.id())
                .then()
                .statusCode(200)
                .body("name", equalTo(patchedName));
    }

    // --------------------------------------------------
    // DELETE
    // --------------------------------------------------

    @Test
    void shouldDeleteStore() {

        StoreData store = createStore();

        given()
                .when()
                .delete("/store/" + store.id())
                .then()
                .statusCode(204);
    }
}