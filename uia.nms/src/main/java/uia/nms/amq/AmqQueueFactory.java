package uia.nms.amq;

import org.apache.activemq.ActiveMQConnectionFactory;

import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsException;
import uia.nms.NmsFactory;
import uia.nms.NmsProducer;

public class AmqQueueFactory extends NmsFactory {

    @Override
    public NmsProducer createProducer(NmsEndPoint endPoint) throws NmsException {
        try {
            return new AmqQueueProducer(connectionFactory(endPoint));
        }
        catch (Exception ex) {
            throw new NmsException("createProducer failed", ex);
        }
    }

    @Override
    public NmsConsumer createConsumer(NmsEndPoint endPoint) throws NmsException {
        try {
            return new AmqQueueConsumer(connectionFactory(endPoint));
        }
        catch (Exception ex) {
            throw new NmsException("createConsumer failed", ex);
        }
    }

    private ActiveMQConnectionFactory connectionFactory(NmsEndPoint endPoint) {
        if ("failover".equals(endPoint.getService())) {
            return new ActiveMQConnectionFactory("failover:" + endPoint.getTarget());
        }
        else {
            return new ActiveMQConnectionFactory(endPoint.getTarget() + ":" + endPoint.getPort());
        }
    }
}
