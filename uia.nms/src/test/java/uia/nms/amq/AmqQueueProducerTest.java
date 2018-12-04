package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMatching;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class AmqQueueProducerTest implements NmsMatching {

    private long time;

    @Test
    public void testPub() throws Exception {
        // NmsEndPoint endPoint1 = new NmsEndPoint(null, null, "tcp://localhost", "61616");
        NmsEndPoint endPoint2 = new NmsEndPoint(null, null, "tcp://localhost", "62616");
        NmsEndPoint endPoint3 = new NmsEndPoint(null, null, "tcp://localhost", "63616");

        NmsConsumer sub2 = new AmqQueueFactory().createConsumer(endPoint2);
        sub2.addLabel("value");
        sub2.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                // String content = body.getContent().get("xml");
                // System.out.println("2> receive: " + content);
                AmqQueueProducerTest.this.time = System.currentTimeMillis();
            }
        });
        sub2.start("Judy.Test");

        NmsConsumer sub3 = new AmqQueueFactory().createConsumer(endPoint3);
        sub3.addLabel("value");
        sub3.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                // String content = body.getContent().get("xml");
                // System.out.println("3> receive: " + content);
                AmqQueueProducerTest.this.time = System.currentTimeMillis();
            }
        });

        sub3.start("Judy.Test");

        long time = System.currentTimeMillis();
        Thread.sleep(180000);
        System.out.println(this.time - time);
    }

    @Test
    public void testPubReply() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");

        final NmsProducer pub = new AmqQueueFactory().createProducer(endPoint);
        NmsConsumer sub = new AmqQueueFactory().createConsumer(endPoint);

        sub.addLabel("xml");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("Receive: " + body.getContent().get("xml"));
                System.out.println("Reply To: " + header.responseSubject);
                pub.send(header.responseSubject, "xml", "You are cute", false, header.correlationID);
            }
        });

        sub.start("Judy.Test");
        pub.start();
        String result = pub.send("Judy.Test", "xml", "Judy", false, 3000, "Judy.Test.Reply", this);
        System.out.println("Get reply: " + result);
        Thread.sleep(2000);

        pub.stop();
        sub.stop();
    }

    @Override
    public boolean check(String message) {
        return true;
    }
}
