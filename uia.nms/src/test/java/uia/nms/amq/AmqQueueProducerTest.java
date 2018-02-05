package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class AmqQueueProducerTest {

    @Test
    public void testPubReply1() throws Exception {
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
        String result = pub.send("Judy.Test", "xml", "Judy", false, 3000, "Judy.Test.Reply");
        System.out.println("Get reply: " + result);
        Thread.sleep(2000);

        pub.stop();
        sub.stop();
    }

    @Test
    public void testPubReply2() throws Exception {
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
        String result = pub.send("Judy.Test", "xml", "Judy", false, 3000);
        System.out.println("Get reply: " + result);
        Thread.sleep(2000);

        pub.stop();
        sub.stop();
    }

    @Test
    public void testPub() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://10.160.1.53", "61616");
        final NmsProducer pub = new AmqQueueFactory().createProducer(endPoint);
        pub.start();
        pub.send("HTKS.FME.DC.PLAN.S", "value", "TEST", false, 2000, "HTKS.FME.DC.PLAN.R");
        pub.stop();
    }
}
