package com.flipkart.gjex.core.config;

import com.flipkart.gjex.core.web.RotationManagementResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * RotationManagementConfig for registering RotationManagementResource.
 * @author ajay.jalgaonkar
 *
 */
@Singleton
@Named("RotationManagementConfig")
public class RotationManagementConfig extends ResourceConfig  {
  @Inject
  public RotationManagementConfig (RotationManagementResource rotationManagementResource) {
		register(rotationManagementResource);
  }
}
