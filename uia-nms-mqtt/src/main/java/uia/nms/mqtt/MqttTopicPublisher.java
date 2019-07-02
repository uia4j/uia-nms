package uia.nms.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import uia.nms.NmsException;

public class MqttTopicPublisher {
	
	private MqttClient client;
	
	public MqttTopicPublisher() throws MqttException {
		String publisherId = UUID.randomUUID().toString();
		this.client = new MqttClient("tcp://localhost:1883",publisherId);
	}

    public void start() throws NmsException {
        try {
    		MqttConnectOptions options = new MqttConnectOptions();
    		options.setAutomaticReconnect(true);
    		options.setCleanSession(true);
    		options.setConnectionTimeout(10);
    		this.client.connect(options);        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new NmsException("start AMQ(TopicProducer) failed", ex);
        }
    }

    public void stop() {
        try {
        	this.client.close();
        }
        catch (Exception ex) {
        }
    }

    public boolean send(String topicName, String label, String content, boolean persistent) {
        try {
        	MqttMessage msg = new MqttMessage(content.getBytes());
        	msg.setQos(0);
            msg.setRetained(true);
			this.client.publish(topicName, msg);
	        return true;
		} catch (Exception e) {
	        return false;
		}     
	}
}
