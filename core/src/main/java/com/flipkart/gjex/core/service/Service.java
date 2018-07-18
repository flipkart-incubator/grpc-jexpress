/*
 * Copyright 2018-2021, the original author or authors.
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

import java.util.EventListener;

/**
 * <code>Service</code> defines an interface for Service-like components that have explicit Start and Stop lifecycles.
 * These methods can be invoked suitably by the container. Examples of implementation include JDBC Datasource, Http Connection pool etc.
 * Some of the methods have been ported from Jetty Component source code.
 *
 * @author regunath.balasubramanian
 */

public interface Service {
	
	
	/**
	 * Starts the service. This method blocks until the service has completely started.
	*/
	void start() throws Exception;

	/**
	 * Stops the service. This method blocks until the service has completely shut down.
	 */
	void stop();
	
	/**
     * @return true if the Service is starting or has been started.
     */
	boolean isRunning();
	
	/**
     * @return true if the Service has been started.
     * @see #start()
     * @see #isStarting()
     */
    boolean isStarted();	
    
    /**
     * @return true if the Service is starting.
     * @see #isStarted()
     */
    boolean isStarting();    
    
    /**
     * @return true if the Service is stopping.
     * @see #isStopped()
     */
    boolean isStopping();  
    
    /**
     * @return true if the Service has been stopped.
     * @see #stop()
     * @see #isStopping()
     */
    boolean isStopped();   
    
    /**
     * @return true if the Service has failed to start or has failed to stop.
     */
    boolean isFailed();  

    /**
     * Methods to add/remove Service lifecycle listeners.
     */
    public void addServiceListener(Service.Listener listener);
    public void removeServiceListener(Service.Listener listener);

    /** 
     * A listener for Service events.
     */
    public interface Listener extends EventListener
    {
        public default void serviceStarting(Service service) {}
        public default void serviceStarted(Service service) {}
        public default void serviceFailure(Service service,Throwable cause) {}
        public default void serviceStopping(Service service) {}
        public default void serviceStopped(Service service) {}
    }    
}
