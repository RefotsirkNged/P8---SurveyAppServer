package sw806f18.server.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.exceptions.SurveyException;
import sw806f18.server.model.Hub;
import sw806f18.server.model.Survey;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("group/{id}")
public class GroupResource {
    /**
     * Get Surveys.
     *
     * @param token Token.
     * @return Surveys Metadata
     */
    @GET
    @Path("/surveys")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSurveys(@PathParam("id") int id,
                             @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            List<Survey> modules;

            try {
                modules = Database.getGroupLinks(id);
            } catch (SurveyException e) {
                return "";
            }

            String jsonGroup = "{ \"modules\": [ ";
            for (int i = 0; i < modules.size(); i++) {
                if (i == 0) {
                    jsonGroup += modules.get(i).getJsonObject();
                } else {
                    jsonGroup += ", " + modules.get(i).getJsonObject();
                }
            }
            jsonGroup += "]}";
            return jsonGroup;
        }

        return "";
    }

    /**
     * Delete link.
     *
     * @param token    Token.
     * @param id       ID.
     * @param moduleId module
     * @return Surveys Metadata
     */
    @POST
    @Path("/link/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteLink(@PathParam("id") int id,
                             @HeaderParam("module") int moduleId,
                             @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.removeGroupLink(id, moduleId);
                return "{\"200\": \"success\"}";
            } catch (SurveyException e) {
                return "{\"error\": \"No such link\"}";
            }
        }

        return "{\"error\": \"Invalid token\"}";
    }

    /**
     * Linking a group to a survey.
     * @param id
     * @param moduleId
     * @param token
     * @return
     */
    @PUT
    @Path("/link/add")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject linkSurveyToGroup(@PathParam("id") int id,
                                        @HeaderParam("module") int moduleId,
                                        @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.linkModuleToGroup(moduleId, id);
            } catch (SurveyException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            } catch (P8Exception e) {
                e.printStackTrace();
            }

            return Json.createObjectBuilder().add("success", 1).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }
}
