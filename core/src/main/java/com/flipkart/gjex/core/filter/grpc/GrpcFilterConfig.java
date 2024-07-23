package com.flipkart.gjex.core.filter.grpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A gRPC Filter Config for processing filters
 *
 * @author ajay.jalgaonkar
 */

@Data
public class GrpcFilterConfig {
  @JsonProperty("enableAccessLogs")
  private boolean enableAccessLogs = true;
}
