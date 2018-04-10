package sw806f18.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static sw806f18.server.TestHelpers.createConnection;
import static sw806f18.server.TestHelpers.survey1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Survey;

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
        TestHelpers.resetDatabase();
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

        Response response = TestHelpers.getAll(target,
            TestHelpers.RESEARCHER_GROUPMANAGER_PATH, token);
        assertEquals(response.getStatus(), 200);
        JsonObject jsonObject = TestHelpers.getPayload(response);

        JsonArray jsonArray = jsonObject.getJsonArray("groups");

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject element = jsonArray.get(i).asJsonObject();
            assertTrue(element.getString("name").equals(expected.get(i).getName()));
            assertTrue(element.getInt("id") == expected.get(i).getId());
        }
    }

    @Test
    public void removeGroupMember() {
        boolean hasError = false;
        List<Participant> expected = null;

        Response response = TestHelpers.removeGroupMember(target, TestHelpers.RESEARCHER_GROUPMANAGER_MEMBER_PATH,
            TestHelpers.participant1, TestHelpers.group1, token);
        assertEquals(200, response.getStatus());
        try {
            expected = Database.getGroupMembers(TestHelpers.group1);
        } catch (GetGroupMemberException e) {
            hasError = true;
        }

        assertFalse(hasError);
        assertNotNull(expected);
        assertEquals(expected.size(), 0);
    }

    @Test
    public void addGroupMember() {
        Response response = TestHelpers.addGroupMember(target, TestHelpers.RESEARCHER_GROUPMANAGER_MEMBER_PATH,
            TestHelpers.participant2, TestHelpers.group1, token);
        assertEquals(200, response.getStatus());

        boolean hasError = false;
        List<Participant> expected = null;

        try {
            expected = Database.getGroupMembers(TestHelpers.group1);
        } catch (GetGroupMemberException e) {
            hasError = true;
        }

        assertFalse(hasError);
        assertNotNull(expected);
        assertTrue(expected.contains(TestHelpers.participant2));
    }

    @Test
    public void getMembersByGroup() {
        Response response = TestHelpers.getGroupMembers(target, TestHelpers.RESEARCHER_PARTICIPANT_PATH,
            TestHelpers.group1, token);
        assertEquals(200, response.getStatus());

        JsonObject jsonObject = TestHelpers.getPayload(response);
        JsonArray jsonArray = jsonObject.getJsonArray("members");
        assertEquals(1, jsonArray.size());

        JsonObject element = jsonArray.get(0).asJsonObject();

        Participant received = new Participant(element.getInt("id"), element.getString("cpr"),
            element.getString("firstname"), element.getString("lastname"));

        assertTrue(TestHelpers.participant1.equalsNoMail(received));
    }

    @Test
    public void getAllParticipants() {
        Response response = TestHelpers.getAll(target, TestHelpers.RESEARCHER_PARTICIPANT_ALL_PATH,
            token);
        assertEquals(200, response.getStatus());

        JsonObject jsonObject = TestHelpers.getPayload(response);
        JsonArray jsonArray = jsonObject.getJsonArray("participants");
        assertEquals(2, jsonArray.size());

        JsonObject element = jsonArray.get(0).asJsonObject();
        Participant p1 = new Participant(element.getInt("id"), element.getString("cpr"),
            element.getString("firstname"), element.getString("lastname"));
        element = jsonArray.get(1).asJsonObject();
        Participant p2 = new Participant(element.getInt("id"), element.getString("cpr"),
            element.getString("firstname"), element.getString("lastname"));

        List<Participant> list = TestHelpers.participants();
        assertTrue(TestHelpers.containsNoMail(list, p1));
        assertTrue(TestHelpers.containsNoMail(list, p2));
    }

    @Test
    public void getAllSurveys() {
        Response response = TestHelpers.getAll(target, TestHelpers.SURVEY_PATH,
                token);
        assertEquals(200, response.getStatus());

        JsonObject jsonObject = TestHelpers.getPayload(response);
        JsonArray jsonArray = jsonObject.getJsonArray("surveys");
        JsonObject element = jsonArray.get(0).asJsonObject();
        Survey s1 = new Survey(element.getString("title"), element.getString("description"));
        assertTrue(TestHelpers.survey1.getTitle().equals(s1.getTitle()));
    }

    @Test
    public void linkModuleToGroup() throws P8Exception, SQLException, ClassNotFoundException {
        Response response = TestHelpers.linkModuleToSurvey(target, TestHelpers.RESEARCHER_GROUPMANAGER_LINK_PATH,
                TestHelpers.survey1.getId(), TestHelpers.group1.getId(), token);
        assertEquals(200, response.getStatus());

        List<Integer> linkedGroups = Database.getModuleLinks(TestHelpers.survey1);
        assertTrue(linkedGroups.contains(TestHelpers.group1.getId()));
    }
}