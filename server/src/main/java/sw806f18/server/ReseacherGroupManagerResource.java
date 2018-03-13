package sw806f18.server;
import sw806f18.server.Authentication;
import sw806f18.server.Database;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.crypto.Data;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Path("researcher/groupmanager")
public class ReseacherGroupManagerResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String GetAllGroups(String x)
    {
        JsonReader jsonReader = Json.createReader(new StringReader(x));
        JsonObject  jsonObject = jsonReader.readObject();
        String token  = jsonObject.getString("token");

        if(Authentication.instance.isTokenExpired(token)){
            try {
                Connection connection = Database.createConnection();
                List<group> groups = Database.getAllGroups(connection);
                Database.closeConnection(connection);
                String jsonGroup = "{ \"group\": { ";
                for(int i = 0; i < groups.size(); i++){
                    if(i == 0)
                        jsonGroup += groups.get(i).getJsonObject();
                    else
                        jsonGroup += ", "+ groups.get(i).getJsonObject();
                }
                jsonGroup += "}}";
                
                return jsonGroup;

            } catch (SQLException e) {
                return e.getMessage();
            } catch (ClassNotFoundException e) {
                return e.getMessage();
            }
        }
        else{
            return "";
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String addGroupMember(@HeaderParam("groupID") int groupID, @HeaderParam("userID") int userID){
        return "";
    }
}
