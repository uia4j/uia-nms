package uia.nms.rmq;

import java.util.UUID;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import uia.nms.NmsException;
import uia.nms.NmsMatching;
import uia.nms.NmsProducer;

public class RmqQueueProducer implements NmsProducer {

    private Connection conn;

    private Channel ch;

    public RmqQueueProducer(Connection conn) throws NmsException {
        this.conn = conn;
    }

    @Override
    public boolean isTimeSync() {
        return false;
    }

    @Override
    public void setTimeSync(boolean timeSync) {
    }

    @Override
    public void setTimeToLive(int timeToLive) {
    }

    @Override
    public int getTimeToLive() {
        return 0;
    }

    @Override
    public void start() throws NmsException {
        if (this.conn == null) {
            throw new NmsException("The connection is closed. Recreate the instance.");
        }
        try {
            this.ch = this.conn.createChannel();
        }
        catch (Exception ex) {
            throw new NmsException("producer failed", ex);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean send(String subjectName, String label, String content, boolean persistent) {
        try {
            BasicProperties props = new BasicProperties.Builder()
                    .contentType("text/plain")
                    .deliveryMode(2)
                    .priority(1)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
            this.ch.queueDeclare(subjectName, false, false, false, null);
            this.ch.basicPublish("", subjectName, props, content.getBytes());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean send(String subjectName, String label, String content, boolean persistent, String correlationID) {
        try {
            BasicProperties props = new BasicProperties.Builder()
                    .contentType("text/plain")
                    .deliveryMode(2)
                    .priority(1)
                    .contentEncoding("utf-8")
                    .correlationId(correlationID)
                    .build();
            this.ch.queueDeclare(subjectName, false, false, false, null);
            this.ch.basicPublish("", subjectName, props, content.getBytes("utf-8"));
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String send(String subjectName, String label, String content, boolean persistent, long timeout) throws NmsException {
        throw new NmsException("not support");
    }

    @Override
    public String send(String subjectName, String label, String content, boolean persistent, long timeout, String replyName, NmsMatching matching) throws NmsException {
        throw new NmsException("not support");
    }

}
