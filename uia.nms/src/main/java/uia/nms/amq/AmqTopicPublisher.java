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
import org.apache.log4j.Logger;

import uia.nms.NmsException;
import uia.nms.NmsMatching;
import uia.nms.NmsProducer;

public class AmqTopicPublisher implements NmsProducer {

    private static final Logger logger = Logger.getLogger(AmqTopicPublisher.class);

    private ActiveMQConnection connection;

    private Session session;

    public AmqTopicPublisher(ActiveMQConnectionFactory factory) throws JMSException {
        this.connection = (ActiveMQConnection) factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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

            TextMessage requestMessage = this.session.createTextMessage(content);
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
            //producer.setTimeToLive(timeout);

            MessageConsumer consumer = this.session.createConsumer(respDest);

            TextMessage requestMessage = this.session.createTextMessage(content);
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
            producer.setTimeToLive(timeout * 5);

            TextMessage requestMessage = this.session.createTextMessage(content);
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
