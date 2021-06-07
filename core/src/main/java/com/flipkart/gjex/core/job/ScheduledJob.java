package com.flipkart.gjex.core.job;

import com.flipkart.gjex.core.logging.Logging;

import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Defines a job to be run recurrently on some schedule.
 * Bound instances of this class will be scheduled automatically.
 */
public abstract class ScheduledJob implements Runnable, Logging {

    public enum IntervalType {
        /**
         * Schedule using {@link java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate}
         */
        RATE,

        /**
         * Schedule using {@link java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay}
         */
        DELAY
    }

    /**
     * @return interval type for scheduling
     */
    @NotNull
    public abstract IntervalType getIntervalType();

    /**
     * @return interval for scheduling. Must be >= 1ms
     */
    @NotNull
    public abstract Duration getInterval();

    /**
     * Implement job logic here
     */
    public abstract void doJob();

    @Override
    public final void run() {
        try {
            doJob();
        } catch (Exception e) {
            errorLog("Unexpected error!", e);
        }
    }

}
