package sw806f18.server.api;

import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.AddGroupMemberException;
import sw806f18.server.exceptions.GetAllParticipantsException;
import sw806f18.server.exceptions.GetGroupMemberException;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;

@Path("researcher/participant")
public class ResearcherParticipantResource {
    @POST
    public void inviteParticipant(@HeaderParam("token") String token,
                                  @HeaderParam("cpr") int cpr,
                                  @HeaderParam("email") String email) {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getGroupMembers(@HeaderParam("token") String token,
                                      @HeaderParam("groupID") int groupId) {
        if (!Authentication.instance.isTokenExpired(token)) {
            try {
                List<Participant> participants = Database.getGroupMembers(new Group(groupId, "", 0));
                JsonArrayBuilder builder = Json.createArrayBuilder();
                for(Participant p : participants){
                    builder.add(Json.createObjectBuilder().add("id", p.getId())
                        .add("cpr", p.getCpr()).add("firstname", p.getFirstName())
                        .add("lastname", p.getLastName()).build());
                }
                return Json.createObjectBuilder().add("members", builder.build()).build();
            } catch (GetGroupMemberException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public JsonObject getAllParticipants(@HeaderParam("token") String token) {
        if (!Authentication.instance.isTokenExpired(token)) {
            try {
                List<Participant> participants = Database.getAllParticipants();
                JsonArrayBuilder builder = Json.createArrayBuilder();
                for(Participant p : participants){
                    builder.add(Json.createObjectBuilder().add("id", p.getId())
                        .add("cpr", p.getCpr()).add("firstname", p.getFirstName())
                        .add("lastname", p.getLastName()).build());
                }
                return Json.createObjectBuilder().add("participants", builder.build()).build();
            } catch (GetAllParticipantsException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }
}
