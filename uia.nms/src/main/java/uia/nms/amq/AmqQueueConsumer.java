package uia.nms.amq;

import java.io.IOException;
import java.util.TreeSet;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsException;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class AmqQueueConsumer implements NmsConsumer, MessageListener, TransportListener {

    private ActiveMQConnectionFactory factory;

    private TreeSet<String> labels;

    private Vector<NmsMessageListener> listeners;

    private ActiveMQConnection connection;

    private Session session;

    private MessageConsumer consumer;

    private boolean started;

    public AmqQueueConsumer(ActiveMQConnectionFactory factory) throws NmsException, JMSException {
        this.factory = factory;
        this.listeners = new Vector<NmsMessageListener>();
        this.labels = new TreeSet<String>();
        this.started = false;
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
    public void start(String queueName) throws NmsException {
        if (this.started) {
            stop();
        }

        try {
            this.connection = (ActiveMQConnection) this.factory.createConnection();
            this.connection.addTransportListener(this);

            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            this.consumer = this.session.createConsumer(this.session.createQueue(queueName));
            this.consumer.setMessageListener(this);

            this.connection.start();
            this.started = true;
        }
        catch (Exception ex) {
            this.started = false;
            throw new NmsException("start AMQ(QueueConsumer) failed", ex);
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

            Queue dest = (Queue) message.getJMSDestination();
            Queue reply = (Queue) message.getJMSReplyTo();

            String replyName = reply == null ? null : reply.getQueueName();
            MessageHeader header = new MessageHeader(
                    dest.getQueueName(),
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
            ex.printStackTrace();
        }
    }

    @Override
    public NmsProducer createProducer() {
        try {
            return new AmqQueueProducer(this.factory);
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
        for (NmsMessageListener l : this.listeners) {
            //l.broken(this);
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