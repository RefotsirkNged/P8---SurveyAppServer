package sw806f18.server.api;

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

import sw806f18.server.Authentication;
import sw806f18.server.Configurations;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.model.Group;

public class ResearcherGroupManagerTest {
    private HttpServer server;
    private WebTarget target;
    private String token;

    /**
     * Setup.
     *
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");
        token = Authentication.instance.getToken(TestHelpers.researcher1.getId());
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
        TestHelpers.resetDatabase();
        TestHelpers.populateDatabase();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void addGroup() {
        try {
            Response response = TestHelpers.addGroup(target,
                    TestHelpers.RESEARCHER_GROUPMANAGER_PATH, TestHelpers.groupCreate.getName(), token);
            assertEquals(response.getStatus(), 200);
            JsonObject jsonObject = TestHelpers.getPayload(response);
            TestHelpers.groupCreate.setId(jsonObject.getInt("groupid"));
            List<Group> groups = Database.getAllGroups();
            assertTrue(groups.contains(TestHelpers.groupCreate));
        } catch (GetGroupsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeGroup() {
        try {
            TestHelpers.deleteGroup(target, TestHelpers.RESEARCHER_GROUPMANAGER_PATH,
                    TestHelpers.group1.getId(), token);
            List<Group> groups = Database.getAllGroups();
            assertFalse(groups.contains(TestHelpers.group1));
        } catch (GetGroupsException e) {
            e.printStackTrace();
        }
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
            // ToDo: Fix pls
            assertTrue(element.getInt("id") == expected.get(i).getId());
        }
    }

    @Test
    public void findUserByName() {
        assertTrue(false);
    }

    @Test
    public void removeGroupMember() {
        assertTrue(false);
    }

    @Test
    public void addGroupMember() {
        assertTrue(false);
    }

    @Test
    public void getMembersByGroup() {
        assertTrue(false);
    }
}