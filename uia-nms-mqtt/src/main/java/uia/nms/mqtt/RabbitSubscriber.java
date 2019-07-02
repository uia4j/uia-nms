package uia.nms.mqtt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import uia.nms.NmsException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;

public class RabbitSubscriber {
	
	private Connection connection;

	private Channel channel;
	
	public RabbitSubscriber() throws Exception {
	}
	
    public void start(String topicName) throws NmsException {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();

			// channel.queueDeclare(topicName, false, false, false, null);
			channel.basicConsume(topicName, true, this::receive, this::cancel);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NmsException("failed", e);
		}	
	}
    
    public void stop() {
    	try {
			this.channel.close();
	    	this.connection.close();
		} catch (Exception e) {
		}
    }
	
	public void receive(String consumerTag, Delivery delivery) {
		try {
			String message = new String(delivery.getBody(), "UTF-8");
		    System.out.println(" [x] Received '" + message + "'");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
	
	public void cancel(String consumerTag) {
		
	}
}
