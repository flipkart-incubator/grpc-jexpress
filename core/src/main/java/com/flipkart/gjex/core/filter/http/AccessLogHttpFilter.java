package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.context.AccessLogContext;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.AccessLogGrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import org.slf4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

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

    // Parameters of the request being processed.
    private RequestParams<Set<String>> requestParams;

    // Logger instance for logging access log messages.
    private static final Logger logger = Logging.loggerWithName("ACCESS-LOG");

    // HTTP header name for content length.
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

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
    public void doProcessRequest(ServletRequest req, RequestParams<Set<String>> requestParamsInput) {
        startTime = System.currentTimeMillis();
        requestParams = requestParamsInput;
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
        AccessLogContext accessLogContext = AccessLogContext.builder()
            .clientIp(requestParams.getClientIp())
            .resourcePath(requestParams.getResourcePath())
            .responseStatus(httpServletResponse.getStatus())
            .contentLength(Integer.valueOf(httpServletResponse.getHeader(CONTENT_LENGTH_HEADER)))
            .responseTime(System.currentTimeMillis() - startTime)
            .build();
        logger.info(accessLogContext.format(format));
    }

    @Override
    public void doHandleException(Exception e) {
        //Todo
    }

}
