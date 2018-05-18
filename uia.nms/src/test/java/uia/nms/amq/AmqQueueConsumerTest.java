package uia.nms.amq;

import org.junit.Test;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;

public class AmqQueueConsumerTest extends AbstractTest {

    @Test
    public void testComsumer() throws Exception {
        NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://10.160.1.51", "61616");

        NmsConsumer sub = new AmqQueueFactory().createConsumer(endPoint);
        sub.addMessageListener(new NmsMessageListener() {

            @Override
            public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
                System.out.println("Receive: " + body.getContent());
            }
        });
        sub.start("UIA.NMS.TEST");

        pressToContinue();

        sub.stop();
    }
}
