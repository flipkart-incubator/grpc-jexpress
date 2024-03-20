package com.flipkart.gjex.examples.helloworld.web;

import com.flipkart.gjex.examples.helloworld.healthcheck.AllIsWellHealthCheck;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/rotation_status")
@Named
public class RotationManagementResource {

  private AllIsWellHealthCheck allIsWellHealthCheck;

  @Inject
  public RotationManagementResource(AllIsWellHealthCheck allIsWellHealthCheck) {
    this.allIsWellHealthCheck = allIsWellHealthCheck;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/oor")
  public Response oor() {
    String response = this.allIsWellHealthCheck.makeOor();
    return Response.status(Response.Status.OK).entity(response).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/bir")
  public Response bir() {
    String response = this.allIsWellHealthCheck.makeBir();
    return Response.status(Response.Status.OK).entity(response).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/status")
  public Response status() {
    String response = this.allIsWellHealthCheck.getStatus();
    if (this.allIsWellHealthCheck.isBir()) {
      return Response.status(Response.Status.OK).entity(response).build();
    }
    return Response.status(Response.Status.NOT_FOUND).entity(response).build();
  }

}
