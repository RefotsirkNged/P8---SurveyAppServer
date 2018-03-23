package sw806f18.server.exceptions;

/**
 * Created by chrae on 22-03-2018.
 */
public class GetGroupMemberException extends P8Exception {
    public GetGroupMemberException(String message) {
        super(message);
    }

    public GetGroupMemberException(String message, Exception ex) {
        super(message, ex);
    }
}
