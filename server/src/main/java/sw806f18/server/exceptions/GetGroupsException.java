package sw806f18.server.exceptions;

/**
 * Created by chrae on 14-03-2018.
 */
public class GetGroupsException extends P8Exception {
    public GetGroupsException(String message){
        super(message);
    }

    public GetGroupsException(String message, Exception ex){
        super(message, ex);
    }
}
