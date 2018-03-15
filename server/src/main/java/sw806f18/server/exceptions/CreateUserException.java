package sw806f18.server.exceptions;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class CreateUserException extends P8Exception {
    public CreateUserException(String message) {
        super(message);
    }

    public CreateUserException(String message, Exception ex) {
        super(message, ex);
    }
}
