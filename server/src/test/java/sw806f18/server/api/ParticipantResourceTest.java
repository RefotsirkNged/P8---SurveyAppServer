package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestRunner;

import static org.junit.Assert.fail;

@RunWith(TestRunner.class)
public class ParticipantResourceTest {

    /**
     * Test for userstory 3's implementation.
     *
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
            .header("email", "sw806f18@gmail.com")
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