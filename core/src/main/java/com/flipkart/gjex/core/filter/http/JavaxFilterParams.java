package com.flipkart.gjex.core.filter.http;

import lombok.Builder;
import lombok.Data;

import javax.servlet.Filter;

/**
 *
 * @author ajay.jalgaonkar
 */

@Data
@Builder
public class JavaxFilterParams {

    // The filter instance to be applied.
    private final Filter filter;

    // The URL pattern(s) the filter applies to.
    private final String pathSpec;
}
