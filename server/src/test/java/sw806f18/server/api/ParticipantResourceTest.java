package sw806f18.server.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.database.Database;
import sw806f18.server.database.RelationalDatabase;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;
import sw806f18.server.exceptions.CPRKeyNotFoundException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Participant;

import javax.json.JsonObject;
import javax.mail.MessagingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

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
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void createParticipant() throws IOException, MessagingException, InterruptedException, LoginException {
        String email = "sw806f18@gmail.com";
        String cpr = "0123456789";
        String password = "power123";
        String emailPassword = "p0wer123";
        String firstname = "test";
        String lastname = "test";

        Participant partialParticipant = new Participant(-1,  email, cpr, firstname, lastname);
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        target.path("researcher").path("participant").request().header("token", token).header("cpr", cpr).header("email", email).post(Entity.text(""));
        Thread.sleep(5000); // Wait for mail
        String key = TestHelpers.getKeyFromParticipantEmail();

        assertNotNull(key);

        Response response1 = target.path("participant").request().header("key", key).header("email", partialParticipant.getEmail()).header("password", password).post(Entity.text(""));

        Participant fullParticipant = Database.getParticipant(email,password);

        assertTrue(fullParticipant.equals(partialParticipant));
        boolean success = true;
        try{
            Database.clearInviteFromKey(key);
        }
        catch(CPRKeyNotFoundException ex){
            success = false;
        }
        assertTrue(success);
    }
}