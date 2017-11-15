package uia.nms;

public interface NmsMessageListener {

    public void messageReceived(NmsConsumer consumer, MessageHeader header, MessageBody body);
}
