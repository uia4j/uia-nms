package uia.nms.mqtt;

import org.junit.Test;

public class MqttTopicPublisherTest {

	@Test 
	public void test() throws Exception {
		MqttTopicPublisher p = new MqttTopicPublisher();
		p.start();
		p.send("fab.device", "abc", "good job", false);
		p.stop();
	}
}
