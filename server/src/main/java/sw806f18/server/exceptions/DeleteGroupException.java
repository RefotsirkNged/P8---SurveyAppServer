package sw806f18.server.exceptions;

/**
 * Created by chrae on 14-03-2018.
 */
public class DeleteGroupException extends P8Exception {
    public DeleteGroupException(String message) {
        super(message);
    }

    public DeleteGroupException(String message, Exception ex) {
        super(message, ex);
    }
}
