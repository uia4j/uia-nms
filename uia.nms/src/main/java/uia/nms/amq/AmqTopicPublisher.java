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

import java.util.Calendar;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.nms.NmsException;
import uia.nms.NmsMatching;
import uia.nms.NmsProducer;

/**
 * ActiveMQ TOPIC publisher implementation
 * 
 * @author Kan
 *
 */
public class AmqTopicPublisher implements NmsProducer {

    private static final Logger logger = LoggerFactory.getLogger(AmqTopicPublisher.class);

    private ActiveMQConnection connection;

    private Session session;
    
    private boolean timeSync;
    
    private int timeToLive;

    AmqTopicPublisher(ActiveMQConnectionFactory factory) throws JMSException {
        this.connection = (ActiveMQConnection) factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.timeSync = true;	// 對 timeToLive 有影響
        this.timeToLive = 5000;
    }

	@Override
	public int getTimeToLive() {
		return timeToLive; 
	}

	@Override
	public void setTimeToLive(int timeToLive) {
		this.timeToLive = Math.max(1000, timeToLive);
	}

	@Override
    public boolean isTimeSync() {
		return timeSync;
	}

	@Override
	public void setTimeSync(boolean timeSync) {
		this.timeSync = timeSync;
	}

    @Override
    public void start() throws NmsException {
        try {
            this.connection.start();
        }
        catch (Exception ex) {
            throw new NmsException("start AMQ(TopicProducer) failed", ex);
        }
    }

    @Override
    public void stop() {
        try {
            this.connection.stop();
            this.connection.close();
        }
        catch (Exception ex) {
        }
    }

    @Override
    public boolean send(String topicName, String label, String content, boolean persistent) {
        return send(topicName, label, content, persistent, Long.toString(Calendar.getInstance().getTime().getTime()));
    }

    @Override
    public boolean send(String topicName, String label, String content, boolean persistent, String correlationID) {
        try {
            Destination reqDest = this.session.createTopic(topicName);
            MessageProducer producer = this.session.createProducer(reqDest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(this.timeToLive);
            if(!this.timeSync) {
            	producer.setDisableMessageTimestamp(true);
            }

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSTimestamp(System.currentTimeMillis());
            requestMessage.setJMSCorrelationID(correlationID);
            requestMessage.setStringProperty("label", label);
            producer.send(requestMessage);
            logger.debug(String.format("amq, topic, %-20s, %s, %-20s, %s",
                    topicName,
                    requestMessage.getJMSCorrelationID(),
                    "null",
                    content));
            producer.close();

            return true;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public String send(String topicName, String label, String content, boolean persistent, long timeout) {
        try {
            Destination reqDest = this.session.createTopic(topicName);
            Destination respDest = this.session.createTemporaryTopic();

            // Create a producer & consumer
            MessageProducer producer = this.session.createProducer(reqDest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(this.timeToLive);
            if(!this.timeSync) {
            	producer.setDisableMessageTimestamp(true);
            }

            MessageConsumer consumer = this.session.createConsumer(respDest);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSTimestamp(System.currentTimeMillis());
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(respDest);
            try {
                producer.send(requestMessage);
                logger.debug(String.format("amq, topic, %-20s, %s, %-20s, %s",
                        topicName,
                        requestMessage.getJMSCorrelationID(),
                        respDest,
                        content));
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            producer.close();

            TextMessage reqplyMessage = (TextMessage) consumer.receive(timeout);
            consumer.close();

            this.connection.deleteTempDestination((ActiveMQTempDestination) respDest);
            return reqplyMessage != null ? reqplyMessage.getText() : null;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String send(String topicName, String label, String content, boolean persistent, long timeout, String replyName, NmsMatching matching) {
        try {
            Destination reqDest = this.session.createTopic(topicName);
            Destination respDest = this.session.createTopic(replyName);

            // Create a producer
            MessageProducer producer = this.session.createProducer(reqDest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(this.timeToLive);
            if(!this.timeSync) {
            	producer.setDisableMessageTimestamp(true);
            }

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSTimestamp(System.currentTimeMillis());
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(respDest);
            try {
                producer.send(requestMessage);
                logger.debug(String.format("amq, topic, %-20s, %s, %-20s, %s",
                        topicName,
                        requestMessage.getJMSCorrelationID(),
                        respDest,
                        content));
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            producer.close();

            // Create a consumer
            MessageConsumer consumer = this.session.createConsumer(respDest);
            String reqplyMessage = null;
            long end = System.currentTimeMillis() + timeout;
            try {
                while (System.currentTimeMillis() < end) {
                    TextMessage _reqplyMessage = (TextMessage) consumer.receive(end - System.currentTimeMillis() + 1000);
                    if (_reqplyMessage != null && matching.check(_reqplyMessage.getText())) {
                        reqplyMessage = _reqplyMessage.getText();
                        break;
                    }
                }
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            consumer.close();

            return reqplyMessage;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }
}
