package sw806f18.server.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.Configurations;
import sw806f18.server.Constants;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Question;

import javax.json.JsonObject;
import javax.validation.constraints.AssertTrue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;

import static org.junit.Assert.*;

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
        MultivaluedMap<String,String> form = new MultivaluedHashMap<>();
        for (Question q : TestHelpers.survey2.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    form.add(q.getHtmlID(), "This is a test");
                    break;
                case NUMBER:
                    form.add(q.getHtmlID(), "123");
                    break;
                case DROPDOWN:
                    form.add(q.getHtmlID(), "123");
                    break;
                default:
                    break;
            }
        }



        Response response = target.path("survey").path(Integer.toString(TestHelpers.survey2.getId()))
                .request()
                .post(Entity.form(form));

        assertEquals(response.getStatus(), 200);
        assertTrue(!response.readEntity(String.class).equals(SurveyResource.getReturnHTML(Constants.hubUrl)));
    }

    @Test
    public void surveySubmittedWithoutError() throws InterruptedException {
        MultivaluedMap<String,String> form = new MultivaluedHashMap<>();
        for (Question q : TestHelpers.survey2.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    form.add(q.getHtmlID(), "This is a test");
                    break;
                case NUMBER:
                    form.add(q.getHtmlID(), "123");
                    break;
                case DROPDOWN:
                    form.add(q.getHtmlID(), "123");
                    break;
                default:
                    break;
            }
        }



        Response response = target.path("survey").path(Integer.toString(TestHelpers.survey2.getId()))
                .request()
                .post(Entity.form(form));

        assertEquals(response.getStatus(), 200);
        assertEquals(response.readEntity(String.class), SurveyResource.getReturnHTML(Constants.hubUrl));
    }
}
