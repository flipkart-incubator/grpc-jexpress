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
package com.flipkart.gjex.grpc.channel;

/**
 * Created by rohit.k on 28/07/18.
 */

/** Configuration object of the  channel used by the grpc client **/
//This class can be extended to more configurable parameters of the channel like keep alive time etc.
public class ChannelConfig {
    private String hostname;
    private int port;
    private long deadlineInMs=Long.MAX_VALUE;

	public ChannelConfig(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getDeadlineInMs() {
		return deadlineInMs;
	}
	public void setDeadlineInMs(long deadlineInMs) {
		this.deadlineInMs = deadlineInMs;
	}
}
