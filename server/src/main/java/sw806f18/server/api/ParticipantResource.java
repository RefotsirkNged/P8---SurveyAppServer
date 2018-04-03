package sw806f18.server.api;

import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.Participant;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.sql.SQLException;

@Path("participant")
public class ParticipantResource {

    /**
     * Creates a participant with the given parameters.
     * @param key
     * @param email
     * @param password
     * @param firstname
     * @param lastname
     */
    @POST
    public void createParticipant(@HeaderParam("key") String key,
                                  @HeaderParam("email") String email,
                                  @HeaderParam("password") String password,
                                  @HeaderParam("firstname") String firstname,
                                  @HeaderParam("lastname") String lastname) {
        String cpr = "";
        try {
            cpr = Database.getCPRFromKey(key);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Participant participant = new Participant(-1, email, cpr, firstname, lastname);
        try {
            Database.createParticipant(participant, password);
        } catch (CreateUserException e) {
            e.printStackTrace();
        }
    }
}
