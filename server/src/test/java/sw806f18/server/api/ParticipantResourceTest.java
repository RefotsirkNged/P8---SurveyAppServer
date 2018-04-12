package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.exceptions.LoginException;

import javax.json.JsonObject;
import javax.mail.MessagingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(TestRunner.class)
public class ParticipantResourceTest {

    /**
     * Test for userstory 3's implementation.
     *
     * @throws IOException
     * @throws MessagingException
     * @throws InterruptedException
     * @throws LoginException
     */
    @Test
    public void createParticipant() throws IOException, MessagingException, InterruptedException, LoginException {
        Response response = TestHelpers.login(TestListener.target,
            TestHelpers.RESEARCHER_LOGIN_PATH,
            TestHelpers.researcher1.getEmail(),
            TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        TestListener.target.path("researcher").path("participant").request()
            .header("token", token)
            .header("cpr", TestHelpers.participantCreate.getCpr())
            .header("email", TestHelpers.participantCreate.getEmail())
            .post(Entity.text(""));
        Thread.sleep(5000); // Wait for mail
        String key = TestHelpers.getKeyFromParticipantEmail();
        assertNotNull(key);
        Response response1 = TestListener.target.path("participant")
            .request().header("key", key)
            .header("email", TestHelpers.participantCreate.getEmail())
            .header("password", TestHelpers.PASSWORD).post(Entity.text(""));
        assertEquals(204, response1.getStatus());
        boolean success = true;
        try {
            Database.clearInviteFromKey(key);
        } catch (CprKeyNotFoundException ex) {
            success = false;
        }
        org.junit.Assert.assertTrue(success);
    }
}