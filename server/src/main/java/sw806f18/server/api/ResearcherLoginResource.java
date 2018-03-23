package sw806f18.server.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sw806f18.server.Authentication;
import sw806f18.server.Database;
import sw806f18.server.exceptions.LoginException;

@Path("researcher/login")
public class ResearcherLoginResource {
    /**
     * Allows an admin to log into the system.
     * @param email The email address of the researcher.
     * @param password The password of the researcher.
     * @return A JSON string with either a login "token" or an "error" message.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject login(@HeaderParam("email") String email,
                            @HeaderParam("password") String password) {
        try {
            int userid = Database.getResearcher(email, password).getId();
            String token = Authentication.instance.getToken(userid);

            return Json.createObjectBuilder().add("token", token).build();
        } catch (LoginException e) {
            e.printStackTrace();
            return Json.createObjectBuilder().add("error", e.getMessage()).build();
        }
    }

}
