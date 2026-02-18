package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    // TODO Auto-generated method stub
    DbWarehouse entity = DbWarehouse.from(warehouse);
    persist(entity);
  }

  @Override
  public void update(Warehouse warehouse) {
    // TODO Auto-generated method stub

    DbWarehouse entity = find("businessUnitCode", warehouse.businessUnitCode)
            .firstResult();

    if (entity == null) {
      throw new IllegalStateException("Warehouse not found");
    }

    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;

    persist(entity);
  }

  @Override
  public void remove(Warehouse warehouse) {
    // TODO Auto-generated method stub

    DbWarehouse entity = find("businessUnitCode", warehouse.businessUnitCode)
            .firstResult();

    if (entity != null) {
      delete(entity);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    // TODO Auto-generated method stub

    DbWarehouse entity = find("businessUnitCode", buCode).firstResult();

    if (entity == null) {
      return null;
    }

    return entity.toWarehouse();
  }
}