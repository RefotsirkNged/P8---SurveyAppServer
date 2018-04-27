package sw806f18.server.exceptions;

public class CprKeyNotFoundException extends P8Exception {
    public CprKeyNotFoundException(String message) {
        super(message);
    }

    public CprKeyNotFoundException(String message, Exception ex) {
        super(message, ex);
    }
}
