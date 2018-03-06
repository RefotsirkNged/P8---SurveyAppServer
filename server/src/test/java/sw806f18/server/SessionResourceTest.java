package sw806f18.server;

import org.glassfish.grizzly.http.server.HttpServer;
// import org.glassfish.grizzly.http.server.Response;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import javax.json.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.StringReader;

public class SessionResourceTest {
    private HttpServer server;
    private WebTarget target;

    private final String validEmail = "researcher1@email.com";
    private final String validPassword = "pypass";

    private final String invalidEmail = "fake1@email.com";
    private final String invalidPassword = "pypass";

    private final String malformedEmail = "xxxemail.com";
    private final String malformedPassword = "pypass";

    @Before
    public void setUp() throws Exception
    {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception
    {
        server.shutdown();
    }

    private JsonObject getPayload(Response response)
    {
        String content = response.readEntity(String.class);
        JsonReader jsonReader = Json.createReader(new StringReader(content));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    private Response login(String email, String password)
    {
        target.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, email);
        target.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password);
        return target.path("session").request().get();
    }

    @Test
    public void legalUserLogin()
    {
        Response response = login(validEmail, validPassword);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = getPayload(response);
        String token = jsonObject.getString("token");
        assertFalse(token.isEmpty());
    }

    @Test
    public void illegalUserLogin()
    {
        Response response = login(invalidEmail, invalidPassword);
        assertEquals(response.getStatus(), 200);            // TODO: What status to return?
        JsonObject jsonObject = getPayload(response);
        String token = jsonObject.getString("token");
        assertTrue(token.isEmpty());
    }

    @Test
    public void malformedUserLogin()
    {
        Response response = login(malformedEmail, malformedPassword);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = getPayload(response);
        String token = jsonObject.getString("token");
        assertTrue(false);
    }

    @Test
    public void acceptValidateToken()
    {
        Response response = login(validEmail, validPassword);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = getPayload(response);
        String token = jsonObject.getString("token");

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("token", token);
        JsonObject otherJsonObject = jsonObjectBuilder.build();

        response = target.request().post(Entity.json(otherJsonObject));
        assertEquals(response.getStatus(), 200);
        JsonObject reponseJsonObject = response.readEntity(JsonObject.class);
        assertEquals(reponseJsonObject.getBoolean("isvalid"), true);
    }

    @Test
    public void declineValidateToken()
    {
        // TODO: Implement me when we know more about tokens
        assertTrue(false);
    }

    @Test
    public void logoutTest()
    {
        // TODO: Implement when we know more about the delete method
        assertTrue(false);
    }
}
