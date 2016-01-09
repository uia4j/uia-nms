/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms.amq;

import java.util.Calendar;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import uia.nms.SubjectProfile;
import uia.nms.SubjectPublisher;

/**
 *
 * @author FW
 */
public class AmqTopicPublisher implements SubjectPublisher {

    private Connection connection;
    private Session session;

    public AmqTopicPublisher(SubjectProfile profile) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(profile.getTarget() + ":" + profile.getPort());
        this.connection = factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start() {
        try {
            this.connection.start();
        } catch (Exception ex) {
        }
    }

    @Override
    public void stop() {
        try {
            this.connection.stop();
        } catch (Exception ex) {
        }
    }

    @Override
    public boolean publish(String topicName, String label, String content) {
        return publish(topicName, label, content, Long.toString(Calendar.getInstance().getTime().getTime())); 
    }

    @Override
    public boolean publish(String topicName, String label, String content, String correlationID) {
        try {
            Topic topic = this.session.createTopic(topicName);
            MessageProducer producer = this.session.createProducer(topic);

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(correlationID);
            requestMessage.setStringProperty("label", label);
            producer.send(requestMessage);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String publish(String topicName, String label, String content, long timeout) {

        try {
            Topic topicSend = this.session.createTopic(topicName);
            Topic topicRcv = this.session.createTemporaryTopic();

            // Create a producer & cousumer
            MessageProducer producer = this.session.createProducer(topicSend);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout);

            MessageConsumer consumer = this.session.createConsumer(topicRcv);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(topicRcv);
            producer.send(requestMessage);

            TextMessage reqplyMessage = (TextMessage) consumer.receive(producer.getTimeToLive());

            producer.close();
            consumer.close();

            return reqplyMessage != null && reqplyMessage.getJMSCorrelationID().equals(requestMessage.getJMSCorrelationID())
                    ? reqplyMessage.getText()
                    : null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String publish(String topicName, String label, String content, long timeout, String replyName) {
        try {
            Topic topicSend = this.session.createTopic(topicName);
            Topic topicRcv = this.session.createTopic(replyName);

            // Create a producer & cousumer
            MessageProducer producer = this.session.createProducer(topicSend);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout);

            MessageConsumer consumer = this.session.createConsumer(topicRcv);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(topicRcv);
            producer.send(requestMessage);

            TextMessage reqplyMessage = (TextMessage) consumer.receive(producer.getTimeToLive());

            producer.close();
            consumer.close();

            return reqplyMessage != null && reqplyMessage.getJMSCorrelationID().equals(requestMessage.getJMSCorrelationID())
                    ? reqplyMessage.getText()
                    : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
