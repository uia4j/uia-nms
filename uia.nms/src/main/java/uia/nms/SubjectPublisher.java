/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms;

/**
 *
 * @author kyle
 */
public interface SubjectPublisher
{

    public void start() throws SubjectException;

    public void stop();

    public boolean publish(String topicName, String label, String content, boolean persistent);

    public boolean publish(String topicName, String label, String content, boolean persistent, String correlationID);

    public String publish(String topicName, String label, String content, boolean persistent, long timeout);

    public String publish(String topicName, String label, String content, boolean persistent, long timeout, String replyName);
}
