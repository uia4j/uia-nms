/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uia.nms;

/**
 *
 * @author kyle
 */
public class MessageHeader {

    private final String listen;

    private final String reply;
    
    private final String correlationID;

    public MessageHeader(String listenTopic, String replyTopic, String correlationID) {
        this.listen = listenTopic;
        this.reply = replyTopic;
        this.correlationID = correlationID;
    }
    
    public String getCorrelationID() {
        return this.correlationID;
    }
    
    public String getListenTopic() {
        return this.listen;
    }

    public String getReplyTopic() {
        return this.reply;
    }
}
