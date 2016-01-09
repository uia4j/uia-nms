/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms.amq;

import java.util.TreeSet;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.SubjectException;
import uia.nms.SubjectListener;
import uia.nms.SubjectProfile;
import uia.nms.SubjectSubscriber;

/**
 *
 * @author FW
 */
public class AmqTopicSubscriber implements SubjectSubscriber, MessageListener {

    private TreeSet<String> labels;

    private Vector<SubjectListener> listeners;

    private Connection connection;

    private Session session;

    private MessageConsumer consumer;

    private boolean started;

    public AmqTopicSubscriber(SubjectProfile profile) throws SubjectException, JMSException {
        this.listeners = new Vector<SubjectListener>();
        this.labels = new TreeSet<String>();
        this.started = false;

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(profile.getTarget() + ":" + profile.getPort());
        this.connection = factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void addLabel(String label) {
        this.labels.add(label);
    }

    @Override
    public void addMessageListener(SubjectListener l) {
        if (!this.listeners.contains(l)) {
            this.listeners.add(l);
        }
    }

    @Override
    public void removeMessageListener(SubjectListener l) {
        if (this.listeners.contains(l)) {
            this.listeners.remove(l);
        }
    }

    @Override
    public void start(String topicName) throws SubjectException {
        if (this.started) {
            stop();
        }

        try {
            this.consumer = this.session.createConsumer(this.session.createTopic(topicName));
            this.consumer.setMessageListener(this);
            this.connection.start();
            this.started = true;
        }
        catch (Exception ex) {
            this.started = false;
            throw new SubjectException("start AMQ subscriber failure", ex);
        }
    }

    @Override
    public void stop() {
        if (!this.started) {
            return;
        }

        try {
            this.consumer.setMessageListener(null);
            this.connection.stop();
        }
        catch (Exception ex) {

        }
        this.started = false;
    }

    /**
     * JMS message listener.
     *
     * @param message The message JMS responses.
     */
    @Override
    public void onMessage(Message message) {
        TextMessage tm = (TextMessage) message;
        try {
            String label = tm.getStringProperty("label");

            Topic dest = (Topic) message.getJMSDestination();
            Topic reply = (Topic) message.getJMSReplyTo();
            String replyName = reply == null ? null : reply.getTopicName();
            MessageHeader header = new MessageHeader(
                    dest.getTopicName(),
                    replyName,
                    message.getJMSCorrelationID());
            MessageBody body = new MessageBody();
            if (this.labels.size() == 0) {
                body.put("value", tm.getText());
            }
            else if (this.labels.contains(label)) {
                body.put(label, tm.getText());
            }

            for (SubjectListener l : this.listeners) {
                l.messageReceived(this, header, body);
            }
        }
        catch (Exception ex) {

        }
    }
}