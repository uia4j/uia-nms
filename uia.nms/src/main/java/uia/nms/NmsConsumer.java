package uia.nms;

public interface NmsConsumer {

    public void addLabel(String label);

    public void addMessageListener(NmsMessageListener l);

    public void removeMessageListener(NmsMessageListener l);

    public void start(String subjectName) throws NmsException;

    public void stop();

    public NmsProducer createProducer();
}
