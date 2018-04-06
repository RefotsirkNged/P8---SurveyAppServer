package sw806f18.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.Authentication;
import sw806f18.server.Configurations;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;


public class ResearcherLoginResourceTest {
    private HttpServer server;
    private WebTarget target;

    /**
     * Setup.
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");
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
        TestHelpers.resetDatabase();
        TestHelpers.populateDatabase();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void legalUserLogin() {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");
        assertTrue(Authentication.instance.decodeToken(token) != null);
    }

    @Test
    public void illegalUserLogin() {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.INVALID_EMAIL, TestHelpers.INVALID_PASSWORD);
        assertEquals(response.getStatus(), 200);            // TODO: What status to return?
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }

    @Test
    public void wrongPasswordUserLogin() {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(), TestHelpers.INVALID_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }


    @Test
    public void validUserToken() {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        boolean isValid = true;
        DecodedJWT decodedJwt = null;

        try {
            decodedJwt = Authentication.instance.decodeToken(token);
        } catch (JWTVerificationException e) {
            isValid = false;
        }

        assertTrue(isValid);
    }
}
