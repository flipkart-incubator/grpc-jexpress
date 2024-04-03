package com.flipkart.gjex.core.web;

import com.flipkart.gjex.core.healthcheck.RotationManagementBasedHealthCheck;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource for rotation management
 * @author ajaypj, See <a href="https://www.linkedin.com/in/apj">https://www.linkedin.com/in/apj</a>
 */

@Singleton
@Path("/")
@Named
public class RotationManagementResource {

  @Inject
  public RotationManagementResource(RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck) {
    this.rotationManagementBasedHealthCheck = rotationManagementBasedHealthCheck;
  }

  private RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck;

  @POST
  @Path("/oor")
  @Produces(MediaType.APPLICATION_JSON)
  public Response oor() {
    String response = this.rotationManagementBasedHealthCheck.makeOor();
    return Response.status(Response.Status.OK).entity(response).build();
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/bir")
  public Response bir() {
    String response = this.rotationManagementBasedHealthCheck.makeBir();
    return Response.status(Response.Status.OK).entity(response).build();
  }

}
