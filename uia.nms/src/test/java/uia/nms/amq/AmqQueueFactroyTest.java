package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class AmqQueueFactroyTest {

    @Test
    public void testPubSub() throws Exception {
        NmsEndPoint profile = new NmsEndPoint(null, null, "tcp://localhost", "61616");

        AmqQueueFactory factory = new AmqQueueFactory();
        NmsConsumer sub = factory.createConsumer(profile);
        sub.addLabel("xml");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("got:" + body.getContent().get("xml"));
            }
        });

        sub.start("a.b.c");

        NmsProducer pub = factory.createProducer(profile);
        pub.start();
        pub.send("a.b.c", "xml", "hello judy", false);
        Thread.sleep(5000);

        pub.stop();
        sub.stop();
    }
}
