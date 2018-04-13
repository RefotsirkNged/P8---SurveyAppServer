package sw806f18.server.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;

import sw806f18.server.TestRunner;
import sw806f18.server.exceptions.*;

import sw806f18.server.model.*;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.*;

/**
 * Created by augustkorvell on 13/03/2018.
 */

@RunWith(TestRunner.class)
public class DatabaseTest {
    @Test
    public void removeQuestionFromSurvey() throws Exception {
        int surveyId = TestHelpers.survey2.getId();
        Question oldQuestion = TestHelpers.survey2.getQuestions().get(0);

        Database.removeQuestionFromSurvey(surveyId, oldQuestion.getId());

        assertTrue(!Database.getSurvey(surveyId).getQuestions().contains(oldQuestion));
    }

    @Test
    public void createResearcher() {
        Researcher researcher = null;

        try {
            researcher = Database.createResearcher(TestHelpers.researcherCreate, "power123");
        } catch (CreateUserException e) {
            fail();
        }

        assertEquals(researcher, TestHelpers.researcherCreate);
    }

    @Test
    public void getResearcher() {
        Researcher researcher = null;

        try {
            researcher = Database.getResearcher(TestHelpers.researcher1.getEmail(), "power123");
        } catch (LoginException e) {
            fail();
        }

        assertEquals(researcher, TestHelpers.researcher1);
    }

    @Test
    public void getAllGroups() throws Exception {
        // ToDo: Fix pls -- Fix what?
        List<Group> groups = Database.getAllGroups();
        List<Group> expected = TestHelpers.testGroups();
        assertEquals(groups, expected);
    }

    @Test
    public void addGroup() {
        Group group = null;
        try {
            group = Database.addGroup(TestHelpers.groupCreate);
        } catch (AddGroupException e) {
            fail();
        }

        assertEquals(group, TestHelpers.groupCreate);
    }

    @Test
    public void createInvite() {
        try {
            Database.createInvite(TestHelpers.inviteCreate);
        } catch (CreateInviteException ex) {
            fail();
        }
    }

    @Test
    public void deleteGroup() {
        try {
            Database.deleteGroup(TestHelpers.group1.getId());
        } catch (DeleteGroupException e) {
            fail();
        }
    }

    @Test
    public void getCprFromInvite() throws SQLException, ClassNotFoundException {
        assertEquals(Database.getCPRFromKey(TestHelpers.invite1.getKey()), TestHelpers.invite1.getCpr());
    }

    @Test
    public void createParticipant() {
        try {
            Database.createParticipant(TestHelpers.participantCreate, TestHelpers.PASSWORD);
        } catch (CreateUserException e) {
            fail();
        }
    }

    @Test
    public void getParticipant() {
        Participant createdParticipant = null;
        try {
            createdParticipant = Database.getParticipant(TestHelpers.participant1.getEmail(), TestHelpers.PASSWORD);
        } catch (LoginException ex) {
            fail();
        }
        assertEquals(createdParticipant, TestHelpers.participant1);

    }

    @Test
    public void clearInviteFromKey() {
        try {
            Database.clearInviteFromKey(TestHelpers.invite1.getKey());
        } catch (CprKeyNotFoundException e) {
            fail();
        }
    }

    @Test
    public void getAllParticipants() {
        List<Participant> participants = null;

        try {
            participants = Database.getAllParticipants();
        } catch (GetAllParticipantsException e) {
            fail();
        }
        List<Participant> expected = TestHelpers.participants();
        assertEquals(participants, expected);
    }

    @Test
    public void getAllSurveys() {
        List<Survey> surveys = null;
        boolean hasError = false;

        try {
            surveys = Database.getAllModules();
        } catch (SurveyException e) {
            hasError = true;
        }
        assertFalse(hasError);
        assertTrue(surveys.size() == 3);
    }

    @Test
    public void removeGroupMember() {
        try {
            Database.removeParticipantFromGroup(TestHelpers.group1, TestHelpers.participant1);
        } catch (RemoveParticipantFromGroupException e) {
            fail();
        }
    }

    @Test
    public void addGroupMember() {
        List<Participant> participants = null;

        try {
            Database.addGroupMember(TestHelpers.group1, TestHelpers.participant2);
            participants = Database.getGroupMembers(TestHelpers.group1);
        } catch (AddGroupMemberException | GetGroupMemberException e) {
            fail();
        }

        assertTrue(participants.contains(TestHelpers.participant2));
    }

