package com.flipkart.gjex.core.healthcheck;

import com.flipkart.gjex.core.logging.Logging;
import io.dropwizard.metrics5.health.HealthCheck;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rotation management based health check for the app
 * @author ajay.jalgaonkar
 */

@Singleton
public class RotationManagementBasedHealthCheck extends HealthCheck implements Logging {
  private static final String BIR = "bir";
  private static final String OOR = "oor";
  private static final AtomicBoolean rotationStatus = new AtomicBoolean(true);

  public RotationManagementBasedHealthCheck() {}

  @Override
  protected Result check() {
    if (isBir()) {
      return Result.healthy("Server is " + getStatus());
    } else {
      return Result.unhealthy("Server is " + getStatus());
    }
  }

  public String getStatus() {
    return isBir() ? BIR : OOR;
  }

  public String makeOor() {
    rotationStatus.set(false);
    return OOR;
  }

  public String makeBir() {
    rotationStatus.set(true);
    return BIR;
  }

  public boolean isBir() {
    return rotationStatus.get();
  }

}
