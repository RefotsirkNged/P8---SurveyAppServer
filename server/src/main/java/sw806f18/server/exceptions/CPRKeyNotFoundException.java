package sw806f18.server.exceptions;

public class CPRKeyNotFoundException extends P8Exception {
    public CPRKeyNotFoundException(String message) {
        super(message);
    }

    public CPRKeyNotFoundException(String message, Exception ex) {
        super(message, ex);
    }
}
