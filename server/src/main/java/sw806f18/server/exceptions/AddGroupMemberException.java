package sw806f18.server.exceptions;

/**
 * Created by chrae on 22-03-2018.
 */
public class AddGroupMemberException extends P8Exception{
    public AddGroupMemberException(String message) {
        super(message);
    }

    public AddGroupMemberException(String message, Exception ex) {
        super(message, ex);
    }
}
