package sw806f18.server.api;

import sw806f18.server.Database;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.Researcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by chrae on 06-03-2018.
 */
@Path("/researcher")
public class ResearcherResource {
    /**
     * Method adding researcher
     *
     * @return String with status of creation
     */
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String addResearcher(@HeaderParam("email") String email,
                                @HeaderParam("password") String password,
                                @HeaderParam("phone") String phone) {

        try {
            Database.createResearcher(new Researcher(email, phone), password);
        } catch (CreateUserException e) {
            e.printStackTrace();
            return "Fail";
        }

        return "SUCCESS!";
    }
}
