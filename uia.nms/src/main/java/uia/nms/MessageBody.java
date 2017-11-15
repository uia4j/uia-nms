package uia.nms;

import java.util.HashMap;
import java.util.Map;

public class MessageBody {

    private final HashMap<String, String> content;

    public MessageBody() {
        this.content = new HashMap<String, String>();
    }

    public Map<String, String> getContent() {
        return this.content;
    }

    public void put(String label, String message) {
        this.content.put(label, message);
    }
}
