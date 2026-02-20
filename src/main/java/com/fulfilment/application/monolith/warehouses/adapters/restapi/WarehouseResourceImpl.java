package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    private WarehouseRepository warehouseRepository;

    @Inject
    private CreateWarehouseOperation createWarehouseOperation;

    @Inject
    private ReplaceWarehouseOperation replaceWarehouseOperation;

    @Inject
    private ArchiveWarehouseOperation archiveWarehouseOperation;

    @Inject
    private WarehouseStore warehouseStore;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
    }

    @Override
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
        var domainWarehouse = toDomain(data);

        createWarehouseOperation.create(domainWarehouse);

        return toWarehouseResponse(domainWarehouse);
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {
        var warehouse = warehouseStore.findByBusinessUnitCode(id);

        if (warehouse == null) {
            throw new NotFoundException("Warehouse not found");
        }

        return toWarehouseResponse(warehouse);
    }

    @Override
    public void archiveAWarehouseUnitByID(String id) {
        var warehouse = warehouseStore.findByBusinessUnitCode(id);

        if (warehouse == null) {
            throw new NotFoundException("Warehouse not found");
        }

        archiveWarehouseOperation.archive(warehouse);
    }

    @Override
    public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode,
                                                      @NotNull Warehouse data) {

        var domainWarehouse = toDomain(data);
        domainWarehouse.businessUnitCode = businessUnitCode;

        replaceWarehouseOperation.replace(domainWarehouse);

        return toWarehouseResponse(domainWarehouse);

    }


    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {

        var response = new Warehouse();
        response.setBusinessUnitCode(warehouse.businessUnitCode);
        response.setLocation(warehouse.location);
        response.setCapacity(warehouse.capacity);
        response.setStock(warehouse.stock);

        return response;
    }

    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(
            Warehouse data) {

        var domain = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        domain.businessUnitCode = data.getBusinessUnitCode();
        domain.location = data.getLocation();
        domain.capacity = data.getCapacity();
        domain.stock = data.getStock();

        return domain;
    }
}