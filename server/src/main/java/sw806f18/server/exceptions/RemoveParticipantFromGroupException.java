package sw806f18.server.exceptions;

/**
 * Created by chrae on 22-03-2018.
 */
public class RemoveParticipantFromGroupException extends P8Exception{
    public RemoveParticipantFromGroupException(String message) {
        super(message);
    }

    public RemoveParticipantFromGroupException(String message, Exception ex) {
        super(message, ex);
    }
}
