package sw806f18.server;

import static org.junit.Assert.assertTrue;

import com.auth0.jwt.interfaces.DecodedJWT;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RenewTokenTest {
    private HttpServer server;
    private WebTarget target;

    /**
     * Before.
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("config.json");
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

//    @Test
//    public void renew() {
//        Response response = TestHelpers.login(target, TestHelpers.RESEARCHER_LOGIN_PATH,
//                TestHelpers.VALID_RESEARCHER_EMAIL, TestHelpers.VALID_RESEARCHER_PASSWORD);
//        JsonObject jsonObject = TestHelpers.getPayload(response);
//        String token = jsonObject.getString("token");
//
//        // Ensure that the renewed token is newer.
//        // Should the new token be made the same second as the old one,
//        // we can't check if they always are assigned the same times.
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        response = target.path(TestHelpers.RENEW_TOKEN_PATH).request().header("token", token).get();
//        jsonObject = TestHelpers.getPayload(response);
//        String newToken = jsonObject.getString("token");
//
//        DecodedJWT token1 = Authentication.instance.decodeToken(token);
//        DecodedJWT token2 = Authentication.instance.decodeToken(newToken);
//
//        System.out.println(token1.getIssuedAt().getTime());
//        System.out.println(token2.getIssuedAt().getTime());
//
//        assertTrue(token1.getIssuedAt().getTime() < token2.getIssuedAt().getTime());
//        assertTrue(token1.getExpiresAt().getTime() < token2.getExpiresAt().getTime());
//        assertTrue(token1.getClaim("userid").asInt() == token2.getClaim("userid").asInt());
//    }
}