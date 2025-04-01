package com.flipkart.gjex.examples.helloworld.web;

import com.flipkart.gjex.examples.helloworld.dao.DummyDAO;
import com.flipkart.gjex.examples.helloworld.entity.DummyEntity;
import io.dropwizard.hibernate.UnitOfWork;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Singleton
@Path("/hibernate/example")
@Named
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleHibernateResource {

    private final DummyDAO dummyDAO;

    @Inject
    public ExampleHibernateResource(DummyDAO dummyDAO) {
        this.dummyDAO = dummyDAO;
    }

    @POST
    @UnitOfWork
    public Response createPerson(DummyEntity dummyEntity) {
        try {
            DummyEntity createdPerson = dummyDAO.create(dummyEntity);
            return Response.status(Response.Status.CREATED)
                    .entity(createdPerson)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating Dummy Entity: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response getPersonById(@PathParam("id") Long id) {
        try {
            Optional<DummyEntity> person = Optional.ofNullable(dummyDAO.findById(id));
            if (person.isPresent()) {
                return Response.ok(person.get())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Dummy Entity not found with id: " + id)
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving Dummy Entity: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
