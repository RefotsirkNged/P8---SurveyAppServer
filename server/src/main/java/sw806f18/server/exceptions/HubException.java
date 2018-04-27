package sw806f18.server.exceptions;

/**
 * Created by chrae on 05-04-2018.
 */
public class HubException extends P8Exception {
    public HubException(String message) {
        super(message);
    }

    public HubException(String message, Exception ex) {
        super(message, ex);
    }
}
