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

import java.io.IOException;
import java.util.TreeSet;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsException;
import uia.nms.NmsTransportListener;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

/**
 * ActiveMQ TOPIC subscriber implementation
 * 
 * @author Kan
 *
 */
public class AmqTopicSubscriber implements NmsConsumer, MessageListener, TransportListener {

    private final ActiveMQConnectionFactory factory;

    private final TreeSet<String> labels;

    private final Vector<NmsMessageListener> listeners;

    private ActiveMQConnection connection;

    private Session session;

    private MessageConsumer consumer;

    private boolean started;

    private NmsTransportListener transportListener;

    AmqTopicSubscriber(ActiveMQConnectionFactory factory) throws NmsException, JMSException {
        this.factory = factory;
        this.factory.setOptimizeAcknowledge(true);
        this.factory.setAlwaysSessionAsync(false);
        this.labels = new TreeSet<String>();
        this.listeners = new Vector<NmsMessageListener>();

        this.started = false;
    }

    @Override
    public NmsTransportListener getTransportListener() {
        return this.transportListener;
    }

    @Override
    public void setTransportListener(NmsTransportListener transportListener) {
        this.transportListener = transportListener;
    }

    @Override
    public void addLabel(String label) {
        this.labels.add(label);
    }

    @Override
    public void addMessageListener(NmsMessageListener l) {
        if (!this.listeners.contains(l)) {
            this.listeners.add(l);
        }
    }

    @Override
    public void removeMessageListener(NmsMessageListener l) {
        if (this.listeners.contains(l)) {
            this.listeners.remove(l);
        }
    }

    @Override
    public void start(String topicName) throws NmsException {
        if (this.started) {
            stop();
        }

        // https://activemq.apache.org/performance-tuning.html
        try {
            this.connection = (ActiveMQConnection) this.factory.createConnection();
            this.connection.addTransportListener(this);

            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            this.consumer = this.session.createConsumer(this.session.createTopic(topicName));
            this.consumer.setMessageListener(this);

            this.connection.start();
            this.started = true;

        }
        catch (Exception ex) {
            this.started = false;
            throw new NmsException("start AMQ(TopicConsumer) failed", ex);
        }
    }

    @Override
    public void stop() {
        if (!this.started) {
            return;
        }

        try {
            this.consumer.setMessageListener(null);
            this.session.close();
            this.connection.close();
            this.consumer.close();
        }
        catch (Exception ex) {

        }

        this.connection = null;
        this.session = null;
        this.consumer = null;
        this.started = false;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage tm = (TextMessage) message;
        try {
            String label = tm.getStringProperty("label");

            Topic dest = (Topic) message.getJMSDestination();
            Topic reply = (Topic) message.getJMSReplyTo();

            String replyName = reply == null ? null : reply.getTopicName();
            MessageHeader header = new MessageHeader(
                    dest.getTopicName(),
                    replyName,
                    message.getJMSCorrelationID());
            MessageBody body = new MessageBody();
            if (this.labels.size() == 0) {
                body.put("value", tm.getText());
            }
            else if (this.labels.contains(label)) {
                body.put(label, tm.getText());
            }
            else {
                return;
            }

            for (NmsMessageListener l : this.listeners) {
                l.messageReceived(this, header, body);
            }
        }
        catch (Exception ex) {

        }
    }

    @Override
    public NmsProducer createProducer() {
        try {
        	NmsProducer producer = new AmqTopicPublisher(this.factory);
        	producer.setTimeToLive(2000);
        	return producer;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onCommand(Object command) {
        // ActiveMQ TransportListener
    }

    @Override
    public void onException(IOException error) {
        // ActiveMQ TransportListener
        if (!this.started) {
            return;
        }
        stop();
        if (this.transportListener != null) {
            this.transportListener.broken(this);
        }
    }

    @Override
    public void transportInterupted() {
        // ActiveMQ TransportListener

    }

    @Override
    public void transportResumed() {
        // ActiveMQ TransportListener
    }
}