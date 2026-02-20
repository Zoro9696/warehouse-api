package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"warehouse_id", "product_id", "store_id"}
        )
)
public class FulfilmentAssignment {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(optional = false)
    public DbWarehouse warehouse;

    @ManyToOne(optional = false)
    public Product product;

    @ManyToOne(optional = false)
    public Store store;

    public LocalDateTime createdAt;
}
