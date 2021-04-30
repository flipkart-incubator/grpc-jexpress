package com.flipkart.gjex.grpc.service;

import com.flipkart.gjex.core.job.ScheduledJob;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.AbstractService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules bound {@link ScheduledJob} instances.
 */
@Singleton
@Named("ScheduleJobManager")
public class ScheduledJobManager extends AbstractService implements Logging {

    private final ScheduledExecutorService scheduler;
    private final List<ScheduledJob> jobs;

    @Inject
    public ScheduledJobManager(@Named("ScheduledJobs.executorThreads") int executorThreads) {
        this.scheduler = Executors.newScheduledThreadPool(executorThreads,
                new ThreadFactoryBuilder().setNameFormat("scheduled-jobs-%d").build());
        this.jobs = new ArrayList<>();
    }

    public void registerScheduledJobs(List<ScheduledJob> jobs) {
        if (!this.isStopped()) {
            throw new IllegalStateException("Jobs cannot be registered once the service is started");
        }
        this.jobs.addAll(jobs);
    }

    @Override
    protected void doStart() throws Exception {
        for (ScheduledJob job : jobs) {
            long interval = job.getInterval().toMillis();
            ScheduledJob.IntervalType intervalType = job.getIntervalType();
            if (intervalType == ScheduledJob.IntervalType.DELAY) {
                scheduler.scheduleWithFixedDelay(job, interval, interval, TimeUnit.MILLISECONDS);
            } else if (intervalType == ScheduledJob.IntervalType.RATE) {
                scheduler.scheduleAtFixedRate(job, interval, interval, TimeUnit.MILLISECONDS);
            }
            infoLog("Scheduled {} with {} at {}ms", job.getClass().getSimpleName(), intervalType, interval);
        }
    }

    @Override
    protected void doStop() {
        scheduler.shutdownNow();
    }

}
