package com.flipkart.gjex.core.filter.http;

import lombok.Builder;
import lombok.Data;

import javax.servlet.Filter;

/**
 * This should be used for registering {@link Filter} present in libraries
 * which cannot be edited like CrossOriginFilter
 *
 * @author ajay.jalgaonkar
 */

@Data
@Builder
@Deprecated
public class JavaxFilterParams {

    // The filter instance to be applied.
    private final Filter filter;

    // The URL pattern(s) the filter applies to.
    private final String pathSpec;
}
