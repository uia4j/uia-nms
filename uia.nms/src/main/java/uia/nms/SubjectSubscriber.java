/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms;


/**
 *
 * @author kyle
 */
public interface SubjectSubscriber {

    public void addLabel(String label);

    public void addMessageListener(SubjectListener l);

    public void removeMessageListener(SubjectListener l);

    public void start(String topicName) throws SubjectException;

    public void stop();
}
