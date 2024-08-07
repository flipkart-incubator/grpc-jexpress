package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.context.AccessLogContext;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.logging.Logging;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

/**
 * Implements an HTTP filter for logging access requests. This filter captures and logs
 * essential request and response details such as client IP, request URI, response status,
 * content length, and the time taken to process the request.
 * <p>
 * This class extends {@link HttpFilter} to provide specific logging functionality for HTTP requests.
 * It uses SLF4J for logging, allowing for integration with various logging frameworks.
 * </p>
 *
 * @author ajay.jalgaonkar
 */
public class AccessLogHttpFilter extends HttpFilter implements Logging {

    // Time when the request processing started.
    private long startTime;

    // Access log context.
    private AccessLogContext.AccessLogContextBuilder accessLogContextBuilder;

    // Logger instance for logging access log messages.
    private static final Logger logger = Logging.loggerWithName("ACCESS-LOG");

    // The format string for the access log message.
    private static String format;

    public AccessLogHttpFilter() {

    }

    public static void setFormat(String format) {
        AccessLogHttpFilter.format = format;
    }


    @Override
    public HttpFilter getInstance() {
        return new AccessLogHttpFilter();
    }

    /**
     * Processes the incoming request by initializing the start time and storing the request parameters.
     *
     * @param req                The incoming servlet request.
     * @param requestParamsInput Parameters of the request, including client IP and any additional metadata.
     */
    @Override
    public void doProcessRequest(ServletRequest req, RequestParams<Map<String,String>> requestParamsInput) {
        startTime = System.currentTimeMillis();
        accessLogContextBuilder = AccessLogContext.builder()
            .clientIp(requestParamsInput.getClientIp())
            .resourcePath(requestParamsInput.getResourcePath())
            .headers(requestParamsInput.getMetadata());
    }

    @Override
    public void doProcessResponseHeaders(Map<String,String> responseHeaders) {
        //
    }

    /**
     * Processes the outgoing response by logging relevant request and response details.
     * Logs the client IP, requested URI, response status, content length, and the time taken to process the request.
     *
     * @param response The outgoing servlet response.
     */
    @Override
    public void doProcessResponse(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (isSuccess(httpServletResponse.getStatus())) {
            // 2xx response
            int contentLength = Optional.ofNullable(httpServletResponse.getHeader(HttpHeaderNames.CONTENT_LENGTH.toString()))
                    .map(Integer::parseInt).orElse(0);
            accessLogContextBuilder.contentLength(contentLength);
        } else {
            // non-2xx response
            accessLogContextBuilder.contentLength(0);
        }
        accessLogContextBuilder
            .responseStatus(httpServletResponse.getStatus())
            .responseTime(System.currentTimeMillis() - startTime);
        logger.info(accessLogContextBuilder.build().format(format));
    }

    @Override
    public void doHandleException(Exception e) {
        // This shouldn't come here for http filters, that said, ensuring that even if happens we log it.
        accessLogContextBuilder
            .contentLength(0)
            .responseStatus(500)
            .responseTime(System.currentTimeMillis() - startTime);
        logger.info(accessLogContextBuilder.build().format(format));
    }

    private static boolean isSuccess(int code) {
        return ((200 <= code) && (code <= 299));
    }

}
