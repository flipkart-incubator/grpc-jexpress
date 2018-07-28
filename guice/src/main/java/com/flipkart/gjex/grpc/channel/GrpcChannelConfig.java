package com.flipkart.gjex.grpc.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by rohit.k on 28/07/18.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrpcChannelConfig {
    private String hostname;
    private int port;
}
