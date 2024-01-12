/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.core.service;

import java.util.concurrent.CopyOnWriteArrayList;

import com.flipkart.gjex.core.logging.Logging;

/**
 * Common basic implementation of Service. Code ported from Jetty Component
 * @author regu.b
 *
 */
public abstract class AbstractService implements Service, Logging {

	public static final String STOPPED = "STOPPED";
	public static final String FAILED = "FAILED";
	public static final String STARTING = "STARTING";
	public static final String STARTED = "STARTED";
	public static final String STOPPING = "STOPPING";
	public static final String RUNNING = "RUNNING";

	private static final int FAILED_STATE = -1, STOPPED_STATE = 0, STARTING_STATE = 1, STARTED_STATE = 2, STOPPING_STATE = 3;

	private final CopyOnWriteArrayList<Service.Listener> listeners = new CopyOnWriteArrayList<Service.Listener>();
	private final Object lock = new Object();
	private volatile int state = STOPPED_STATE;
	private long stopTimeout = 30000;

	protected void doStart() throws Exception {
	}

	protected void doStop() {
	}

	@Override
	public final void start() throws Exception {
		synchronized (lock) {
			try {
				if (state == STARTED_STATE || state == STARTING_STATE)
					return;
				setStarting();
				doStart();
				setStarted();
				AbstractService self = this;
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                    System.err.println("*** shutting down since JVM is shutting down");
                    self.stop();
                    System.err.println("*** server shut down");
                }));
			} catch (Throwable e) {
				setFailed(e);
				throw e;
			}
		}
	}

	@Override
	public final void stop() {
		synchronized (lock) {
			try {
				if (state == STOPPING_STATE || state == STOPPED_STATE)
					return;
				setStopping();
				doStop();
				setStopped();
			} catch (Throwable e) {
				setFailed(e);
			}
		}
	}

	@Override
	public boolean isRunning() {
		final int tempState = state;

		return tempState == STARTED_STATE || state == STARTING_STATE;
	}

	@Override
	public boolean isStarted() {
		return state == STARTED_STATE;
	}

	@Override
	public boolean isStarting() {
		return state == STARTING_STATE;
	}

	@Override
	public boolean isStopping() {
		return state == STOPPING_STATE;
	}

	@Override
	public boolean isStopped() {
		return state == STOPPED_STATE;
	}

	@Override
	public boolean isFailed() {
		return state == FAILED_STATE;
	}

	@Override
	public void addServiceListener(Service.Listener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeServiceListener(Service.Listener listener) {
		listeners.remove(listener);
	}

	public String getState() {
		switch (state) {
		case FAILED_STATE:
			return FAILED;
		case STARTING_STATE:
			return STARTING;
		case STARTED_STATE:
			return STARTED;
		case STOPPING_STATE:
			return STOPPING;
		case STOPPED_STATE:
			return STOPPED;
		}
		return null;
	}

	public static String getState(Service service) {
		if (service.isStarting())
			return STARTING;
		if (service.isStarted())
			return STARTED;
		if (service.isStopping())
			return STOPPING;
		if (service.isStopped())
			return STOPPED;
		return FAILED;
	}

	private void setStarted() {
		state = STARTED_STATE;
		logDebug(STARTED + " {}", null, this);
		listeners.forEach(listener -> listener.serviceStarted(this));
	}

	private void setStarting() {
		logDebug("starting {}", null, this);
		state = STARTING_STATE;
		listeners.forEach(listener -> listener.serviceStarting(this));
	}

	private void setStopping() {
		logDebug("stopping {}", null, this);
		state = STOPPING_STATE;
		listeners.forEach(listener -> listener.serviceStopping(this));
	}

	private void setStopped() {
		state = STOPPED_STATE;
		logDebug("{} {}", null, STOPPED, this);
		listeners.forEach(listener -> listener.serviceStopped(this));
	}

	private void setFailed(Throwable th) {
		state = FAILED_STATE;
		warnLog(FAILED + " " + this + ": " + th, th);
		listeners.forEach(listener -> listener.serviceFailure(this, th));
	}

	public long getStopTimeout() {
		return stopTimeout;
	}

	public void setStopTimeout(long stopTimeout) {
		this.stopTimeout = stopTimeout;
	}

}