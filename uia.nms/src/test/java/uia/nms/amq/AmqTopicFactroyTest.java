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
public class AmqTopicFactroyTest {
    
    public AmqTopicFactroyTest() {
    }
    
    @Test
    public void testPubSub() throws Exception {
        SubjectProfile profile = new SubjectProfile(null, null, "tcp://localhost", "61616");
        
        AmqTopicPublisher pub = new AmqTopicPublisher(profile);
        AmqTopicSubscriber sub = new AmqTopicSubscriber(profile);
        sub.addLabel("xml");
        sub.addMessageListener(new SubjectListener() {

            public void messageReceived(SubjectSubscriber sub, MessageHeader header, MessageBody body) {
                System.out.println(body.getContent().get("xml"));
            }
        
        });
        
        sub.start("a.b.c");
        
        pub.start();
        pub.publish("a.b.c", "xml", "hello judy");
        Thread.sleep(5000);
        
        pub.stop();
        sub.stop();
    }
}
