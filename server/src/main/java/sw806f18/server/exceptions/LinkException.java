package sw806f18.server.exceptions;

/**
 * Created by Ledning on 05-04-2018.
 */
public class LinkException extends P8Exception {
    public LinkException(String message) {
        super(message);
    }

    public LinkException(String message, Exception ex) {
        super(message, ex);
    }
}
