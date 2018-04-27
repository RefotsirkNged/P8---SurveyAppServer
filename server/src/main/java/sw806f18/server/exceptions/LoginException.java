package sw806f18.server.exceptions;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class LoginException extends P8Exception {
    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Exception ex) {
        super(message, ex);
    }
}
