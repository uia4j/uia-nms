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
    public void testPubReply1() throws Exception {
        SubjectProfile profile = new SubjectProfile(null, null, "tcp://localhost", "61616");

        final AmqQueuePublisher pub = new AmqQueuePublisher(profile);
        AmqQueueSubscriber sub = new AmqQueueSubscriber(profile);

        sub.addLabel("xml");
        sub.addMessageListener(new SubjectListener() {

            @Override
            public void messageReceived(SubjectSubscriber sub, MessageHeader header, MessageBody body) {
                System.out.println("Receive: " + body.getContent().get("xml"));
                System.out.println("Reply To: " + header.getTargetDest());
                pub.publish(header.getTargetDest(), "xml", "You are cute", false, header.getCorrelationID());
            }
        });

        sub.start("Judy.Test");
        pub.start();
        String result = pub.publish("Judy.Test", "xml", "Judy", false, 3000, "Judy.Test.Reply");
        System.out.println("Get reply: " + result);
        Thread.sleep(2000);

        pub.stop();
        sub.stop();
    }

    @Test
    public void testPubReply2() throws Exception {
        SubjectProfile profile = new SubjectProfile(null, null, "tcp://localhost", "61616");

        final AmqQueuePublisher pub = new AmqQueuePublisher(profile);
        AmqQueueSubscriber sub = new AmqQueueSubscriber(profile);

        sub.addLabel("xml");
        sub.addMessageListener(new SubjectListener() {

            @Override
            public void messageReceived(SubjectSubscriber sub, MessageHeader header, MessageBody body) {
                System.out.println("Receive: " + body.getContent().get("xml"));
                System.out.println("Reply To: " + header.getTargetDest());
                pub.publish(header.getTargetDest(), "xml", "You are cute", false, header.getCorrelationID());
            }
        });

        sub.start("Judy.Test");
        pub.start();
        String result = pub.publish("Judy.Test", "xml", "Judy", false, 3000);
        System.out.println("Get reply: " + result);
        Thread.sleep(2000);

        pub.stop();
        sub.stop();
    }
}
