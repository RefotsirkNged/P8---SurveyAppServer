package sw806f18.server;

import sw806f18.server.model.Group;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TestHelpers {
    public final static String RESEARCHER_LOGIN_PATH = "researcher/login";
    public final static String RESEARCHER_GROUPMANAGER_PATH = "researcher/groupmanager";
    public final static String RENEW_TOKEN_PATH = "renewtoken";

    public final static String VALID_RESEARCHER_EMAIL = "researcher1@email.com";
    public final static String VALID_RESEARCHER_PASSWORD = "pypass";

    public final static String INVALID_RESEARCHER_EMAIL = "fake1@email.com";
    public final static String INVALID_RESEARCHER_PASSWORD = "fake";

    private TestHelpers()
    {
        // Don't instantiate me
    }

    public static JsonObject getPayload(Response response)
    {
        String content = response.readEntity(String.class);
        JsonReader jsonReader = Json.createReader(new StringReader(content));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    public static Response login(WebTarget target, String path, String email, String password)
    {
        return target.path(path).request().header("email", email).header("password", password).post(Entity.text(""));
    }

    public static Response getAllGroups(WebTarget target, String path, String token)
    {
        return target.path(path).request().header("token", token).get();
    }

    public static Response addGroup(WebTarget target, String path, String name, String token)
    {
        return target.path(path).request().header("name", name).header("token", token).put(Entity.text(""));
    }

    public static Response deleteGroup(WebTarget target, String path, int id, String token)
    {
        return target.path(path).request().header("id", id).header("token", token).delete();
    }

    public static String getResearcherLoginToken(WebTarget target)
    {
        Response response = login(target, "researcher/login", VALID_RESEARCHER_EMAIL, VALID_RESEARCHER_PASSWORD);
        JsonObject jsonObject = getPayload(response);
        return jsonObject.getString("token");
    }

    public static List<Group> testGroups(){
        List<Group> list = new ArrayList<>();
        list.add(new Group(1, "Group 1", 0));
        list.add(new Group(2, "Group 2", 0));
        list.add(new Group(3, "Group 3", 0));
        return list;
    }
}
