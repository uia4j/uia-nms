package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMatching;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class AmqFailoverTest implements NmsMatching {

    @Test
    public void test() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(
                "failover",
                null,
                "tcp://localhost:61616,tcp://localhost:62616,tcp://localhost:63616",
                null);

        final NmsConsumer sub = new AmqQueueFactory().createConsumer(endPoint);
        sub.addLabel("value");
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("got it");
                System.out.println(body.getContent());
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(header.responseSubject);
                sub.createProducer().send(header.responseSubject, null, "good job", false);
            }
        });
        sub.start("HTKS.FAILOVER.S");
        Thread.sleep(1000);

        final NmsProducer pub = new AmqQueueFactory().createProducer(endPoint);
        pub.start();
        String result = pub.send(
                "HTKS.FAILOVER.S",
                "value",
                "xxxx",
                false,
                3000,
                "HTKS.FAILOVER.R",
                this);
        System.out.println("Get reply: " + result);
        pub.stop();

        Thread.sleep(1000);
        sub.stop();
    }

    @Override
    public boolean check(String message) {
        return true;
    }
}
