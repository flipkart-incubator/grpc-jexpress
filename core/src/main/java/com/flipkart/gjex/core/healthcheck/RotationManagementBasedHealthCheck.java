package com.flipkart.gjex.core.healthcheck;

import com.flipkart.gjex.core.logging.Logging;
import com.codahale.metrics.health.HealthCheck;

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
    if (inRotation()) {
      return Result.healthy("Server is " + getStatus());
    } else {
      return Result.unhealthy("Server is " + getStatus());
    }
  }

  public static String getStatus() {
    return inRotation() ? BIR : OOR;
  }

  public static String makeOor() {
    rotationStatus.set(false);
    return OOR;
  }

  public static String makeBir() {
    rotationStatus.set(true);
    return BIR;
  }

  public static boolean inRotation() {
    return rotationStatus.get();
  }

}
