package sw806f18.server;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import java.util.Date;

public class ResearcherLoginResourceTest {
    private HttpServer server;
    private WebTarget target;

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

    @Test
    public void legalUserLogin()
    {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.VALID_RESEARCHER_EMAIL, TestHelpers.VALID_RESEARCHER_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");
        assertFalse(token.isEmpty());
    }

    @Test
    public void illegalUserLogin()
    {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.INVALID_RESEARCHER_EMAIL, TestHelpers.INVALID_RESEARCHER_PASSWORD);
        assertEquals(response.getStatus(), 200);            // TODO: What status to return?
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }

    @Test
    public void wrongPasswordUserLogin()
    {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.VALID_RESEARCHER_EMAIL, TestHelpers.INVALID_RESEARCHER_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }


    @Test
    public void validUserToken()
    {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.VALID_RESEARCHER_EMAIL, TestHelpers.VALID_RESEARCHER_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        boolean isValid = true;
        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = Authentication.instance.decodeToken(token);
        } catch (JWTVerificationException e)
        {
            isValid = false;
        }

        assertTrue(isValid);

        long expirationTime = 0;

        try {
            expirationTime = decodedJWT.getExpiresAt().getTime();
        } catch (Exception e)
        {
            assertFalse(true);
        }

        assertTrue(expirationTime > new Date().getTime());
    }
}