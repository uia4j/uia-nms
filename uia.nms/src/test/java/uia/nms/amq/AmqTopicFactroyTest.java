package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;

public class AmqTopicFactroyTest {

    @Test
    public void testPubSub() throws Exception {
        NmsEndPoint profile = new NmsEndPoint(null, null, "tcp://localhost", "61616");

        AmqTopicPublisher pub = new AmqTopicPublisher(profile);
        AmqTopicSubscriber sub = new AmqTopicSubscriber(profile);
        sub.addLabel("xml");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println(body.getContent().get("xml"));
            }

        });

        sub.start("a.b.c");

        pub.start();
        pub.send("a.b.c", "xml", "hello judy", false);
        Thread.sleep(5000);

        pub.stop();
        sub.stop();
    }
}
