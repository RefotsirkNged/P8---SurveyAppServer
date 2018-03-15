package sw806f18.server.api;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("researcher/participant")
public class ResearcherParticipantResource {
    @POST
    public void inviteParticipant(@HeaderParam("token") String token, @HeaderParam("cpr") int cpr, @HeaderParam("email") String email) {

    }
}
