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
import uia.nms.NmsProducer;

public class AmqQueueFactroyTest {

    @Test
    public void testPubSub() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");

        // consumer
        NmsConsumer consumer = new AmqQueueFactory().createConsumer(endPoint);
        consumer.addLabel("data");
        consumer.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println(body.getContent().get("data"));
            }

        });
        consumer.start("NMS.AMQ.TEST");

        // producer
        NmsProducer producer = new AmqQueueFactory().createProducer(endPoint);
        producer.start();
        producer.send(
        		"NMS.AMQ.TEST",			// name
        		"data",					// label of the message
        		"Hello Judy", 			// message
        		false);					// persistent flag

        Thread.sleep(5000);
        producer.stop();
        consumer.stop();
    }
}
