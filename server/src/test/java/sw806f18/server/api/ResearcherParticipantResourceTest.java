package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.model.Participant;

import javax.json.JsonObject;
import javax.mail.MessagingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class ResearcherParticipantResourceTest {

    @Test
    public void inviteParticipant() throws MessagingException, IOException, InterruptedException {
        Participant participant = new Participant(-1, "sw806f18@gmail.com", "0123456789", "test", "test", -1);
        Response response =
            TestHelpers.login(TestListener.target,
                TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(),
                TestHelpers.PASSWORD);

        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        TestListener.target.path("researcher").path("participant").request()
            .header("token", token)
            .header("cpr", "0123456789")
            .header("email", "sw806f18@gmail.com").post(Entity.text(""));
        Thread.sleep(5000); // Wait for mail
        String key = TestHelpers.getKeyFromParticipantEmail();

        assertNotNull(key);

        boolean success = true;
        try {
            Database.clearInviteFromKey(key);
        } catch (CprKeyNotFoundException ex) {
            success = false;
        }
        assertTrue(success);
    }
}