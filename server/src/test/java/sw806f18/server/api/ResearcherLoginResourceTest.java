package sw806f18.server.api;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.Authentication;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunner.class)
public class ResearcherLoginResourceTest {

    @Test
    public void legalUserLogin() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.RESEARCHER_LOGIN_PATH,
            TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");
        assertTrue(Authentication.instance.decodeToken(token) != null);
    }

    @Test
    public void illegalUserLogin() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.RESEARCHER_LOGIN_PATH,
            TestHelpers.INVALID_EMAIL, TestHelpers.INVALID_PASSWORD);
        assertEquals(response.getStatus(), 200);            // TODO: What status to return?
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }

    @Test
    public void wrongPasswordUserLogin() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.RESEARCHER_LOGIN_PATH,
            TestHelpers.researcher1.getEmail(), TestHelpers.INVALID_PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String error = jsonObject.getString("error");
        assertTrue(error.equals("Invalid email or password!"));
    }


    @Test
    public void validUserToken() {
        Response response = TestHelpers.login(TestListener.target, TestHelpers.RESEARCHER_LOGIN_PATH,
            TestHelpers.researcher1.getEmail(), TestHelpers.PASSWORD);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);
        String token = jsonObject.getString("token");

        boolean isValid = true;
        DecodedJWT decodedJwt = null;

        try {
            decodedJwt = Authentication.instance.decodeToken(token);
        } catch (JWTVerificationException e) {
            isValid = false;
        }

        assertTrue(isValid);
    }
}
