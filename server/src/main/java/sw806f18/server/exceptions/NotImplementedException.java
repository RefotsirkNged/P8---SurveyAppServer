package sw806f18.server.exceptions;

// This really should be standard Java
public class NotImplementedException extends P8Exception {

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Exception ex) {
        super(message, ex);
    }
}
