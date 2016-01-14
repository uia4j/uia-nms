/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms.amq;

import java.util.Calendar;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import uia.nms.SubjectException;
import uia.nms.SubjectProfile;
import uia.nms.SubjectPublisher;

/**
 *
 * @author FW
 */
public class AmqQueuePublisher implements SubjectPublisher {

    private Connection connection;

    private Session session;

    public AmqQueuePublisher(SubjectProfile profile) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(profile.getTarget() + ":" + profile.getPort());
        this.connection = factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start() throws SubjectException {
        try {
            this.connection.start();
        }
        catch (Exception ex) {
            throw new SubjectException("start AMQ publisher faulure", ex);
        }
    }

    @Override
    public void stop() {
        try {
            this.connection.stop();
        }
        catch (Exception ex) {

        }
    }

    @Override
    public boolean publish(String queueName, String label, String content, boolean persistent) {
        return publish(queueName, label, content, persistent, Long.toString(Calendar.getInstance().getTime().getTime()));
    }

    @Override
    public boolean publish(String queueName, String label, String content, boolean persistent, String correlationID) {
        try {
            Destination dest = this.session.createQueue(queueName);
            MessageProducer producer = this.session.createProducer(dest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(correlationID);
            requestMessage.setStringProperty("label", label);
            producer.send(requestMessage);

            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String publish(String queueName, String label, String content, boolean persistent, long timeout) {

        try {
            Destination dest = this.session.createQueue(queueName);
            Destination destRcv = this.session.createTemporaryQueue();

            // Create a producer & cousumer
            MessageProducer producer = this.session.createProducer(dest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout);

            MessageConsumer consumer = this.session.createConsumer(destRcv);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(destRcv);
            producer.send(requestMessage);

            TextMessage reqplyMessage = (TextMessage) consumer.receive(producer.getTimeToLive());

            producer.close();
            consumer.close();

            return reqplyMessage != null && reqplyMessage.getJMSCorrelationID().equals(requestMessage.getJMSCorrelationID())
                    ? reqplyMessage.getText()
                    : null;
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String publish(String queueName, String label, String content, boolean persistent, long timeout, String replyName) {
        try {
            Destination dest = this.session.createQueue(queueName);
            Destination destRcv = this.session.createTemporaryQueue();

            // Create a producer & cousumer
            MessageProducer producer = this.session.createProducer(dest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout);

            MessageConsumer consumer = this.session.createConsumer(destRcv);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(destRcv);
            producer.send(requestMessage);

            TextMessage reqplyMessage = (TextMessage) consumer.receive(producer.getTimeToLive());

            producer.close();
            consumer.close();

            return reqplyMessage != null && reqplyMessage.getJMSCorrelationID().equals(requestMessage.getJMSCorrelationID())
                    ? reqplyMessage.getText()
                    : null;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
