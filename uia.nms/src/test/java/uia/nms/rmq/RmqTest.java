package uia.nms.rmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import uia.nms.MessageBody;
import uia.nms.MessageHeader;
import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsMessageListener;
import uia.nms.NmsProducer;

public class RmqTest {

    @Test
    public void testDrop() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        factory.setConnectionTimeout(15000);
        factory.setRequestedHeartbeat(60);

        // factory.setVirtualHost("/");
        factory.setHost("10.160.1.126");
        factory.setPort(30056);
        factory.setUsername("eap");
        factory.setPassword("eap");
        Connection conn = factory.newConnection();
        for (int i = 1; i < 300; i++) {
            try {
                conn.createChannel().queueDelete(String.format("HTKS.FDC.DC.WPRBY.WPRBY%02d.S", i));
            }
            catch (Exception ex) {

            }
        }
        for (int i = 1; i <= 20; i++) {
            try {
                conn.createChannel().queueDelete(String.format("HTKS.FDC.DC.BSPN.BSPN%02d.S", i));
                conn.createChannel().queueDelete(String.format("HTKS.FDC.DC.BPVD.BPVD%02d.S", i));
                conn.createChannel().queueDelete(String.format("HTKS.FDC.DC.BPLT.BPLT%02d.S", i));
                conn.createChannel().queueDelete(String.format("HTKS.FDC.DC.BPDB.BPDB%02d.S", i));
                conn.createChannel().queueDelete(String.format("HTKS.FDC.DC.BDEV.BDEV%02d.S", i));
            }
            catch (Exception ex) {

            }
        }
    }
}
