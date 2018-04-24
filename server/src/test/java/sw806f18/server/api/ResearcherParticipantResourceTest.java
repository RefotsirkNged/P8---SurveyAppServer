package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.model.Participant;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(TestRunner.class)
public class ResearcherParticipantResourceTest {

    @Test
    public void inviteParticipant() {
        // TODO: FIX ME
        Participant participant = new Participant(
            -1,
            "sw806f18@gmail.com",
            "0123456789",
            "test",
            "test",
            -1
        );
        Map<String, String> map = new HashMap<>();
        map.put("cpr", participant.getCpr());
        map.put("email", participant.getEmail());

        try {
            HttpURLConnection connection = TestHelpers.getHttpConnection(
                TestHelpers.RESEARCHER_PARTICIPANT_PATH,
                "POST", TestHelpers.tokenResearcher1,
                map,
                null,
                null
            );
            assertEquals(200, connection.getResponseCode());
            Thread.sleep(5000); // Wait for mail
            String key = TestHelpers.getKeyFromParticipantEmail();
            assertNotNull(key);
            Database.clearInviteFromKey(key);
        } catch (IOException | MessagingException | InterruptedException | CprKeyNotFoundException e1) {
            e1.printStackTrace();
            fail();
        }
    }
}