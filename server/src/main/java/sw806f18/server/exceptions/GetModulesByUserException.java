package sw806f18.server.exceptions;

/**
 * Created by chrae on 05-04-2018.
 */
public class GetModulesByUserException  extends P8Exception {
    public GetModulesByUserException(String message) {
        super(message);
    }

    public GetModulesByUserException(String message, Exception ex) {
        super(message, ex);
    }
}
