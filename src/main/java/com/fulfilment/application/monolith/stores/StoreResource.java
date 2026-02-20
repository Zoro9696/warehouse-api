package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreChangedEvent;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

    @Inject
    jakarta.enterprise.event.Event<StoreChangedEvent> storeChangedEvent;

    private static final Logger LOGGER = Logger.getLogger(StoreResource.class.getName());

    // ---------------- GET ----------------

    @GET
    public List<Store> get() {
        return Store.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Store getSingle(@PathParam("id") Long id) {
        Store entity = Store.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    // ---------------- CREATE ----------------

    @POST
    @Transactional
    public Response create(Store store) {
        if (store.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        store.persist();

        storeChangedEvent.fire(
                new StoreChangedEvent(store, StoreChangedEvent.Operation.CREATE)
        );

        return Response.status(201).entity(store).build();
    }

    // ---------------- UPDATE ----------------

    @PUT
    @Path("{id}")
    @Transactional
    public Store update(@PathParam("id") Long id, Store updatedStore) {

        if (updatedStore.name == null) {
            throw new WebApplicationException("Store Name was not set on request.", 422);
        }

        Store entity = Store.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }

        entity.name = updatedStore.name;
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

        storeChangedEvent.fire(
                new StoreChangedEvent(entity, StoreChangedEvent.Operation.UPDATE)
        );

        return entity;
    }

    @PATCH
    @Path("{id}")
    @Transactional
    public Store patch(@PathParam("id") Long id, Store updatedStore) {

        Store entity = Store.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }

        if (updatedStore.name != null) {
            entity.name = updatedStore.name;
        }

        if (updatedStore.quantityProductsInStock != 0) {
            entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
        }

        storeChangedEvent.fire(
                new StoreChangedEvent(entity, StoreChangedEvent.Operation.UPDATE)
        );

        return entity;
    }

    // ---------------- DELETE ----------------

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {

        Store entity = Store.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }

        entity.delete();
        return Response.status(204).build();
    }
}