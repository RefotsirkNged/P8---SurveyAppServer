package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;

import javax.mail.MessagingException;
import javax.validation.constraints.AssertTrue;
import java.io.IOException;
import java.net.HttpURLConnection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(TestRunner.class)
public class ParticipantResourceTest {
    @Test
    public void createParticipant() throws IOException, InterruptedException, MessagingException {
        HttpURLConnection response1 = TestHelpers.inviteParticipant(TestHelpers.RESEARCHER_PARTICIPANT_PATH,
                TestHelpers.participantCreate.getCpr(),
                "sw806f18@gmail.com",
                TestHelpers.tokenResearcher1);
        Thread.sleep(5000); // Wait for mail
        assertEquals(200, response1.getResponseCode());
        String key = TestHelpers.getKeyFromParticipantEmail();
        assertNotNull(key);
        HttpURLConnection response2 = TestHelpers.createParticipant(TestHelpers.PARTICIPANT_PATH, key,
                TestHelpers.participantCreate.getEmail(), TestHelpers.PASSWORD,
                TestHelpers.participantCreate.getFirstName(), TestHelpers.participantCreate.getLastName());
        assertEquals(200, response1.getResponseCode());
    }
}