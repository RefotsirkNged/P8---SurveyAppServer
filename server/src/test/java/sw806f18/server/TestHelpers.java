package sw806f18.server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.StringReader;

public class TestHelpers {
    public final static String RESEARCHER_LOGIN_PATH = "researcher/login";
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

    public static String getResearcherLoginToken(WebTarget target)
    {
        Response response = login(target, "researcher/login", VALID_RESEARCHER_EMAIL, VALID_RESEARCHER_PASSWORD);
        JsonObject jsonObject = getPayload(response);
        return jsonObject.getString("token");
    }
}
