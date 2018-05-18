package uia.nms.amq;

import java.util.Calendar;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
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

public class AmqQueueProducer implements NmsProducer {

    private static final Logger logger = Logger.getLogger(AmqQueueProducer.class);

    private ActiveMQConnection connection;

    private Session session;

    public AmqQueueProducer(ActiveMQConnectionFactory factory) throws Exception {
        this.connection = (ActiveMQConnection) factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start() throws NmsException {
        try {
            this.connection.start();
        }
        catch (Exception ex) {
            throw new NmsException("start AMQ(QueueProducer) failed", ex);
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
    public boolean send(String queueName, String label, String content, boolean persistent) {
        return send(queueName, label, content, persistent, Long.toString(Calendar.getInstance().getTime().getTime()));
    }

    @Override
    public boolean send(String queueName, String label, String content, boolean persistent, String correlationID) {
        try {
            Destination reqDest = this.session.createQueue(queueName);
            MessageProducer producer = this.session.createProducer(reqDest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(60000);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(correlationID);
            requestMessage.setStringProperty("label", label);
            producer.send(requestMessage);
            logger.debug(String.format("amq, queue, %-20s, %s, %-20s, %s",
                    queueName,
                    requestMessage.getJMSCorrelationID(),
                    "-",
                    content));
            producer.close();

            return true;
        }
        catch (Exception ex) {
            logger.error(ex);
            return false;
        }
    }

    @Override
    public String send(String queueName, String label, String content, boolean persistent, long timeout) {
        try {
            Destination reqDest = this.session.createQueue(queueName);
            Destination respDest = this.session.createTemporaryQueue();

            // Create a producer & consumer
            MessageConsumer consumer = this.session.createConsumer(respDest);

            MessageProducer producer = this.session.createProducer(reqDest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(timeout * 60);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(respDest);
            try {
                producer.send(requestMessage);
                logger.debug(String.format("amq, queue, %-20s, %s, %-20s, %s",
                        queueName,
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
    public String send(String queueName, String label, String content, boolean persistent, long timeout, String replyName, NmsMatching matching) {
        try {
            Destination reqDest = this.session.createQueue(queueName);
            Destination respDest = this.session.createQueue(replyName);

            // Create a producer
            MessageProducer producer = this.session.createProducer(reqDest);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            // producer.setTimeToLive(timeout * 60);

            TextMessage requestMessage = this.session.createTextMessage(content);
            requestMessage.setJMSCorrelationID(Long.toString(Calendar.getInstance().getTime().getTime()));
            requestMessage.setStringProperty("label", label);
            requestMessage.setJMSReplyTo(respDest);
            try {
                producer.send(requestMessage);
                logger.debug(String.format("amq, queue, %-20s, %s, %-20s, %s",
                        queueName,
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
            try {
                long end = System.currentTimeMillis() + timeout;
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
