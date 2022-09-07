package uia.nms.rmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.TopologyRecoveryException;

import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsException;
import uia.nms.NmsFactory;
import uia.nms.NmsProducer;

public class RmqQueueFactory extends NmsFactory implements ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqQueueFactory.class);

    private ConnectionFactory factory;

    public RmqQueueFactory() {
        this.factory = new ConnectionFactory();
        this.factory.setAutomaticRecoveryEnabled(true);
        this.factory.setConnectionTimeout(15000);
        this.factory.setRequestedHeartbeat(60);
        this.factory.setExceptionHandler(this);
    }

    @Override
    public NmsProducer createProducer(NmsEndPoint endPoint) throws NmsException {
        try {
            if (endPoint.getService() != null) {
                this.factory.setVirtualHost(endPoint.getService());
            }
            this.factory.setVirtualHost(endPoint.getService());
            this.factory.setHost(endPoint.getTarget());
            this.factory.setPort(Integer.parseInt(endPoint.getPort()));
            this.factory.setUsername(endPoint.getUser());
            this.factory.setPassword(endPoint.getPassword());
            Connection conn = this.factory.newConnection();
            return new RmqQueueProducer(conn);
        }
        catch (Exception ex) {
            throw new NmsException(ex.getMessage(), ex);
        }
    }

    @Override
    public NmsConsumer createConsumer(NmsEndPoint endPoint) throws NmsException {
        try {
            if (endPoint.getService() != null) {
                this.factory.setVirtualHost(endPoint.getService());
            }
            this.factory.setHost(endPoint.getTarget());
            this.factory.setPort(Integer.parseInt(endPoint.getPort()));
            this.factory.setUsername(endPoint.getUser());
            this.factory.setPassword(endPoint.getPassword());
            Connection conn = this.factory.newConnection();
            return new RmqQueueConsumer(conn);
        }
        catch (Exception ex) {
            throw new NmsException(ex.getMessage(), ex);
        }
    }

    @Override
    public void handleUnexpectedConnectionDriverException(Connection conn, Throwable exception) {
        logger.error("UnexpectedConnectionDriver", exception);

    }

    @Override
    public void handleReturnListenerException(Channel channel, Throwable exception) {
        logger.error("ReturnListener", exception);
    }

    @Override
    public void handleConfirmListenerException(Channel channel, Throwable exception) {
        logger.error("ConfirmListener", exception);
    }

    @Override
    public void handleBlockedListenerException(Connection connection, Throwable exception) {
        logger.error("BlockedListener", exception);
    }

    @Override
    public void handleConsumerException(Channel channel, Throwable exception, Consumer consumer, String consumerTag, String methodName) {
        logger.error("Consumer", exception);
    }

    @Override
    public void handleConnectionRecoveryException(Connection conn, Throwable exception) {
        logger.error("ConnectionRecovery", exception);
    }

    @Override
    public void handleChannelRecoveryException(Channel ch, Throwable exception) {
        logger.error("ChannelRecovery", exception);
    }

    @Override
    public void handleTopologyRecoveryException(Connection conn, Channel ch, TopologyRecoveryException exception) {
        logger.error("TopologyRecovery", exception);
    }

}
