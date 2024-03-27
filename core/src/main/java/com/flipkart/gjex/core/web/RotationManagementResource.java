package com.flipkart.gjex.core.web;

import com.flipkart.gjex.core.task.RotationManagementBasedHealthCheck;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource for rotation management
 * @author ajay.jalgaonkar
 */

@Singleton
@Path("/")
@Named
public class RotationManagementResource {

  @Context
  private ServletContext servletContext;

  @Inject
  public RotationManagementResource(RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck) {
    this.rotationManagementBasedHealthCheck = rotationManagementBasedHealthCheck;
  }

  private RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck;

  @GET
  @Path("/oor")
  @Produces(MediaType.APPLICATION_JSON)
  public Response oor() {
    String response = this.rotationManagementBasedHealthCheck.makeOor();
    return Response.status(Response.Status.OK).entity(response).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/bir")
  public Response bir() {
    String response = this.rotationManagementBasedHealthCheck.makeBir();
    return Response.status(Response.Status.OK).entity(response).build();
  }

}
