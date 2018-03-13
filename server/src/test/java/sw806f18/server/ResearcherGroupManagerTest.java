package sw806f18.server;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertTrue;

public class ResearcherGroupManagerTest {
    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception
    {
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

    @Test
    public void addGroupMember(){
        assertTrue(false);
    }

    @Test
    public  void addGroup(){
        assertTrue(false);
    }

    @Test
    public  void removeGroup(){
        assertTrue(false);
    }

    @Test
    public void removeGrooupMember(){
        assertTrue(false);
    }

    @Test
    public void getAllGroups(){
        assertTrue(false);
    }

    @Test
    public void findUserByName(){
        assertTrue(false);
    }



}
