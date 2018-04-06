package sw806f18.server.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.GetModulesByUserException;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.model.*;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Path("participant/hub")
public class ParticipantHubResource {
    /**
     * GET method for getting modules by user ID.
     *
     * @param token
     * @return Modules
     */
    @GET
    @Path("/modules")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getModules(@HeaderParam("token") String token) {
        DecodedJWT decodedJWT = Authentication.instance.decodeToken(token);
        int userId = decodedJWT.getClaim("userid").asInt();
        List<Survey> modules = new ArrayList<>();

        try {
            modules = Database.getModulesByUser(userId);
        } catch (GetModulesByUserException e) {
            return Json.createObjectBuilder().add("error", e.getMessage()).build();
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (Survey m : modules) {
            arrayBuilder.add(Json.createObjectBuilder().add("id", m.getId())
                    .add("title", m.getTitle()).add("description", m.getDescription()).build());
        }
        return builder.add("modules", arrayBuilder.build()).build();
    }

    /**
     * Get hub HTML.
     *
     * @param token Token.
     * @return HTML for hub.
     */
    @GET
    @Path("/{token}")
    @Produces(MediaType.TEXT_HTML)
    public InputStream getHub(@PathParam("token") String token) {
        DecodedJWT decodedJWT = Authentication.instance.decodeToken(token);
        int userId = decodedJWT.getClaim("userid").asInt();
        Hub hub;

        try {
            hub = Hub.buildHub(userId);
            return new ByteArrayInputStream(hub.getHTML().getBytes(StandardCharsets.UTF_8));
        } catch (HubException e) {
            String error = "System error. Contact sytem administrator.";
            return new ByteArrayInputStream(error.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
