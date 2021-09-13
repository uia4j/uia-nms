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
import uia.nms.NmsMatching;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class AmqQueueProducerTest implements NmsMatching {

	private NmsEndPoint endPoint;
	
	private int index;
	
	public AmqQueueProducerTest() {
        this.endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
	}

    @Test
    public void testSend() throws Exception {
        final NmsProducer pub = new AmqQueueFactory().createProducer(this.endPoint);
        pub.start();
        pub.send("NMS.AMQ.TEST", "data", "Judy", true);
        pub.stop();
    }

    @Test
    public void testReply1() throws Exception {
        final NmsConsumer sub = new AmqQueueFactory().createConsumer(this.endPoint);
        sub.addLabel("data");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
            	String name = body.getContent().get("data");
                System.out.println("message=" + name + ", response=" + header.responseSubject + ", cid="  + header.correlationID);
                String reply = "Hello " + name;

                NmsProducer subpub = sub.createProducer();
                subpub.send(header.responseSubject, "data", reply, false, header.correlationID);
            }
        });
        sub.start("NMS.AMQ.TEST");

        final NmsProducer pub = new AmqQueueFactory().createProducer(this.endPoint);
        pub.start();
        String result = pub.send(
        		"NMS.AMQ.TEST", 
        		"data", 
        		"Judy", 
        		false, 
        		3000);
        System.out.println("Get reply: " + result);

        Thread.sleep(1000);
        pub.stop();
        sub.stop();
    }

    @Test
    public void testReply2() throws Exception {
        final NmsConsumer sub = new AmqQueueFactory().createConsumer(this.endPoint);
        sub.addLabel("data");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
            	
            	String name = body.getContent().get("data");
                System.out.println("message=" + name + ", response=" + header.responseSubject + ", cid="  + header.correlationID);
                String reply = "Hello " + name;
                
                NmsProducer subpub = sub.createProducer();
                subpub.send(header.responseSubject, "data", reply, false, header.correlationID);
                subpub.send(header.responseSubject, "data", reply + " how are you", false, header.correlationID);
            }
        });

        sub.start("NMS.AMQ.TEST");

        this.index = 0;
        final NmsProducer pub = new AmqQueueFactory().createProducer(this.endPoint);
        pub.start();
        String result = pub.send(
        		"NMS.AMQ.TEST", 
        		"data", 
        		"Judy", 
        		false, 
        		3000, 
        		"NMS.AMQ.TEST.REPLY",		// set a specific reply name.
        		this);						// match helper
        System.out.println("Reply: " + result);

        Thread.sleep(1000);
        pub.stop();
        sub.stop();
    }

    @Override
    public boolean check(String message) {
    	boolean result = this.index++ > 0;
    	System.out.println("match> " + message + ", " + result);
    	return result;
    }
}
