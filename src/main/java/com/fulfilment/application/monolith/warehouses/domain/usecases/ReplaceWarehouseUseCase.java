package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;

    @Inject
    public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    @Override
    @Transactional
    public void replace(Warehouse newWarehouse) {

        if (newWarehouse.businessUnitCode == null || newWarehouse.businessUnitCode.isBlank()) {
            throw new IllegalArgumentException("Business Unit Code must not be null");
        }

        if (newWarehouse.capacity == null || newWarehouse.stock == null) {
            throw new IllegalArgumentException("Capacity and stock must not be null");
        }

        Warehouse existing = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

        if (existing == null) {
            throw new NotFoundException("Warehouse not found for replacement");
        }

        if (newWarehouse.capacity < existing.stock) {
            throw new IllegalStateException("New capacity cannot accommodate existing stock");
        }

        if (!newWarehouse.stock.equals(existing.stock)) {
            throw new IllegalStateException("New warehouse stock must match previous warehouse stock");
        }

        warehouseStore.remove(existing);

        // Create new warehouse
        newWarehouse.createdAt = LocalDateTime.now();
        newWarehouse.archivedAt = null;

        warehouseStore.create(newWarehouse);
    }

}