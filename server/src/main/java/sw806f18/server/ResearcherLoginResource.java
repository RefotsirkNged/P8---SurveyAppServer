package sw806f18.server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

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
    public JsonObject login(@HeaderParam("email") String email, @HeaderParam("password") String password)
    {
        try {
            Connection connection = Database.createConnection();
            int userid = Database.getUser(connection, email, password);

            if(userid == -1 || !Database.isResearcher(connection, userid))
            {
                return Json.createObjectBuilder().add("error", "Invalid email or password!").build();
            }

            String token = Authentication.instance.getToken(userid); // Database.createSessionToken(connection, userid);
            Database.closeConnection(connection);

            return Json.createObjectBuilder().add("token", token).build();
        } catch (Exception e) {
            return Json.createObjectBuilder().add("error", "Exception: " + e.getMessage()).build();
        }
    }

}
