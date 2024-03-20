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
@Path("/app-healthcheck")
@Named
public class HelloWorldHealthCheckResource {
  private AllIsWellHealthCheck allIsWellHealthCheck;

  @Inject
  public HelloWorldHealthCheckResource(AllIsWellHealthCheck allIsWellHealthCheck) {
    this.allIsWellHealthCheck = allIsWellHealthCheck;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/")
  public Response health() {
    if (allIsWellHealthCheck.isBir()) {
       return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
  }
}
