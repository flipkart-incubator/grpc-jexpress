/*
 * Copyright (c) The original author or authors
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
