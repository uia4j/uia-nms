package uia.nms.mqtt;

import org.junit.Test;

public class RabbitTopicSubscriberTest {

	@Test
	public void test() throws Exception {
		RabbitSubscriber s = new RabbitSubscriber();
		s.start("fab.dc");
		Thread.sleep(10000);
		s.stop();
	}
}
