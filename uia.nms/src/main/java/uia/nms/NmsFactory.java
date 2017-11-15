package uia.nms;

public abstract class NmsFactory {

    public abstract NmsProducer createProducer(NmsEndPoint endPoint) throws NmsException;

    public abstract NmsConsumer createConsumer(NmsEndPoint endPoint) throws NmsException;
}
