package com.flipkart.gjex.grpc.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Created by rohit.k on 28/07/18.
 */

@Data
@RequiredArgsConstructor
/** Configuration object of the  channel used by the grpc client **/
//This class can be extended to more configurable parameters of the channel like keep alive time etc.
public class ChannelConfig {
    private final String hostname;
    private final int port;
    private long deadlineInMs=Long.MAX_VALUE;

}
