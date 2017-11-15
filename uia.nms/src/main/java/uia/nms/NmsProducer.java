package uia.nms;

public interface NmsProducer {

    public void start() throws NmsException;

    public void stop();

    public boolean send(String subjectName, String label, String content, boolean persistent);

    public boolean send(String subjectName, String label, String content, boolean persistent, String correlationID);

    public String send(String subjectName, String label, String content, boolean persistent, long timeout);

    public String send(String subjectName, String label, String content, boolean persistent, long timeout, String replyName);
}
