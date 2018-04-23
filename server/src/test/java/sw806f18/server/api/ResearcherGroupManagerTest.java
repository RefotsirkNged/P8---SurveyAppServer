package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.GetGroupMemberException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Survey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class ResearcherGroupManagerTest {

    @Test
    public void addGroup() {
        try {
            HttpURLConnection response = TestHelpers.addGroup(
                TestHelpers.RESEARCHER_GROUPMANAGER_PATH,
                TestHelpers.groupCreate.getName(),
                TestHelpers.tokenResearcher1);
            assertEquals(response.getResponseCode(), 200);
            JsonNode payload = TestHelpers.getJsonPayload(response);

            TestHelpers.groupCreate.setId(payload.get("groupid").asInt());
            List<Group> groups = Database.getAllGroups();
            assertTrue(groups.contains(TestHelpers.groupCreate));
        } catch (GetGroupsException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void removeGroup() {
        try {
            TestHelpers.deleteGroup(
                TestHelpers.RESEARCHER_GROUPMANAGER_PATH,
                TestHelpers.group1.getId(),
                TestHelpers.tokenResearcher1);
            List<Group> groups = Database.getAllGroups();
            assertFalse(groups.contains(TestHelpers.group1));
        } catch (GetGroupsException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getAllGroups() {
        List<Group> expected = TestHelpers.testGroups();

        HttpURLConnection response = null;
        try {
            response = TestHelpers.getAll(
                TestHelpers.RESEARCHER_GROUPMANAGER_PATH, TestHelpers.tokenResearcher1);
            assertEquals(response.getResponseCode(), 200);
            JsonNode jsonObject = TestHelpers.getJsonPayload(response);

            JsonNode arr = jsonObject.get("groups");
            for (int i = 0; i < arr.size(); i++) {
                JsonNode element = arr.get(i);
                assertEquals(element.get("name").asText(), expected.get(i).getName());
                assertEquals(element.get("id").asInt(), expected.get(i).getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void removeGroupMember() {
        List<Participant> expected = null;

        HttpURLConnection response = null;
        try {
            response = TestHelpers.removeGroupMember(
                TestHelpers.RESEARCHER_GROUPMANAGER_MEMBER_PATH,
                TestHelpers.participant1, TestHelpers.group1,
                TestHelpers.tokenResearcher1);

            assertEquals(200, response.getResponseCode());

            expected = Database.getGroupMembers(TestHelpers.group1);

            assertNotNull(expected);
            assertEquals(expected.size(), 0);
        } catch (IOException | GetGroupMemberException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addGroupMember() {
        try {
            HttpURLConnection response = TestHelpers.addGroupMember(
                TestHelpers.RESEARCHER_GROUPMANAGER_MEMBER_PATH,
                TestHelpers.participant2,
                TestHelpers.group1,
                TestHelpers.tokenResearcher1);
            assertEquals(200, response.getResponseCode());

            List<Participant> expected = null;

            expected = Database.getGroupMembers(TestHelpers.group1);
            assertNotNull(expected);
            assertTrue(expected.contains(TestHelpers.participant2));
        } catch (GetGroupMemberException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getMembersByGroup() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.getGroupMembers(
                TestHelpers.RESEARCHER_PARTICIPANT_PATH,
                TestHelpers.group1,
                TestHelpers.tokenResearcher1);
            assertEquals(200, response.getResponseCode());

            JsonNode jsonObject = TestHelpers.getJsonPayload(response);
            JsonNode members = jsonObject.get("members");
            assertEquals(1, members.size());

            JsonNode node = members.get(0);

            Participant received = new Participant(
                node.get("id").asInt(),
                node.get("cpr").asText(),
                node.get("firstname").asText(),
                node.get("lastname").asText(),
                node.get("primarygroup").asInt()
            );

            assertTrue(TestHelpers.participant1.equalsNoMail(received));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getAllParticipants() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.getAll(
                TestHelpers.RESEARCHER_PARTICIPANT_ALL_PATH,
                TestHelpers.tokenResearcher1);

            assertEquals(200, response.getResponseCode());

            JsonNode jsonObject = TestHelpers.getJsonPayload(response);
            JsonNode jsonArray = jsonObject.get("participants");
            assertEquals(2, jsonArray.size());

            JsonNode element = jsonArray.get(0);
            Participant p1 = new Participant(
                element.get("id").asInt(),
                element.get("cpr").asText(),
                element.get("firstname").asText(),
                element.get("lastname").asText(),
                element.get("primarygroup").asInt());

            element = jsonArray.get(1);
            Participant p2 = new Participant(element.get("id").asInt(),
                element.get("cpr").asText(),
                element.get("firstname").asText(),
                element.get("lastname").asText(),
                element.get("primarygroup").asInt());

            List<Participant> list = TestHelpers.participants();
            assertTrue(TestHelpers.containsNoMail(list, p1));
            assertTrue(TestHelpers.containsNoMail(list, p2));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getAllSurveys() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.getAll(TestHelpers.SURVEY_PATH,
                TestHelpers.tokenResearcher1);
            assertEquals(200, response.getResponseCode());

            JsonNode jsonObject = TestHelpers.getJsonPayload(response);
            JsonNode jsonArray = jsonObject.get("surveys");
            JsonNode element = jsonArray.get(0);
            Survey s1 = new Survey(element.get("title").asText(), element.get("description").asText());
            assertEquals(TestHelpers.survey1.getTitle(), s1.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void linkModuleToGroup() throws P8Exception, SQLException, ClassNotFoundException {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.linkModuleToSurvey(
                TestHelpers.RESEARCHER_GROUPMANAGER_LINK_PATH,
                TestHelpers.survey1.getId(), TestHelpers.group1.getId(), TestHelpers.tokenResearcher1);
            assertEquals(200, response.getResponseCode());

            List<Integer> linkedGroups = Database.getModuleLinks(TestHelpers.survey1);
            assertTrue(linkedGroups.contains(TestHelpers.group1.getId()));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}