package sw806f18.server.exceptions;

/**
 * Created by chrae on 14-03-2018.
 */
public class AddGroupException extends P8Exception {
    public AddGroupException(String message) {
        super(message);
    }

    public AddGroupException(String message, Exception ex) {
        super(message, ex);
    }
}
