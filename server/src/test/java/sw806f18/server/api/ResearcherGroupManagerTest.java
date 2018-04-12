package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.GetGroupMemberException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.exceptions.NotImplementedException;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Survey;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class ResearcherGroupManagerTest {

    @Test
    public void addGroup() {
        try {
            Response response = TestHelpers.addGroup(TestListener.target,
                TestHelpers.RESEARCHER_GROUPMANAGER_PATH,
                TestHelpers.groupCreate.getName(),
                TestHelpers.tokenResearcher1);
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
            TestHelpers.deleteGroup(TestListener.target,
                TestHelpers.RESEARCHER_GROUPMANAGER_PATH,
                TestHelpers.group1.getId(),
                TestHelpers.tokenResearcher1);
            List<Group> groups = Database.getAllGroups();
            assertFalse(groups.contains(TestHelpers.group1));
        } catch (GetGroupsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllGroups() {
        List<Group> expected = TestHelpers.testGroups();

        Response response = TestHelpers.getAll(TestListener.target,
            TestHelpers.RESEARCHER_GROUPMANAGER_PATH, TestHelpers.tokenResearcher1);
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

        Response response = TestHelpers.removeGroupMember(TestListener.target,
            TestHelpers.RESEARCHER_GROUPMANAGER_MEMBER_PATH,
            TestHelpers.participant1, TestHelpers.group1,
            TestHelpers.tokenResearcher1);
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
        Response response = TestHelpers.addGroupMember(TestListener.target,
            TestHelpers.RESEARCHER_GROUPMANAGER_MEMBER_PATH,
            TestHelpers.participant2,
            TestHelpers.group1,
            TestHelpers.tokenResearcher1);
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
        Response response = TestHelpers.getGroupMembers(TestListener.target,
            TestHelpers.RESEARCHER_PARTICIPANT_PATH,
            TestHelpers.group1,
            TestHelpers.tokenResearcher1);
        assertEquals(200, response.getStatus());

        JsonObject jsonObject = TestHelpers.getPayload(response);
        JsonArray jsonArray = jsonObject.getJsonArray("members");
        assertEquals(1, jsonArray.size());

        JsonObject element = jsonArray.get(0).asJsonObject();

        Participant received = new Participant(
            element.getInt("id"),
            element.getString("cpr"),
            element.getString("firstname"),
            element.getString("lastname"),
            element.getInt("primarygroup"));

        assertTrue(TestHelpers.participant1.equalsNoMail(received));
    }

    @Test
    public void getAllParticipants() {
        Response response = TestHelpers.getAll(TestListener.target,
            TestHelpers.RESEARCHER_PARTICIPANT_ALL_PATH,
                TestHelpers.tokenResearcher1);
        assertEquals(200, response.getStatus());

        JsonObject jsonObject = TestHelpers.getPayload(response);
        JsonArray jsonArray = jsonObject.getJsonArray("participants");
        assertEquals(2, jsonArray.size());

        JsonObject element = jsonArray.get(0).asJsonObject();
        Participant p1 = new Participant(
            element.getInt("id"),
            element.getString("cpr"),
            element.getString("firstname"),
            element.getString("lastname"),
            element.getInt("primarygroup"));
        element = jsonArray.get(1).asJsonObject();
        Participant p2 = new Participant(element.getInt("id"),
            element.getString("cpr"),
            element.getString("firstname"),
            element.getString("lastname"),
            element.getInt("primarygroup"));

        List<Participant> list = TestHelpers.participants();
        assertTrue(TestHelpers.containsNoMail(list, p1));
        assertTrue(TestHelpers.containsNoMail(list, p2));
    }

    @Test
    public void getAllSurveys() {
        Response response = TestHelpers.getAll(TestListener.target, TestHelpers.SURVEY_PATH,
                TestHelpers.tokenResearcher1);
        assertEquals(200, response.getStatus());

        JsonObject jsonObject = TestHelpers.getPayload(response);
        JsonArray jsonArray = jsonObject.getJsonArray("surveys");
        JsonObject element = jsonArray.get(0).asJsonObject();
        Survey s1 = new Survey(element.getString("title"), element.getString("description"));
        assertTrue(TestHelpers.survey1.getTitle().equals(s1.getTitle()));
    }
}