package com.fulfilment.application.monolith.warehouses.adapters.database;


import com.fulfilment.application.monolith.fulfilment.FulfilmentAssignment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

@ApplicationScoped
public class FulfilmentRepository implements PanacheRepository<FulfilmentAssignment> {

    public long countWarehousesByStoreAndProduct(Store store, Product product) {
        return find("store = ?1 and product = ?2", store, product).project(FulfilmentAssignment.class).stream().map(f -> f.warehouse.id).distinct().count();
    }

    public long countWarehousesByStore(Store store) {
        return find("store", store).stream().map(f -> f.warehouse.id).distinct().count();
    }

    public long countProductsByWarehouse(DbWarehouse warehouse) {
        return find("warehouse", warehouse).stream().map(f -> f.product.id).distinct().count();
    }
}

