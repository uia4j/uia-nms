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

public class AmqFailoverTest {

    @Test
    public void test() throws Exception {
        // NmsEndPoint endPoint = new NmsEndPoint("failover", null, "tcp://10.160.2.26:61616,tcp://10.160.2.27:61616", null);
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://10.160.2.30", "61616");

        final NmsConsumer sub = new AmqQueueFactory().createConsumer(endPoint);
        sub.addLabel("value");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("got it");
                System.out.println(body.getContent());
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(header.responseSubject);
                sub.createProducer().send(header.responseSubject, null, "good job", false);
            }
        });
        sub.start("HTKS.FAILOVER.S");
        Thread.sleep(1000);

        final NmsProducer pub = new AmqQueueFactory().createProducer(endPoint);
        pub.start();
        // String result = pub.send("HTKS.FAILOVER.S", "value", "xxxx", false, 3000, "HTKS.FAILOVER.R");
        // System.out.println("Get reply: " + result);
        pub.stop();
        sub.stop();
    }
}
