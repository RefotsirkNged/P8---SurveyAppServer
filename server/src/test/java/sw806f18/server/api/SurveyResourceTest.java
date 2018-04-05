package sw806f18.server.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.Configurations;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by augustkorvell on 05/04/2018.
 */
public class SurveyResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");
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
        TestHelpers.populateDatabase();
    }

    @After
    public void tearDown() throws Exception {
        TestHelpers.resetDatabase();
        server.shutdown();
    }

    @Test
    public void surveySubmittedWithError() {

    }

    @Test
    public void surveySubmittedWithoutError() {

    }
}
