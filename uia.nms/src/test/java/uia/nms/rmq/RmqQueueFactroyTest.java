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
package uia.nms.rmq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class RmqQueueFactroyTest {

    @Test
    public void testConn() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint("/", null, "10.170.110.64", "5672");
        endPoint.setUser("trek");
        endPoint.setPassword("trek12345");

        final NmsConsumer consumer = new RmqQueueFactory().createConsumer(endPoint);
        consumer.addLabel("data");
        consumer.start("HTJS.TREK");    // queue
        Thread.sleep(2000);
        consumer.stop();
    }

    @Test
    public void testPubSub1() throws Exception {
        //NmsEndPoint endPoint = new NmsEndPoint("/", null, "10.160.1.192", "5672");
        //endPoint.setUser("admin");
        //endPoint.setPassword("admin");

        NmsEndPoint endPoint = new NmsEndPoint("/", null, "10.160.1.126", "30056");
        endPoint.setUser("eap");
        endPoint.setPassword("eap");

        // consumer
        final NmsConsumer consumer = new RmqQueueFactory().createConsumer(endPoint);
        consumer.addLabel("data");
        consumer.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println(header.requestSubject + "=" + body.getContent().get("data"));
            }

        });

        consumer.start("HTKS.FDC.DC.WPRBY");    // queue

        // producer
        final NmsProducer producer = new RmqQueueFactory().createProducer(endPoint);
        producer.start("HTKS.FDC.DC"); // exchange name
        producer.send("HTKS.FDC.DC.WPRBY.WPRBY14.S", "data", "stdf", true);  // routing key
        producer.stop();

        Thread.sleep(3000);

        consumer.stop();

    }

    @Test
    public void testPubSub2() throws Exception {
        //NmsEndPoint endPoint = new NmsEndPoint("/", null, "10.160.1.192", "5672");
        //endPoint.setUser("admin");
        //endPoint.setPassword("admin");

        NmsEndPoint endPoint = new NmsEndPoint("/", null, "10.160.1.126", "30056");
        endPoint.setUser("eap");
        endPoint.setPassword("eap");

        // producer
        final NmsProducer producer = new RmqQueueFactory().createProducer(endPoint);
        producer.start("HTKS.FDC.DC"); // exchange name
        producer.send("HTKS.FDC.DC.WPRBY.WPRBY14.S", "data", "stdf", true);  // routing key
        producer.stop();

        Thread.sleep(5000);

        // consumer
        final NmsConsumer consumer = new RmqQueueFactory().createConsumer(endPoint);
        consumer.addLabel("data");
        consumer.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println(header.requestSubject + "=" + body.getContent().get("data"));
            }

        });
        consumer.start("TREK");    // queue
        Thread.sleep(5000);
        consumer.stop();

    }

    @Test
    public void testFDC() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint("/", null, "10.160.1.126", "30056");
        endPoint.setUser("eap");
        endPoint.setPassword("eap");

        Path fileName = Paths.get("D:/workspace/htks/pie/03.code/uia-pie/pie-dc/examples/bpvd08/case5.txt");
        // consumer
        final NmsConsumer consumer = new RmqQueueFactory().createConsumer(endPoint);
        consumer.addLabel("data");
        consumer.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                String data = body.getContent().get("data") + "\n";
                System.out.println(data);
                try {
                    Files.write(fileName, data.getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e) {
                }
            }

        });

        consumer.start("HTKS.FDC.DC.BPVD.BPVD08.S");
        Thread.sleep(5000);
        consumer.stop();

    }

}
