package com.flipkart.gjex.core.task;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class RotationManagementTask {
  private static final String BIR = "bir";
  private static final String OOR = "oor";

  private AtomicBoolean state;

  public RotationManagementTask() {
    state = new AtomicBoolean(true);
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
