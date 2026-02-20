package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreEventListener {

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    public void onStoreChanged(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreChangedEvent event) {
        if (event.getOperation() == StoreChangedEvent.Operation.CREATE) {
            legacyStoreManagerGateway.createStoreOnLegacySystem(event.getStore());
        } else if (event.getOperation() == StoreChangedEvent.Operation.UPDATE) {
            legacyStoreManagerGateway.updateStoreOnLegacySystem(event.getStore());
        }
    }

}