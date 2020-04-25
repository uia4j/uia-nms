/*******************************************************************************
 * Copyright 2018 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;
import uia.nms.NmsTransportListener;

public class AmqTopicConsumerTest extends AbstractTest implements NmsTransportListener, NmsMessageListener {

    @Test
    public void testListener() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://10.160.1.51", "61616");

        NmsConsumer sub = new AmqTopicFactory().createConsumer(endPoint);
        sub.setTransportListener(this);
        sub.addMessageListener(this);
        sub.start("NMS.AMQ.TEST");

        pressToContinue();
        sub.stop();
    }

    @Override
    public void broken(NmsConsumer c) {
        System.out.println("broken");
    }

    @Override
    public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
        System.out.println("Receive: " + body.getContent());
    }
}
