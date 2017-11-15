package uia.nms;

public class NmsException extends Exception {

    private static final long serialVersionUID = -5417616927725462467L;

    public NmsException(String message) {
        super(message);
    }

    public NmsException(String message, Exception ex) {
        super(message, ex);
    }
}
