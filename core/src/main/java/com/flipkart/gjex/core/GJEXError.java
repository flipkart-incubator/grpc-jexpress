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

package com.flipkart.gjex.core;

/**
 * <code>GJEXError</code> defines an error in the GJEX runtime.
 *
 * @author regunath.balasubramanian
 *
 */
public class GJEXError extends RuntimeException {

	/** Default serial version UID */
	private static final long serialVersionUID = 1L;

	/** Default Fill in stack trace setting*/
	private static final boolean DEFAULT_FILL_IN_STACK_TRACE = true;

    /** Enum of error types*/
    public enum ErrorType {
        runtime,timeout,retriable
    }

	/** The type of error*/
    private ErrorType type;

    /** The flag for filling in the stack trace*/
    private boolean fillInStackTrace = DEFAULT_FILL_IN_STACK_TRACE;

    /** Constructors */
	public GJEXError(ErrorType type, String errorMessage, Throwable rootCause) {
		super(errorMessage, rootCause);
		this.type = type;
	}
	public GJEXError(ErrorType type, String errorMessage, Throwable rootCause, boolean fillInStackTrace) {
		this(type, errorMessage, rootCause);
		this.fillInStackTrace = fillInStackTrace;
	}

	/**
	 * Fills in the stack trace based on how this Exception was created
	 * @see java.lang.Throwable#fillInStackTrace()
	 */
    public Throwable fillInStackTrace() {
	    	if (!fillInStackTrace) {
	    		return null;
	    	} else {
	    		return super.fillInStackTrace();
	    	}
    }

	/** Accessor/Mutator methods*/
	public ErrorType getType() {
		return type;
	}
	public void setType(ErrorType type) {
		this.type = type;
	}

}
