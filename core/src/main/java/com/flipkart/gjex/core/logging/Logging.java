/*
 * Copyright 2012-2016, the original author or authors.
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

package com.flipkart.gjex.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import com.flipkart.gjex.Constants;

/**
 * Convenience logging implementation with default behavior for use by classes in GJEX runtime and applications.
 * 
 * @author regu.b
 *
 */
public interface Logging {

    default String msgWithLogIdent(String msg) {
        String logId = MDC.get(Constants.LOGGING_ID);
        if(logId == null || logId.isEmpty())
            return msg;
        else return logId + msg;
    }

    default String getLoggerName() { return this.getClass().getCanonicalName();}

    default Logger logger() {
        return LoggerFactory.getLogger(getLoggerName());
    }

    default void trace(String msg)  {
        if (logger().isTraceEnabled()) logger().trace(msgWithLogIdent(msg));
    }

    default void trace(Throwable e) {
        if (logger().isTraceEnabled()) logger().trace(MDC.get(Constants.LOGGING_ID),e);
    }

    default void trace(String msg,Throwable e) {
        if (logger().isTraceEnabled()) logger().trace(msgWithLogIdent(msg),e);
    }

    default void logTrace(String msg,Throwable e, Object... args) {
        if (logger().isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msgWithLogIdent(msg), args);
            logger().trace(ft.getMessage(),e);
        }
    }

    default void traceLog(String msg, Object... args){
        logger().trace(msgWithLogIdent(msg), args);
    }

    default void debug(String msg)  {
        if (logger().isDebugEnabled()) logger().debug(msgWithLogIdent(msg));
    }

    default void debug(Throwable e) {
        if (logger().isDebugEnabled()) logger().debug(MDC.get(Constants.LOGGING_ID),e);
    }

    default void debug(String msg,Throwable e) {
        if (logger().isDebugEnabled()) logger().debug(msgWithLogIdent(msg),e);
    }

    default void logDebug(String msg,Throwable e, Object... args) {
        if (logger().isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msgWithLogIdent(msg), args);
            logger().debug(ft.getMessage(),e);
        }
    }

    default void debugLog(String msg, Object... args){
        logger().debug(msgWithLogIdent(msg), args);
    }

    default void info(String msg)  {
        if (logger().isInfoEnabled()) logger().info(msgWithLogIdent(msg));
    }

    default void info(Throwable e) {
        if (logger().isInfoEnabled()) logger().info(MDC.get(Constants.LOGGING_ID),e);
    }

    default void info(String msg,Throwable e) {
        if (logger().isInfoEnabled()) logger().info(msgWithLogIdent(msg),e);
    }

    default void logInfo(String msg,Throwable e, Object... args) {
        if (logger().isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msgWithLogIdent(msg), args);
            logger().info(ft.getMessage(),e);
        }
    }

    default void infoLog(String msg, Object... args){
        logger().info(msgWithLogIdent(msg), args);
    }

    default void warn(String msg)  {
        if (logger().isWarnEnabled()) logger().warn(msgWithLogIdent(msg));
    }

    default void warn(Throwable e) {
        if (logger().isWarnEnabled()) logger().warn(MDC.get(Constants.LOGGING_ID),e);
    }

    default void warn(String msg,Throwable e) {
        if (logger().isWarnEnabled()) logger().warn(msgWithLogIdent(msg),e);
    }

    default void logWarn(String msg,Throwable e, Object... args) {
        if (logger().isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msgWithLogIdent(msg), args);
            logger().warn(ft.getMessage(),e);
        }
    }

    default void warnLog(String msg, Object... args){
        logger().warn(msgWithLogIdent(msg), args);
    }

    default void error(String msg)  {
        if (logger().isErrorEnabled()) logger().error(msgWithLogIdent(msg));
    }

    default void error(Throwable e) {
        if (logger().isErrorEnabled()) logger().error(MDC.get(Constants.LOGGING_ID),e);
    }

    default void error(String msg,Throwable e) {
        if (logger().isErrorEnabled()) logger().error(msgWithLogIdent(msg),e);
    }

    default void logError(String msg,Throwable e, Object... args) {
        if (logger().isErrorEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msgWithLogIdent(msg), args);
            logger().error(ft.getMessage(),e);
        }
    }

    default void errorLog(String msg, Object... args){
        logger().error(msgWithLogIdent(msg), args);
    }

}
