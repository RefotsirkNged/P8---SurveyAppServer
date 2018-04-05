package sw806f18.server.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.auth0.jwt.interfaces.DecodedJWT;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Hub;
import sw806f18.server.model.Participant;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


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

    /**
     * Get login page HTML.
     * @return HTML for hub.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream getLoginPage() {
        StringBuilder sb = new StringBuilder();
        String token = login("test1@testesen.dk", "power123").getString("token");



        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
