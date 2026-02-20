package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.fulfilment.FulfilmentAssignment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.FulfilmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssignWarehouseUseCaseTest {

    private AssignWarehouseUseCase useCase;
    private FakeFulfilmentRepository repository;
    private Store store;
    private Product product;
    private DbWarehouse warehouse;

    @BeforeEach
    void setup() {
        repository = new FakeFulfilmentRepository();
        useCase = new AssignWarehouseUseCase();
        useCase.repository = repository;

        store = new Store();
        store.id = 1L;

        product = new Product();
        product.id = 1L;

        warehouse = new DbWarehouse();
        warehouse.id = 1L;
    }

    @Test
    void assign_successfulAssignment() {
        useCase.assign(warehouse, product, store);

        assertEquals(1, repository.getAssignments().size());

        FulfilmentAssignment assignment = repository.getAssignments().get(0);
        assertEquals(store, assignment.store);
        assertEquals(product, assignment.product);
        assertEquals(warehouse, assignment.warehouse);
        assertNotNull(assignment.createdAt);
    }

    @Test
    void assign_throwsIfProductAlreadyInTwoWarehouses() {
        DbWarehouse w2 = new DbWarehouse();
        w2.id = 2L;
        DbWarehouse w3 = new DbWarehouse();
        w3.id = 3L;

        // Directly set fields
        FulfilmentAssignment a1 = new FulfilmentAssignment();
        a1.store = store; a1.product = product; a1.warehouse = warehouse; a1.createdAt = LocalDateTime.now();
        FulfilmentAssignment a2 = new FulfilmentAssignment();
        a2.store = store; a2.product = product; a2.warehouse = w2; a2.createdAt = LocalDateTime.now();

        repository.persist(a1);
        repository.persist(a2);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> useCase.assign(w3, product, store));
        assertEquals("Product can be fulfilled by max 2 warehouses per store", ex.getMessage());
    }

    @Test
    void assign_throwsIfStoreAlreadyHasThreeWarehouses() {
        DbWarehouse w2 = new DbWarehouse();
        w2.id = 2L;
        DbWarehouse w3 = new DbWarehouse();
        w3.id = 3L;

        Product p2 = new Product();
        p2.id = 2L;
        Product p3 = new Product();
        p3.id = 3L;

        FulfilmentAssignment a1 = new FulfilmentAssignment();
        a1.store = store; a1.product = product; a1.warehouse = warehouse; a1.createdAt = LocalDateTime.now();
        FulfilmentAssignment a2 = new FulfilmentAssignment();
        a2.store = store; a2.product = p2; a2.warehouse = w2; a2.createdAt = LocalDateTime.now();
        FulfilmentAssignment a3 = new FulfilmentAssignment();
        a3.store = store; a3.product = p3; a3.warehouse = w3; a3.createdAt = LocalDateTime.now();

        repository.persist(a1);
        repository.persist(a2);
        repository.persist(a3);

        DbWarehouse w4 = new DbWarehouse();
        w4.id = 4L;
        Product p4 = new Product();
        p4.id = 4L;

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> useCase.assign(w4, p4, store));
        assertEquals("Store can be fulfilled by max 3 warehouses", ex.getMessage());
    }

    @Test
    void assign_throwsIfWarehouseAlreadyHasFiveProducts() {
        for (int i = 1; i <= 5; i++) {
            Product p = new Product();
            p.id = (long) i;
            FulfilmentAssignment a = new FulfilmentAssignment();
            a.store = store; a.product = p; a.warehouse = warehouse; a.createdAt = LocalDateTime.now();
            repository.persist(a);
        }

        Product newProduct = new Product();
        newProduct.id = 6L;

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> useCase.assign(warehouse, newProduct, store));
        assertEquals("Warehouse can store max 5 product types", ex.getMessage());
    }

    private static class FakeFulfilmentRepository extends FulfilmentRepository {
        private final List<FulfilmentAssignment> assignments = new ArrayList<>();

        @Override
        public long countWarehousesByStoreAndProduct(Store store, Product product) {
            return assignments.stream()
                    .filter(a -> a.store.equals(store) && a.product.equals(product))
                    .map(a -> a.warehouse)
                    .distinct()
                    .count();
        }

        @Override
        public long countWarehousesByStore(Store store) {
            return assignments.stream()
                    .filter(a -> a.store.equals(store))
                    .map(a -> a.warehouse)
                    .distinct()
                    .count();
        }

        @Override
        public long countProductsByWarehouse(DbWarehouse warehouse) {
            return assignments.stream()
                    .filter(a -> a.warehouse.equals(warehouse))
                    .map(a -> a.product)
                    .distinct()
                    .count();
        }

        @Override
        public void persist(FulfilmentAssignment assignment) {
            assignments.add(assignment);
        }

        public List<FulfilmentAssignment> getAssignments() {
            return assignments;
        }
    }
}
