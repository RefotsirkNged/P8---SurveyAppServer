package sw806f18.server.api;
import sw806f18.server.Authentication;
import sw806f18.server.Database;
import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.DeleteGroupException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.model.Group;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("researcher/groupmanager")
public class ReseacherGroupManagerResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllGroups(@HeaderParam("token") String token)
    {
        if(!Authentication.instance.isTokenExpired(token)){
            try {
                List<Group> groups = Database.getAllGroups();
                String jsonGroup = "{ \"groups\": [ ";
                for(int i = 0; i < groups.size(); i++){
                    if(i == 0)
                        jsonGroup    += groups.get(i).getJsonObject();
                    else
                        jsonGroup += ", "+ groups.get(i).getJsonObject();
                }
                jsonGroup += "]}";
                return jsonGroup;

            } catch (GetGroupsException e) {
                return e.getMessage();
            }
        }
        else{
            return "";
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject addGroup(@HeaderParam("name") String name, @HeaderParam("token") String token){
        if(!Authentication.instance.isTokenExpired(token)) {
            int id = 0;
            try {
                id = Database.addGroup(name);
            } catch (AddGroupException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
            return Json.createObjectBuilder().add("success", id).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("member")
    public String addGroupMember(@HeaderParam("groupID") int groupID, @HeaderParam("userID") int userID){
        return "";
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject deleteGroup(@HeaderParam("id") int groupID, @HeaderParam("token") String token){
        if(!Authentication.instance.isTokenExpired(token)) {
            try {
                Database.deleteGroup(groupID);
            } catch (DeleteGroupException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
            return Json.createObjectBuilder().add("success", 1).build();
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }
}
