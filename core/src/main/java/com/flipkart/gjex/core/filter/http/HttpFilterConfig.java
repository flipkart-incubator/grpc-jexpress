package com.flipkart.gjex.core.filter.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * An HTTP Filter Config for processing filters
 *
 * @author ajay.jalgaonkar
 */

@Data
public class HttpFilterConfig {
  @JsonProperty("enableAccessLogs")
  private boolean enableAccessLogs = true;
}
