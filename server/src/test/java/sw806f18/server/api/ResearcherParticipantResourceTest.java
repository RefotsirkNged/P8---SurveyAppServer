package sw806f18.server.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.RelationalDatabase;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;
import sw806f18.server.exceptions.CPRKeyNotFoundException;
import sw806f18.server.model.Participant;

import javax.json.JsonObject;
import javax.mail.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ResearcherParticipantResourceTest {
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
    public void inviteParticipant() throws MessagingException, IOException, InterruptedException {
        Participant participant = new Participant(-1, "sw806f18@gmail.com", "0123456789");
        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH, TestHelpers.VALID_RESEARCHER_EMAIL, TestHelpers.VALID_RESEARCHER_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        target.path("researcher").path("participant").request().header("token", token).header("cpr", "0123456789").header("email", "sw806f18@gmail.com").post(Entity.text(""));
        Thread.sleep(5000); // Wait for mail
        String key = TestHelpers.getKeyFromParticipantEmail();

        assertNotNull(key);

        boolean success = true;
        try{
            RelationalDatabase.clearInviteFromKey(key);
        }
        catch(CPRKeyNotFoundException ex){
            success = false;
        }
        Assert.assertTrue(success);
    }
}