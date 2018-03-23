package sw806f18.server.api;

import java.io.IOException;

import javax.json.JsonObject;
import javax.mail.MessagingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw806f18.server.Configurations;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.exceptions.LoginException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ParticipantResourceTest {
    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
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
        Configurations.instance = new Configurations("test-config.json");
        TestHelpers.resetDatabase();
        TestHelpers.populateDatabase();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Test for userstory 3's implementation.
     * @throws IOException
     * @throws MessagingException
     * @throws InterruptedException
     * @throws LoginException
     */
    @Test
    public void createParticipant() throws IOException, MessagingException, InterruptedException, LoginException {
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        target.path("researcher").path("participant").request().header("token", token).header("cpr", TestHelpers.participantCreate.getCpr()).header("email", TestHelpers.participantCreate.getEmail()).post(Entity.text(""));
        Thread.sleep(5000); // Wait for mail
        String key = TestHelpers.getKeyFromParticipantEmail();
        assertNotNull(key);
        Response response1 = target.path("participant").request().header("key", key).header("email", TestHelpers.participantCreate.getEmail()).header("password", TestHelpers.PASSWORD).post(Entity.text(""));
        assertEquals(response1.getStatus(), 200);
      //  assertTrue(fullParticipant.equals(partialParticipant));
        boolean success = true;
        try {
            Database.clearInviteFromKey(key);
        } catch (CprKeyNotFoundException ex) {
            success = false;
        }
        org.junit.Assert.assertTrue(success);
    }
}