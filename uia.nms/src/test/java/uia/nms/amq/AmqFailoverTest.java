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

public class AmqFailoverTest {

    @Test
    public void test1() throws Exception {
        NmsEndPoint endPoint1 = new NmsEndPoint("failover", null, "tcp://localhost:61616", null);
        NmsEndPoint endPoint2 = new NmsEndPoint("failover", null, "tcp://localhost:61626", null);
        NmsEndPoint endPoint = new NmsEndPoint("failover", null, "tcp://localhost:61616,tcp://localhost:61626", null);

        final NmsConsumer sub = new AmqQueueFactory().createConsumer(endPoint);
        sub.addLabel("value");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println(" got: " + body.getContent());
            }
        });
        sub.start("NMS.HA");

        System.out.println("---");
        final NmsProducer pub = new AmqQueueFactory().createProducer(endPoint);
        pub.start();
        for (int x = 1; x < 1000; x++) {
            try {
                System.out.println(x);
                pub.send("NMS.HA", "value", "xxxx" + x, false);
                Thread.sleep(500);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Thread.sleep(1000);
        pub.stop();
        Thread.sleep(1000);
        sub.stop();
    }

    @Test
    public void test01ToOthers() throws Exception {
        final NmsEndPoint endPoint1 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61616");

        final NmsEndPoint endPoint2 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61626");
        final NmsEndPoint endPoint3 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61636");
        final NmsEndPoint endPoint4 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61646");
        final NmsEndPoint endPoint5 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61656");

        final NmsConsumer sub2 = new AmqQueueFactory().createConsumer(endPoint2);
        sub2.addLabel("value");
        sub2.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("2> got: " + body.getContent());
            }
        });

        final NmsConsumer sub3 = new AmqQueueFactory().createConsumer(endPoint3);
        sub3.addLabel("value");
        sub3.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("3> got: " + body.getContent());
            }
        });
        final NmsConsumer sub4 = new AmqQueueFactory().createConsumer(endPoint4);
        sub4.addLabel("value");
        sub4.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("4> got: " + body.getContent());
            }
        });
        final NmsConsumer sub5 = new AmqQueueFactory().createConsumer(endPoint5);
        sub5.addLabel("value");
        sub5.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("5> got: " + body.getContent());
            }
        });

        //sub2.start("NMS.HA2");
        sub3.start("NMS.HA3");
        sub4.start("NMS.HA4");
        //sub5.start("NMS.HA5");

        final NmsProducer pub = new AmqQueueFactory().createProducer(endPoint1);
        pub.start();
        pub.send("NMS.HA2", "value", "message to HA2-1", false);
        pub.send("NMS.HA4", "value", "message to HA4-1", false);
        pub.send("NMS.HA2", "value", "message to HA2-2", false);
        pub.send("NMS.HA4", "value", "message to HA4-2", false);
        pub.send("NMS.HA3", "value", "message to HA3-1", false);
        pub.send("NMS.HA5", "value", "message to HA5-1", false);
        Thread.sleep(1000);
        pub.stop();

        sub2.stop();
        //sub3.stop();
        sub4.stop();
        sub5.stop();
    }

    @Test
    public void testOthersTo01() throws Exception {
        final NmsEndPoint endPoint1 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61616");
        final NmsEndPoint endPoint2 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61626");
        final NmsEndPoint endPoint3 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61636");
        final NmsEndPoint endPoint4 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61646");
        final NmsEndPoint endPoint5 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61656");

        final NmsConsumer sub1 = new AmqQueueFactory().createConsumer(endPoint1);
        sub1.addLabel("value");
        sub1.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("1> got: " + body.getContent());
            }
        });

        sub1.start("NMS.HA.*");

        //final NmsProducer pub2 = new AmqQueueFactory().createProducer(endPoint2);
        //pub2.start();

        final NmsProducer pub3 = new AmqQueueFactory().createProducer(endPoint3);
        pub3.start();

        final NmsProducer pub4 = new AmqQueueFactory().createProducer(endPoint4);
        pub4.start();

        //final NmsProducer pub5 = new AmqQueueFactory().createProducer(endPoint5);
        //pub5.start();

        //pub2.send("NMS.HA.2", "value", "2> message1", false);
        //pub5.send("NMS.HA.5", "value", "5> message1", false);
        pub4.send("NMS.HA.4", "value", "4> message1", false);
        pub4.send("NMS.HA.4", "value", "4> message2", false);
        pub3.send("NMS.HA.3", "value", "3> message1", false);
        //pub2.send("NMS.HA.2", "value", "2> message2", false);
        pub4.send("NMS.HA.4", "value", "4> message3", false);

        Thread.sleep(1000);

        sub1.stop();
        //pub2.stop();
        pub3.stop();
        pub4.stop();
        //pub5.stop();

    }

    @Test
    public void test29() throws Exception {
        final NmsEndPoint endPoint1 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61616");
        final NmsEndPoint endPoint2 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61626");
        final NmsEndPoint endPoint3 = new NmsEndPoint(null, null, "tcp://10.160.82.8", "61636");
        final NmsEndPoint endPoint29 = new NmsEndPoint(null, null, "tcp://10.160.2.29", "61616");

        final NmsConsumer sub1 = new AmqQueueFactory().createConsumer(endPoint1);
        sub1.addLabel("value");
        sub1.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("1> got: " + body.getContent());
            }
        });

        final NmsConsumer sub2 = new AmqQueueFactory().createConsumer(endPoint2);
        sub2.addLabel("value");
        sub2.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("2> got: " + body.getContent());
            }
        });

        final NmsConsumer sub3 = new AmqQueueFactory().createConsumer(endPoint3);
        sub3.addLabel("value");
        sub3.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("3> got: " + body.getContent());
            }
        });

        final NmsConsumer sub29 = new AmqQueueFactory().createConsumer(endPoint29);
        sub29.addLabel("value");
        sub29.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("29> got: " + body.getContent());
            }
        });

        sub1.start("NMS.HA1");
        sub2.start("NMS.HA2");
        sub3.start("NMS.HA3");
        sub29.start("NMS.HA29");

        final NmsProducer pub2 = new AmqQueueFactory().createProducer(endPoint2);
        pub2.start();

        final NmsProducer pub3 = new AmqQueueFactory().createProducer(endPoint3);
        pub3.start();

        final NmsProducer pub29 = new AmqQueueFactory().createProducer(endPoint29);
        pub29.start();

        //pub5.start();

        pub2.send("NMS.HA29", "value", "2 to 29", false);
        pub2.send("NMS.HA3", "value", "2 to 3", false);
        pub3.send("NMS.HA29", "value", "3 to 29", false);
        pub29.send("NMS.HA1", "value", "29 to 1", false);
        pub29.send("NMS.HA2", "value", "29 to 2", false);
        pub29.send("NMS.HA3", "value", "29 to 3", false);

        Thread.sleep(1000);

        sub2.stop();
        pub29.stop();

    }
}
