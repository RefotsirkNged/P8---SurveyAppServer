package sw806f18.server.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Group;
import sw806f18.server.model.JsonBuilder;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Survey;

@RestController
@RequestMapping(path = "researcher/groupmanager")
public class ReseacherGroupManagerResource {

    /**
     * endpoint for giving all surveys.
     *
     * @param token
     * @return
     */
    @RequestMapping(path = "/surveys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllSurveys(@CookieValue(value = "token") String token) {
        // TODO: FIX ME!!!
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            List<Survey> surveys;
            try {
                surveys = Database.getAllModules();
            } catch (SurveyException e) {
                return "{ \"error\": \"No modules found.\" }";
            }
            String  jsonGroup = "{ \"modules\": [ ";
            for (int i = 0; i < surveys.size(); i++) {
                if (i == 0) {
                    jsonGroup += surveys.get(i).getJsonObject();
                } else {
                    jsonGroup += ", " + surveys.get(i).getJsonObject();
                }
            }
            jsonGroup += "]}";
            return jsonGroup;
        }
        return "";
    }

    /**
     * Get all groups endpoint.
     *
     * @param token Login token.
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllGroups(@CookieValue(value = "token") String token) {
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
     *
     * @param name  Name.
     * @param token Login Token.
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addGroup(@RequestHeader(value = "name") String name,
                                   @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Group group;
            try {
                group = Database.addGroup(new Group(name, 0));
            } catch (AddGroupException e) {
                return ResponseEntity.ok(JsonBuilder.buildError(e.getMessage()));
            }

            return ResponseEntity.ok(JsonBuilder.buildMessage("groupid", group.getId()));
        }
        return ResponseEntity.ok(JsonBuilder.buildError("Invalid token"));
    }

    /**
     * Delete group endpoint.
     *
     * @param groupId Group ID.
     * @param token   Login Token.
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteGroup(@RequestHeader(value = "id") String groupId, // TODO: URI param instead?
                                      @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.deleteGroup(Integer.parseInt(groupId));
            } catch (DeleteGroupException e) {
                return ResponseEntity.badRequest().body(new Error(e.getMessage()));
            }

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Add group member endpoint.
     *
     * @param groupId       Group ID.
     * @param participantId User ID.
     * @param token         Token.
     */
    @RequestMapping(path = "/member", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addGroupMember(@RequestHeader(value = "groupID") int groupId, // TODO: Commandline param
                                         @RequestHeader(value = "userID") int participantId,
                                         @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.addGroupMember(new Group(groupId, "", 0),
                    new Participant(participantId, "", "", "", 0));
            } catch (AddGroupMemberException e) {
                return ResponseEntity.badRequest().body(new Error(e.getMessage()));
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Add group member endpoint.
     *
     * @param groupId Group ID.
     * @param userId  User ID.
     * @param token   Token.
     */
    @RequestMapping(path = "/member", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeGroupMember(@RequestHeader(value = "groupID") int groupId,
                                            @RequestHeader(value = "userID") int userId,
                                            @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.removeParticipantFromGroup(new Group(groupId, "", 0),
                    new Participant(userId, "", "", "", 0));
            } catch (RemoveParticipantFromGroupException e) {
                return ResponseEntity.badRequest().body(new Error(e.getMessage()));
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }
}
