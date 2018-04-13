package sw806f18.server.exceptions;

public class AnswerException extends P8Exception {
    public AnswerException(String message) {
        super(message);
    }

    public AnswerException(String message, Exception ex) {
        super(message, ex);
    }
}
