package sw806f18.server.exceptions;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class DeleteUserException extends P8Exception {
    public DeleteUserException(String message) {
        super(message);
    }

    public DeleteUserException(String message, Exception ex) {
        super(message, ex);
    }
}
