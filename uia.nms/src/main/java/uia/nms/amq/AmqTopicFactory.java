package uia.nms.amq;

import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsException;
import uia.nms.NmsFactory;
import uia.nms.NmsProducer;

public class AmqTopicFactory extends NmsFactory {

    @Override
    public NmsProducer createProducer(NmsEndPoint endPoint) throws NmsException {
        try {
            return new AmqTopicPublisher(endPoint);
        }
        catch (Exception ex) {
            throw new NmsException("createProducer failed", ex);
        }
    }

    @Override
    public NmsConsumer createConsumer(NmsEndPoint endPoint) throws NmsException {
        try {
            return new AmqTopicSubscriber(endPoint);
        }
        catch (Exception ex) {
            throw new NmsException("createConsumer failed", ex);
        }
    }
}
