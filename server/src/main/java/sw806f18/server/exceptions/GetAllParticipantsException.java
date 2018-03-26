package sw806f18.server.exceptions;

/**
 * Created by chrae on 23-03-2018.
 */
public class GetAllParticipantsException extends P8Exception {

    public GetAllParticipantsException(String message) {
        super(message);
    }

    public GetAllParticipantsException(String message, Exception ex) {
        super(message, ex);
    }
}
