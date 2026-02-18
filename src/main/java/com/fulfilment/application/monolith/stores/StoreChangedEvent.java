package com.fulfilment.application.monolith.stores;

public class StoreChangedEvent {

    public enum Operation {
        CREATE,
        UPDATE
    }

    private final Store store;
    private final Operation operation;

    public StoreChangedEvent(Store store, Operation operation) {
        this.store = store;
        this.operation = operation;
    }

    public Store getStore() {
        return store;
    }
    public Operation getOperation() {
        return operation;
    }

}