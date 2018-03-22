package sw806f18.server.exceptions;

/**
 * Created by augustkorvell on 22/03/2018.
 */
public class SurveyException extends P8Exception {
    public SurveyException(String message) {
        super(message);
    }

    public SurveyException(String message, Exception ex) {
        super(message, ex);
    }
}
