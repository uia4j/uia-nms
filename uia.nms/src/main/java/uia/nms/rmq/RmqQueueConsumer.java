package uia.nms.rmq;

import java.io.IOException;
import java.util.ArrayList;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsException;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;
import uia.nms.NmsTransportListener;

public class RmqQueueConsumer implements NmsConsumer {

    private Connection conn;

    private Channel ch;

    private ArrayList<NmsMessageListener> listeners;

    private NmsTransportListener transportListener;

    public RmqQueueConsumer(Connection conn) throws NmsException {
        this.conn = conn;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void setTransportListener(NmsTransportListener transportListener) {
        this.transportListener = transportListener;
    }

    @Override
    public NmsTransportListener getTransportListener() {
        return this.transportListener;
    }

    @Override
    public void addLabel(String label) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addMessageListener(NmsMessageListener l) {
        this.listeners.add(l);

    }

    @Override
    public void removeMessageListener(NmsMessageListener l) {
        this.listeners.remove(l);
    }

    @Override
    public void start(String queueName) throws NmsException {
        if (this.conn == null) {
            throw new NmsException("The connection is closed. Recreate the instance.");
        }
        try {
            this.ch = this.conn.createChannel();
            this.ch.queueDeclare(queueName, true, false, false, null);
            this.ch.basicConsume(queueName, new Consumer() {

                @Override
                public void handleConsumeOk(String consumerTag) {
                }

                @Override
                public void handleCancelOk(String consumerTag) {
                }

                @Override
                public void handleCancel(String consumerTag) throws IOException {
                }

                @Override
                public void handleDelivery(String arg0, Envelope enve, BasicProperties props, byte[] arg3) throws IOException {
                    // important
                    RmqQueueConsumer.this.ch.basicAck(enve.getDeliveryTag(), false);
                    MessageHeader header = new MessageHeader(
                            enve.getRoutingKey(),
                            props.getReplyTo(),
                            props.getCorrelationId());
                    MessageBody body = new MessageBody();
                    body.put("data", new String(arg3));
                    handleMessage(header, body);

                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                }

                @Override
                public void handleRecoverOk(String consumerTag) {
                }

            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new NmsException("consumer failed", ex);
        }

    }

    @Override
    public void stop() {
    }

    @Override
    public NmsProducer createProducer() {
        // TODO Auto-generated method stub
        return null;
    }

    private void handleMessage(MessageHeader header, MessageBody body) {
        for (NmsMessageListener l : this.listeners) {
            try {
                l.messageReceived(this, header, body);
            }
            catch (Exception ex) {

            }
        }
    }

}
