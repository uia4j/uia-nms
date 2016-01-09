/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.SubjectListener;
import uia.nms.SubjectProfile;
import uia.nms.SubjectSubscriber;

/**
 *
 * @author FW
 */
public class AmqQueueSubTest {

    public AmqQueueSubTest() {
    }

    @Test
    public void testPubReply() throws Exception {
        SubjectProfile profile = new SubjectProfile(null, null, "tcp://localhost", "61616");

        final AmqQueuePublisher pub = new AmqQueuePublisher(profile);
        AmqQueueSubscriber sub = new AmqQueueSubscriber(profile);

        sub.addLabel("xml");
        sub.addMessageListener(new SubjectListener() {
            
            public void messageReceived(SubjectSubscriber sub, MessageHeader header, MessageBody body) {
               System.out.println("Receive: " + body.getContent().get("xml"));
               System.out.println("Reply To: " + header.getReplyTopic());
               pub.publish(header.getReplyTopic(), "xml", "You are cute", header.getCorrelationID());
            }
        });

        sub.start("Judy.Test");
        pub.start();
        String result = pub.publish("Judy.Test", "xml", "Judy", 3000, "Judy.Test.Reply");
        System.out.println("Get reply: " + result);
        Thread.sleep(2000);

        pub.stop();
        sub.stop();
    }
}
