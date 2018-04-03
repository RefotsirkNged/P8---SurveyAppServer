package sw806f18.server.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Participant;


@Path("participant/login")
public class ParticipantLoginResource {
    /**
     * POST method for performing a login as a participant.
     * @param email
     * @param password
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject login(@HeaderParam("email") String email, @HeaderParam("password") String password) {
        try {
            Participant participant = Database.getParticipant(email, password);
            String token = Authentication.instance.getToken(participant.getId());

            return Json.createObjectBuilder().add("token", token).build();
        } catch (LoginException e) {
            e.printStackTrace();
            return Json.createObjectBuilder().add("error", e.getMessage()).build();
        }
    }
}
