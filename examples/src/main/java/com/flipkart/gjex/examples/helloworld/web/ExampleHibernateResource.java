package com.flipkart.gjex.examples.helloworld.web;

import com.flipkart.gjex.examples.helloworld.dao.DummyDAO;
import com.flipkart.gjex.examples.helloworld.entity.DummyEntity;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Singleton
@Path("/hibernate/example")
@Named
public class ExampleHibernateResource {

    private final DummyDAO dummyDAO;

    @Inject
    public ExampleHibernateResource(DummyDAO dummyDAO) {
        this.dummyDAO = dummyDAO;
    }

    @POST
    public Response createPerson(DummyEntity person) {
        DummyEntity createdPerson = dummyDAO.create(person);
        return Response.status(Response.Status.CREATED).entity(createdPerson).build();
    }

    @GET
    @Path("/{id}")
    public Response getPersonById(@PathParam("id") Long id) {
        Optional<DummyEntity> person = Optional.ofNullable(dummyDAO.findById(id));
        if (person.isPresent()) {
            return Response.ok(person.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
