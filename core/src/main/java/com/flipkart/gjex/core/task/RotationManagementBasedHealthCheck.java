package com.flipkart.gjex.core.task;

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

  private AtomicBoolean state;

  public RotationManagementBasedHealthCheck() {
    state = new AtomicBoolean(true);
  }

  @Override
  protected Result check() {
    if (isBir()) {
      info("Returning healthy status.");
      return Result.healthy("Server is " + getStatus());
    } else {
      info("Returning unhealthy status.");
      return Result.unhealthy("Server is " + getStatus());
    }
  }

  public String getStatus() {
    return isBir() ? BIR : OOR;
  }

  public String makeOor() {
    state.set(false);
    return OOR;
  }

  public String makeBir() {
    state.set(true);
    return BIR;
  }

  public boolean isBir() {
    return state.get();
  }

}
