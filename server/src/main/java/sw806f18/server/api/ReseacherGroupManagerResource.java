package sw806f18.server.api;

import java.sql.SQLException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Survey;

@Path("researcher/groupmanager")
public class ReseacherGroupManagerResource {

    /**
     * Get all groups endpoint.
     * @param token Login token.
     * @return Response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllGroups(@HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                List<Group> groups = Database.getAllGroups();
                String jsonGroup = "{ \"groups\": [ ";
                for (int i = 0; i < groups.size(); i++) {
                    if (i == 0) {
                        jsonGroup += groups.get(i).getJsonObject();
                    } else {
                        jsonGroup += ", " + groups.get(i).getJsonObject();
                    }
                }
                jsonGroup += "]}";
                return jsonGroup;

            } catch (GetGroupsException e) {
                return e.getMessage();
            }
        } else {
            return "";
        }
    }

    /**
     * Add group endpoint.
     * @param name Name.
     * @param token Login Token.
     * @return Response.
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject addGroup(@HeaderParam("name") String name,
                               @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Group group;
            try {
                group = Database.addGroup(new Group(name, 0));
            } catch (AddGroupException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
            return Json.createObjectBuilder().add("groupid", group.getId()).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    /**
     * Delete group endpoint.
     * @param groupId Group ID.
     * @param token Login Token.
     * @return Response.
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject deleteGroup(@HeaderParam("id") int groupId,
                                  @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.deleteGroup(groupId);
            } catch (DeleteGroupException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
            return Json.createObjectBuilder().add("success", 1).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    /**
     * Add group member endpoint.
     * @param groupId Group ID.
     * @param userId User ID.
     * @param token Token.
     */
    @PUT
    @Path("member")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject addGroupMember(@HeaderParam("groupID") int groupId,
                                 @HeaderParam("userID") int userId,
                                 @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.addGroupMember(new Group(groupId, "", 0),
                    new Participant(userId, "", "", "", 0));
            } catch (AddGroupMemberException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
            return Json.createObjectBuilder().add("success", 1).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    /**
     * Add group member endpoint.
     * @param groupId Group ID.
     * @param userId User ID.
     * @param token Token.
     */
    @DELETE
    @Path("member")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject removeGroupMember(@HeaderParam("groupID") int groupId,
                                 @HeaderParam("userID") int userId,
                                 @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.removeParticipantFromGroup(new Group(groupId, "", 0),
                    new Participant(userId, "", "","", 0));
            } catch (RemoveParticipantFromGroupException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
            return Json.createObjectBuilder().add("success", 1).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    /**
     * Linking a group to a survey.
     * @param surveyID
     * @param groupID
     * @param token
     * @return
     */
    @PUT
    @Path("groups")
    @Produces(MediaType.APPLICATION_JSON)
    public  JsonObject linkSurveyToGroup(@HeaderParam("surveyID") int surveyID,
                                         @HeaderParam("groupID") int groupID,
                                         @HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Survey s = new Survey();
                s.setId(surveyID);
                Group g = new Group("Dummygroup", groupID);
                Database.linkModuleToGroup(s, g);
            } catch (SurveyException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }

            return Json.createObjectBuilder().add("success", 1).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }
}
