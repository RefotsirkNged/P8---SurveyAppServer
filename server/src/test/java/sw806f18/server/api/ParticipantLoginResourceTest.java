package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class ParticipantLoginResourceTest {
    @Test
    public void legalUserLogin() throws InterruptedException {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.login(TestHelpers.PARTICIPANT_LOGIN_PATH,
                TestHelpers.participant1.getEmail(), TestHelpers.PASSWORD);
            response.connect();
            assertEquals(response.getResponseCode(), 200);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void illegalUserLogin() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.login(TestHelpers.PARTICIPANT_LOGIN_PATH,
                TestHelpers.INVALID_EMAIL, TestHelpers.INVALID_PASSWORD);
            assertEquals(response.getResponseCode(), 200);            // TODO: What status to return?
            JsonNode payload = TestHelpers.getJsonPayload(response);
            String error = payload.get("error").asText();
            assertEquals(error, "Invalid email or password!");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void wrongPasswordUserLogin() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.login(TestHelpers.PARTICIPANT_LOGIN_PATH,
                TestHelpers.participant1.getEmail(), TestHelpers.INVALID_PASSWORD);
            assertEquals(response.getResponseCode(), 200);
            JsonNode payload = TestHelpers.getJsonPayload(response);
            String error = payload.get("error").textValue();
            assertTrue(error.equals("Invalid email or password!"));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getLoginPage() throws IOException {
        String actual = TestHelpers.getHTML(TestHelpers.PARTICIPANT_LOGIN_PATH);
        String expected = "";
        boolean hasError = false;
        try {
            expected = new String(Files.readAllBytes(Paths.get("tmp/participantlogin.html")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            hasError = true;
        }
        assertFalse(hasError);
        assertEquals(actual, expected.replace("\n", ""));
    }
}
