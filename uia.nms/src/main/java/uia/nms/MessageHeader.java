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

    private final String sourceDest;

    private final String targetDest;
    
    private final String correlationID;

    public MessageHeader(String listenTopic, String replyTopic, String correlationID) {
        this.sourceDest = listenTopic;
        this.targetDest = replyTopic;
        this.correlationID = correlationID;
    }
    
    public String getCorrelationID() {
        return this.correlationID;
    }
    
    public String getSourceDest() {
        return this.sourceDest;
    }

    public String getTargetDest() {
        return this.targetDest;
    }
}
