package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.fulfilment.FulfilmentAssignment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.FulfilmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AssignWarehouseUseCase {

    @Inject
    FulfilmentRepository repository;

    @Transactional
    public void assign(DbWarehouse warehouse, Product product, Store store) {

        if (repository.countWarehousesByStoreAndProduct(store, product) >= 2) {
            throw new IllegalStateException("Product can be fulfilled by max 2 warehouses per store");
        }

        if (repository.countWarehousesByStore(store) >= 3) {
            throw new IllegalStateException("Store can be fulfilled by max 3 warehouses");
        }

        if (repository.countProductsByWarehouse(warehouse) >= 5) {
            throw new IllegalStateException("Warehouse can store max 5 product types");
        }

        FulfilmentAssignment assignment = new FulfilmentAssignment();
        assignment.warehouse = warehouse;
        assignment.product = product;
        assignment.store = store;
        assignment.createdAt = java.time.LocalDateTime.now();

        repository.persist(assignment);
    }
}

