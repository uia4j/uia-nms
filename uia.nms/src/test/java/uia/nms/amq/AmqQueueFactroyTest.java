/*******************************************************************************
 * Copyright 2018 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

    private int c = 0;

    @Test
    public void testPubSub1() throws Exception {
        // producer
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            NmsProducer producer = new AmqQueueFactory().createProducer(endPoint);
            producer.setTimeToLive(10000);
            producer.start();
            producer.send(
                    "NMS.AMQ.TEST",			// name
                    "data",					// label of the message
                    "Hello Judy", 			// message
                    false);					// persistent flag
            producer.stop();
        }

        // consumer
        final NmsConsumer consumer = new AmqQueueFactory().createConsumer(endPoint);
        final long t1 = System.currentTimeMillis();
        consumer.addLabel("data");
        consumer.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                AmqQueueFactroyTest.this.c++;
                if (AmqQueueFactroyTest.this.c > 999) {
                    long t2 = System.currentTimeMillis();
                    System.out.println(t2 - t1);
                    consumer.stop();
                    System.exit(0);
                }
                // System.out.println(body.getContent().get("data"));
            }

        });
        System.out.println("ready to consume...");

        consumer.start("NMS.AMQ.TEST");
        Thread.sleep(60000);
    }

    @Test
    public void testPubSub2() throws Exception {
        // producer
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            NmsProducer producer = new AmqQueueFactory().createProducer(endPoint);
            producer.setTimeToLive(10000);
            producer.start();
            producer.send(
                    "NMS.AMQ.TEST",			// name
                    "data",					// label of the message
                    "Hello Judy", 			// message
                    false);					// persistent flag
            producer.stop();
        }
    }

    @Test
    public void testPubSub3() throws Exception {
        // producer
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
        NmsProducer producer = new AmqQueueFactory().createProducer(endPoint);
        producer.start();
        producer.send(
                "NMS.AMQ.TEST",			// name
                "data",					// label of the message
                "Hello Judy", 			// message
                false,					// persistent flag
                5000);
        producer.stop();
    }
}
