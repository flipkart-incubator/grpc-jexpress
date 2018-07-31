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
/** Configuration object of the  channel used by the grpc client **/
//This class can be extended to more configurable parameters of the channel like keep alive time etc.
public class ChannelConfig {
    private String hostname;
    private int port;
    private long deadlineInMs=Long.MAX_VALUE;

}
