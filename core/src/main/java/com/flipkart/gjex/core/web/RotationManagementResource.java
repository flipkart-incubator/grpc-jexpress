package com.flipkart.gjex.core.web;

import com.flipkart.gjex.core.task.RotationManagementBasedHealthCheck;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource for rotation management
 * @author ajay.jalgaonkar
 */

@Singleton
@Path("/rotation_status")
@Named
public class RotationManagementResource {

  private RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck;

  @Inject
  public RotationManagementResource(RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck) {
    this.rotationManagementBasedHealthCheck = rotationManagementBasedHealthCheck;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/oor")
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

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/status")
  public Response status() {
    String response = this.rotationManagementBasedHealthCheck.getStatus();
    if (this.rotationManagementBasedHealthCheck.isBir()) {
      return Response.status(Response.Status.OK).entity(response).build();
    }
    return Response.status(Response.Status.NOT_FOUND).entity(response).build();
  }

}
