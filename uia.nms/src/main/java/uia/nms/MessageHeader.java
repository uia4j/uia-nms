package uia.nms;

public class MessageHeader {

    public final String requestSubject;

    public final String responseSubject;

    public final String correlationID;

    public MessageHeader(String requestSubject, String responseSubject, String correlationID) {
        this.requestSubject = requestSubject;
        this.responseSubject = responseSubject;
        this.correlationID = correlationID;
    }

    @Override
    public String toString() {
        return this.correlationID;
    }
}
