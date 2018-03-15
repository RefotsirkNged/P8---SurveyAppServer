package sw806f18.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.DeleteGroupException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.model.Group;

public class ResearcherGroupManagerTest {
    private HttpServer server;
    private WebTarget target;
    private String token;

    /**
     * Setup.
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("config.json");
        token = Authentication.instance.getToken(1);
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

    @Test
    public void addGroupMember() {
        assertTrue(false);
    }

    @Test
    public void addGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            Response response = TestHelpers.addGroup(target,
                    TestHelpers.RESEARCHER_GROUPMANAGER_PATH, group.getName(), token);
            assertEquals(response.getStatus(), 200);
            JsonObject jsonObject = TestHelpers.getPayload(response);
            group.setId(jsonObject.getInt("success"));
            List<Group> groups = Database.getAllGroups();
            assertTrue(groups.contains(group));
            Database.deleteGroup(group.getId());
        } catch (GetGroupsException e) {
            e.printStackTrace();
        } catch (DeleteGroupException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(Database.addGroup(group.getName()));
            TestHelpers.deleteGroup(target, TestHelpers.RESEARCHER_GROUPMANAGER_PATH,
                    group.getId(), token);
            List<Group> groups = Database.getAllGroups();
            assertFalse(groups.contains(group));
        } catch (GetGroupsException e) {
            e.printStackTrace();
        } catch (AddGroupException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeGroupMember() {
        assertTrue(false);
    }

    @Test
    public void getAllGroups() {
        List<Group> expected = TestHelpers.testGroups();

        Response response = TestHelpers.getAllGroups(target,
                TestHelpers.RESEARCHER_GROUPMANAGER_PATH, token);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);

        JsonArray jsonArray = jsonObject.getJsonArray("groups");

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject element = jsonArray.get(i).asJsonObject();
            assertTrue(element.getString("name").equals(expected.get(i).getName()));
        }
    }

    @Test
    public void findUserByName() {
        assertTrue(false);
    }
}