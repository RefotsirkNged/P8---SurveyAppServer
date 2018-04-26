package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.Authentication;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;

import javax.mail.MessagingException;
import javax.validation.constraints.AssertTrue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(TestRunner.class)
public class ParticipantResourceTest {
    @Test
    public void createParticipant() throws IOException, InterruptedException, MessagingException {

        HttpURLConnection response = TestHelpers.createParticipant(TestHelpers.PARTICIPANT_PATH,
                TestHelpers.participantCreate.getCpr(),
                TestHelpers.participantCreate.getEmail(),
                TestHelpers.participantCreate.getFirstName(), TestHelpers.participantCreate.getLastName());
        assertEquals(200, response.getResponseCode());
        HttpURLConnection response1 =  TestHelpers.login(TestHelpers.PARTICIPANT_LOGIN_PATH,
                TestHelpers.participantCreate.getEmail(), TestHelpers.participantCreate.getCpr());
        assertEquals(200, response1.getResponseCode());
        Map<String, List<String>> headerFields = response1.getHeaderFields();
        List<String> strings = headerFields.get("Set-Cookie");

        Pattern compile = Pattern.compile("(?<=token=).*");
        Matcher matcher = compile.matcher(strings.get(0));

        if (!matcher.find()) {
            fail();
        }

    }
}