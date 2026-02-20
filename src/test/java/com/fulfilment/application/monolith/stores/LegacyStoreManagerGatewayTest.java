package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
class LegacyStoreManagerGatewayTest {

    @Inject
    LegacyStoreManagerGateway gateway;

    private Store createStore() {
        Store store = new Store();
        store.name = "Gateway-" + System.nanoTime();
        store.quantityProductsInStock = 10;
        return store;
    }

    // --------------------------------------------------
    // CREATE STORE FLOW
    // --------------------------------------------------

    @Test
    void shouldCreateStoreOnLegacySystem() {

        Store store = createStore();

        assertDoesNotThrow(() ->
                gateway.createStoreOnLegacySystem(store)
        );
    }

    // --------------------------------------------------
    // UPDATE STORE FLOW
    // --------------------------------------------------

    @Test
    void shouldUpdateStoreOnLegacySystem() {

        Store store = createStore();

        assertDoesNotThrow(() ->
                gateway.updateStoreOnLegacySystem(store)
        );
    }
}
