/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms.amq;

import java.util.Calendar;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.apache.log4j.Logger;

import uia.nms.SubjectProfile;
import uia.nms.SubjectPublisher;

/**
 *
 * @author FW
 */
public class AmqTopicPublisher implements SubjectPublisher {

    private static final Logger logger = Logger.getLogger(AmqTopicPublisher.class);

    private ActiveMQConnection connection;

    private Session session;

    public AmqTopicPublisher(SubjectProfile profile) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(profile.getTarget() + ":" + profile.getPort());
        this.connection = (ActiveMQConnection) factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start() {
        try {
            this.connection.start();
        }
        catch (Exception ex) {
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
    public boolean publish(String topicName, String label, String content, boolean persistent) {
        return publish(topicName, label, content, persistent, Long.toString(Calendar.getInstance().getTime().getTime()));
    }

    @Override
    public boolean publish(String topicName, String label, String content, boolean persistent, String correlationID) {
        try {
            Topic topic = this.session.createTopic(topicName);
            MessageProducer producer = this.session.createProducer(topic);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(correlationID);
            requestMessage.setStringProperty("label", label);
            producer.send(requestMessage);
            logger.debug(String.format("amq> %s >>> %s, cid:%s", topicName, null, requestMessage.getJMSCorrelationID()));

            return true;
        }
        catch (Exception ex) {
            logger.error(ex);
            return false;
        }
    }

    @Override
    public String publish(String topicName, String label, String content, boolean persistent, long timeout) {
        try {
            Topic topicSend = this.session.createTopic(topicName);
            Destination destRcv = this.session.createTemporaryTopic();
            // Destination destRcv = this.session.createTemporaryQueue();

            // Create a producer & consumer
            MessageProducer producer = this.session.createProducer(topicSend);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout);

            // MessageConsumer consumer = this.session.createConsumer(destRcv);
            MessageConsumer consumer = this.session.createConsumer(destRcv);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(destRcv);
            producer.send(requestMessage);
            logger.debug(String.format("amq> %s >>> %s, cid:%s", topicName, destRcv, requestMessage.getJMSCorrelationID()));

            TextMessage reqplyMessage = (TextMessage) consumer.receive(producer.getTimeToLive());
            this.connection.deleteTempDestination((ActiveMQTempDestination) destRcv);

            producer.close();
            consumer.close();

            /**
            return reqplyMessage != null && reqplyMessage.getJMSCorrelationID().equals(requestMessage.getJMSCorrelationID())
                    ? reqplyMessage.getText()
                    : null;
             */
            return reqplyMessage != null ? reqplyMessage.getText() : null;
        }
        catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public String publish(String topicName, String label, String content, boolean persistent, long timeout, String replyName) {
        try {
            Topic topicSend = this.session.createTopic(topicName);
            Topic topicRcv = this.session.createTopic(replyName);

            // Create a producer & consumer
            MessageProducer producer = this.session.createProducer(topicSend);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout);

            MessageConsumer consumer = this.session.createConsumer(topicRcv);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(topicRcv);
            producer.send(requestMessage);
            logger.debug(String.format("amq> %s >>> %s, cid:%s", topicName, replyName, requestMessage.getJMSCorrelationID()));

            TextMessage reqplyMessage = (TextMessage) consumer.receive(producer.getTimeToLive());

            producer.close();
            consumer.close();

            /**
            return reqplyMessage != null && reqplyMessage.getJMSCorrelationID().equals(requestMessage.getJMSCorrelationID())
                    ? reqplyMessage.getText()
                    : null;
             */
            return reqplyMessage != null ? reqplyMessage.getText() : null;
        }
        catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }
}
