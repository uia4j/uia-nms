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
    public void start();
    
    public void stop();

    public boolean publish(String topicName, String label, String content);

    public boolean publish(String topicName, String label, String content, String correlationID);

    public String publish(String topicName, String label, String content, long timeout);

    public String publish(String topicName, String label, String content, long timeout, String replyName);
}
