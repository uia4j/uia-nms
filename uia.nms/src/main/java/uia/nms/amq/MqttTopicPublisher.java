package uia.nms.amq;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import uia.nms.NmsException;
import uia.nms.NmsMatching;
import uia.nms.NmsProducer;

public class MqttTopicPublisher implements NmsProducer {

    IMqttClient publisher;

    public MqttTopicPublisher() throws MqttException {
        this.publisher = new MqttClient("tcp://localhost:1883", UUID.randomUUID().toString());
    }

    @Override
    public boolean isTimeSync() {
        return false;
    }

    @Override
    public void setTimeSync(boolean timeSync) {
    }

    @Override
    public void setTimeToLive(int timeToLive) {
    }

    @Override
    public int getTimeToLive() {
        return 0;
    }

    @Override
    public void start() throws NmsException {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            this.publisher.connect(options);
        }
        catch (MqttException e) {
            throw new NmsException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean send(String subjectName, String label, String content, boolean persistent) throws NmsException {
        try {
            MqttMessage msg = new MqttMessage(content.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            this.publisher.publish(subjectName, msg);
        }
        catch (MqttException e) {
            throw new NmsException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean send(String subjectName, String label, String content, boolean persistent, String correlationID) throws NmsException {
        try {
            this.publisher = new MqttClient("tcp://iot.eclipse.org:1883", correlationID);
        }
        catch (MqttException e) {
            throw new NmsException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public String send(String subjectName, String label, String content, boolean persistent, long timeout) throws NmsException {
        return null;
    }

    @Override
    public String send(String subjectName, String label, String content, boolean persistent, long timeout, String replyName, NmsMatching matching) throws NmsException {
        return null;
    }

}
