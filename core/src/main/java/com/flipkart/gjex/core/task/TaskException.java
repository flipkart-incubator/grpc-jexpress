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

package com.flipkart.gjex.core.task;

import com.flipkart.gjex.core.GJEXError;

/**
 * The <code>TaskException</code> is sub-type of the {@link GJEXError} for use in the task module
 *
 * @author regunath.balasubramanian
 */
public class TaskException extends GJEXError {

	private static final long serialVersionUID = 1L;

	/** Flag to control logging to the distributed trace*/
	private boolean isTraced = true;

	/**
	 * Constructor for TaskException.
	 * @param msg the detail message
	 */
	public TaskException(String msg) {
		super(GJEXError.ErrorType.runtime, msg, null);
	}

	/**
	 * Constructor for TaskException.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public TaskException(String msg, Throwable cause) {
		super(GJEXError.ErrorType.runtime, msg, cause);
	}

	/**
	 * Constructor for TaskException
	 * @param cause the root cause
	 * @param isTraced flag to indicate if error should be logged to the distributed trace
	 */
	public TaskException(Throwable cause, boolean isTraced) {
		super(GJEXError.ErrorType.runtime, cause.getMessage(), cause);
		this.isTraced = isTraced;
	}

	public boolean isTraced() {
		return isTraced;
	}
	public void setTraced(boolean isTraced) {
		this.isTraced = isTraced;
	}


}
