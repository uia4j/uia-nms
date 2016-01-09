/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uia.nms;

/**
 *
 * @author kyle
 */
public interface SubjectListener {

    public void messageReceived(SubjectSubscriber sub, MessageHeader header, MessageBody body);
}
