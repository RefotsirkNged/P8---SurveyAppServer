package sw806f18.server.api;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("participant")
public class Participant {
    @POST
    public void addParticipant(@HeaderParam("key") String key, @HeaderParam("email") String email, @HeaderParam("password") String password) {

    }
}
