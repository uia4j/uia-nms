NMS
===

## ActiveMQ
### Topic
Publisher
```java
NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
NmsProducer pub = new AmqTopicFactory().createProducer(endPoint);
pub.start();
pub.send(
        "NMS.AMQ.TEST", 		// name
        "data",					// label of the message
        "Hello judy", 			// message
        false);					// persistent flag
pub.stop();
```

Subscriber
```java
NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
NmsConsumer sub = new AmqTopicFactory().createConsumer(endPoint);
sub.addLabel("data");           // receive messages with label 'data'
sub.addMessageListener(new NmsMessageListener() {

    @Override
    public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
        System.out.println(body.getContent().get("data"));
    }
});

sub.start("NMS.AMQ.TEST");      // listen queue 'NMS.AMQ.TEST'.
```


### Queue
Producer
```java
NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
NmsProducer pro = new AmqQueueFactory().createProducer(endPoint);
pro.start();
pro.send(
        "NMS.AMQ.TEST", 		// name
        "data",					// label of the message
        "Hello Judy", 			// message
        false);					// persistent flag
pro.stop();
```

Consumer
```java
NmsEndPoint endPoint = new NmsEndPoint(null, null, "tcp://localhost", "61616");
NmsConsumer con = new AmqQueueFactory().createConsumer(endPoint);
con.addLabel("data");           // receive messages with label 'data'
con.addMessageListener(new NmsMessageListener() {

    @Override
    public void messageReceived(NmsConsumer sub, MessageHeader header, MessageBody body) {
        System.out.println("got:" + body.getContent().get("data"));
    }

});

con.start("NMS.AMQ.TEST");      // listen queue 'NMS.AMQ.TEST'.
```