    @Test
    public void getMembersByGroup() {
        List<Participant> participants = null;
        try {
            participants = Database.getGroupMembers(TestHelpers.group1);
        } catch (GetGroupMemberException e) {
            fail();
        }

        assertEquals(participants.size(), 1);
        assertEquals(participants.get(0), TestHelpers.participant1);
    }

    @Test
    public void addAndGetSurvey() throws Exception {
        List<Survey> surveys = TestHelpers.testSurveys();

        for (Survey survey : surveys) {
            survey.setId(Database.addSurvey(survey));
        }

        for (Survey survey : surveys) {
            assertEquals(survey, Database.getSurvey(survey.getId()));
        }
    }

    @Test
    public void getUsersSurveys() throws Exception {
        List<Survey> surveys = Database.getUsersSurveys(TestHelpers.participant1);

        assertTrue(surveys.size() > 0 && surveys.get(0).getId() == TestHelpers.survey1.getId());
    }

    @Test
    public void linkModuleToGroup() throws Exception {
        Database.linkModuleToGroup(TestHelpers.survey3.getId(), TestHelpers.group2.getId());
        List<Integer> linkedSurveys = Database.getModuleLinks(TestHelpers.survey3);
        assertEquals((int) linkedSurveys.get(0), TestHelpers.group2.getId());
    }

    @Test
    public void getModuleLinks() throws P8Exception, SQLException, ClassNotFoundException {
        List<Integer> linkedSurveys = Database.getModuleLinks(TestHelpers.survey1);
        assertEquals((int) linkedSurveys.get(0), TestHelpers.group1.getId());
    }

    @Test
    public void getModulesByUser() {
        int userId = TestHelpers.participant1.getId();

        List<Survey> expected = new ArrayList<>();
        expected.add(TestHelpers.survey1);
        expected.add(TestHelpers.survey2);

        List<Survey> actual = new ArrayList<>();

        try {
            actual = Database.getModulesByUser(userId);
        } catch (GetModulesByUserException e) {
            fail();
        }

        assertEquals(actual.size(), expected.size());

        for (int i = 0; i < actual.size(); i++) {
            Survey s1 = actual.get(i);
            Survey s2 = expected.get(i);
            assertTrue(s1.getTitle().equals(s2.getTitle()));
            assertTrue(s1.getDescription().equals(s2.getDescription()));
            assertEquals(s1.getId(), s2.getId());
            assertEquals(s1.getFrequencyType(), s2.getFrequencyType());
            assertEquals(s1.getFrequencyValue(), s2.getFrequencyValue());
        }
    }

    @Test
    public void getHubByUser() {
        Hub actual = null;
        try {
            actual = Database.getHubByUser(TestHelpers.participant1.getId());
        } catch (HubException e) {
            fail();
        }
        assertEquals(TestHelpers.hub1, actual);
    }

    @Test
    public void addHub() {
        try {
            Database.addHub(TestHelpers.hub2);
        } catch (HubException e) {
            fail();
        }
    }

    @Test
    public void addAnswer() {
        Answer answer = new Answer(TestHelpers.participant1.getId(), TestHelpers.survey2);
        try {
            Database.addAnswer(answer);
        } catch (AnswerException e) {
            e.printStackTrace();
            fail();
        }
        Answer newestAnswer = Database.getNewestAnswer(TestHelpers.participant1.getId());

        assertEquals(answer, newestAnswer);
    }

    @Test
    public void getGroupLinks() {
        boolean hasError = false;
        List<Survey> modules = new ArrayList<>();

        try {
            modules = Database.getGroupLinks(TestHelpers.group1.getId());
        } catch (SurveyException e) {
            hasError = true;
        }
        assertFalse(hasError);
        List<Survey> expected = new ArrayList<>();
        expected.add(TestHelpers.survey1);
        expected.add(TestHelpers.survey2);

        for (int i = 0; i < modules.size(); i++) {
            assertEquals(modules.get(i).getId(), expected.get(i).getId());
            assertTrue(modules.get(i).getTitle().equals(expected.get(i).getTitle()));
            assertTrue(modules.get(i).getDescription().equals(expected.get(i).getDescription()));
        }
    }

    @Test
    public void removeGroupLink() {
        boolean hasError = false;
        try {
            Database.removeGroupLink(TestHelpers.group1.getId(), TestHelpers.survey2.getId());
            List<Survey> modules = Database.getGroupLinks(TestHelpers.group1.getId());
            assertEquals(1, modules.size());
            assertEquals(modules.get(0).getId(), TestHelpers.survey1.getId());
        } catch (SurveyException e) {
            hasError = true;
        }
        assertFalse(hasError);
    }
}