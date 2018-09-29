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

public class AmqTopicFactroyTest {

    @Test
    public void testPubSub() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");

        final NmsProducer pub = new AmqTopicFactory().createProducer(endPoint);
        NmsConsumer sub = new AmqTopicFactory().createConsumer(endPoint);
        sub.addLabel("xml");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println(body.getContent().get("xml"));
            }
        });

        sub.start("a.b.c");

        pub.start();
        pub.send("a.b.c", "xml", "hello judy", false);
        Thread.sleep(5000);

        pub.stop();
        sub.stop();
    }
}
