package sw806f18.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(TestRunner.class)
public class ParticipantLoginResourceTest {
    @Test
    public void legalUserLogin() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.PARTICIPANT_LOGIN_PATH,
            TestHelpers.participant1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");
        assertTrue(Authentication.instance.decodeToken(token) != null);
    }

    @Test
    public void illegalUserLogin() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.PARTICIPANT_LOGIN_PATH,
            TestHelpers.INVALID_EMAIL, TestHelpers.INVALID_PASSWORD);
        assertEquals(response.getStatus(), 200);            // TODO: What status to return?
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }

    @Test
    public void wrongPasswordUserLogin() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.PARTICIPANT_LOGIN_PATH,
            TestHelpers.participant1.getEmail(), TestHelpers.INVALID_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }

    @Test
    public void getLoginPage() {
        String actual = TestHelpers.getHTML(TestListener.target, TestHelpers.PARTICIPANT_LOGIN_PATH);
        String expected = "";
        boolean hasError = false;
        try {
            expected = new String(Files.readAllBytes(Paths.get("tmp/participantlogin.html")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            hasError = true;
        }
        assertFalse(hasError);
        assertTrue(actual.equals(expected));
    }
}
