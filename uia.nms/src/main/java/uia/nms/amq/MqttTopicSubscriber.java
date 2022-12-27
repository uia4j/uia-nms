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

import java.util.UUID;
import java.util.Vector;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import uia.nms.NmsConsumer;
import uia.nms.NmsException;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;
import uia.nms.NmsTransportListener;

/**
 * ActiveMQ TOPIC subscriber implementation
 *
 * @author Kan
 *
 */
public class MqttTopicSubscriber implements NmsConsumer, IMqttMessageListener {

    private final Vector<NmsMessageListener> listeners;

    MqttClient sub;

    MqttTopicSubscriber() throws Exception {
        this.sub = new MqttClient("tcp://localhost:1883", UUID.randomUUID().toString());
        this.listeners = new Vector<NmsMessageListener>();
    }

    @Override
    public NmsTransportListener getTransportListener() {
        return null;
    }

    @Override
    public void setTransportListener(NmsTransportListener transportListener) {
    }

    @Override
    public void addLabel(String label) {
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
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            this.sub.connect(options);
            this.sub.subscribe(topicName, this);
        }
        catch (MqttException e) {
            throw new NmsException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        try {
            this.sub.close();
        }
        catch (MqttException e) {
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(new String(message.getPayload()));
    }

    @Override
    public NmsProducer createProducer() {
        return null;
    }
}