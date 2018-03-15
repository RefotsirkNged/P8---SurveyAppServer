package sw806f18.server.exceptions;

public class CreateInviteException extends P8Exception {
    public CreateInviteException(String message) {
        super(message);
    }

    public CreateInviteException(String message, Exception ex) {
        super(message, ex);
    }
}
