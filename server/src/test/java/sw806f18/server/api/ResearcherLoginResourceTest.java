package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.Authentication;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(TestRunner.class)
public class ResearcherLoginResourceTest {

    @Test
    public void legalUserLogin() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.login(TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
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
            response = TestHelpers.login(TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.INVALID_EMAIL, TestHelpers.INVALID_PASSWORD);
            assertEquals(response.getResponseCode(), 200);
            JsonNode payload = TestHelpers.getJsonPayload(response);
            String error = payload.get("error").textValue();
            assertEquals("Invalid email or password!", error);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void wrongPasswordUserLogin() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.login(TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(), TestHelpers.INVALID_PASSWORD);
            assertEquals(response.getResponseCode(), 200);
            JsonNode payload = TestHelpers.getJsonPayload(response);
            String error = payload.get("error").textValue();
            assertEquals("Invalid email or password!", error);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void validUserToken() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.login(TestHelpers.RESEARCHER_LOGIN_PATH,
                TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
            assertEquals(response.getResponseCode(), 200);

            Map<String, List<String>> headerFields = response.getHeaderFields();
            List<String> strings = headerFields.get("Set-Cookie");

            Pattern compile = Pattern.compile("(?<=token=).*");
            Matcher matcher = compile.matcher(strings.get(0));
            String token = null;

            if (matcher.find()) {
                token = matcher.group();
            }

            assertTrue(Authentication.instance.decodeToken(token)
                .getClaim("userid").asInt() == TestHelpers.researcher1.getId());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
