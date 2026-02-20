package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    @Inject
    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    @Transactional
    public void create(Warehouse warehouse) {

        if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
            throw new IllegalArgumentException("Business Unit Code must not be null");
        }

        Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);

        if (existing != null && existing.archivedAt == null) {
            throw new IllegalStateException("Warehouse with this Business Unit Code already exists");
        }

        var location = locationResolver.resolveByIdentifier(warehouse.location);

        long activeWarehousesAtLocation = warehouseStore.getAll().stream().filter(w -> w.location.equals(warehouse.location)).filter(w -> w.archivedAt == null).count();

        if (activeWarehousesAtLocation >= location.getMaxNumberOfWarehouses()) {
            throw new IllegalStateException("Maximum warehouses reached for this location");
        }

        if (warehouse.capacity > location.getMaxCapacity()) {
            throw new IllegalStateException("Capacity exceeds location maximum capacity");
        }

        if (warehouse.stock > warehouse.capacity) {
            throw new IllegalStateException("Stock cannot exceed capacity");
        }

        warehouse.createdAt = java.time.LocalDateTime.now();
        warehouse.archivedAt = null;

        warehouseStore.create(warehouse);
    }
}