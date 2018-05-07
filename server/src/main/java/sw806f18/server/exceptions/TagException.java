package sw806f18.server.exceptions;

public class TagException extends P8Exception {
    /**
     * Exception thrown when a problem related to tags is raised.
     * @param message A message to be included with the exception.
     */
    public TagException(String message) {
        super(message);
    }

    /**
     * Exception thrown when a problem related to tags is raised.
     * @param message A message to be included with the exception.
     * @param ex Thrown exception to be nested into the exception stack trace.
     */
    public TagException(String message, Exception ex) {
        super(message, ex);
    }
}
