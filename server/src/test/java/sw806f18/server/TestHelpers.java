package sw806f18.server;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import sw806f18.server.model.Group;

public class TestHelpers {
    public static final String RESEARCHER_LOGIN_PATH = "researcher/login";
    public static final String RESEARCHER_GROUPMANAGER_PATH = "researcher/groupmanager";
    public static final String RENEW_TOKEN_PATH = "renewtoken";

    public static final String VALID_RESEARCHER_EMAIL = "researcher1@email.com";
    public static final String VALID_RESEARCHER_PASSWORD = "pypass";

    public static final String INVALID_RESEARCHER_EMAIL = "fake1@email.com";
    public static final String INVALID_RESEARCHER_PASSWORD = "fake";

    private TestHelpers() {
        // Don't instantiate me
    }

    /**
     * Get payload.
     * @param response Response.
     * @return Payload.
     */
    public static JsonObject getPayload(Response response) {
        String content = response.readEntity(String.class);
        JsonReader jsonReader = Json.createReader(new StringReader(content));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    /**
     * Login function.
     * @param target Web target.
     * @param path Endpoint.
     * @param email Email.
     * @param password Password.
     * @return Response.
     */
    public static Response login(WebTarget target, String path, String email, String password) {
        return target.path(path).request().header("email", email)
                .header("password", password).post(Entity.text(""));
    }

    /**
     * Get all groups.
     * @param target Web target.
     * @param path Endpoint.
     * @param token Token.
     * @return All groups.
     */
    public static Response getAllGroups(WebTarget target, String path, String token) {
        return target.path(path).request().header("token", token).get();
    }

    /**
     * Add group to database.
     * @param target Web target.
     * @param path Endpoint.
     * @param name Group name
     * @param token Token.
     * @return
     */
    public static Response addGroup(WebTarget target, String path, String name, String token) {
        return target.path(path).request().header("name", name)
                .header("token", token).put(Entity.text(""));
    }

    /**
     * Delete group in the database.
     * @param target Web target.
     * @param path Endpoint.
     * @param id Group id.
     * @param token Token.
     * @return Response.
     */
    public static Response deleteGroup(WebTarget target, String path, int id, String token) {
        return target.path(path).request().header("id", id).header("token", token).delete();
    }

    /**
     * Get the test researcher's login token.
     * @param target Web target.
     * @return Login token.
     */
    public static String getResearcherLoginToken(WebTarget target) {
        Response response = login(target, "researcher/login",
                VALID_RESEARCHER_EMAIL, VALID_RESEARCHER_PASSWORD);
        JsonObject jsonObject = getPayload(response);
        return jsonObject.getString("token");
    }

    /**
     * Fetch testing groups.
     * @return Test groups.
     */
    public static List<Group> testGroups() {
        List<Group> list = new ArrayList<>();
        list.add(new Group(1, "Group 1", 0));
        list.add(new Group(2, "Group 2", 0));
        list.add(new Group(3, "Group 3", 0));
        return list;
    }
}
