package sw806f18.server.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.Configurations;
import sw806f18.server.TestHelpers;

import sw806f18.server.exceptions.*;

import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;
import sw806f18.server.model.Survey;

import javax.validation.constraints.AssertTrue;

/**
 * Created by augustkorvell on 13/03/2018.
 */

public class DatabaseTest {
    /**
     * Before.
     *
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");

        TestHelpers.populateDatabase();
    }

    @After
    public void cleanUp() throws Exception {
        TestHelpers.resetDatabase();
    }

    @Test
    public void createResearcher() {
        boolean hasError = false;
        Researcher researcher = null;

        try {
            researcher = Database.createResearcher(TestHelpers.researcherCreate, "power123");
        } catch (CreateUserException e) {
            hasError = true;
        }

        assertFalse(hasError);
        assertTrue(researcher.equals(TestHelpers.researcherCreate));
    }

    @Test
    public void getResearcher() {
        Researcher researcher = null;
        boolean hasError = false;

        try {
            researcher = Database.getResearcher(TestHelpers.researcher1.getEmail(), "power123");
        } catch (LoginException e) {
            hasError = true;
        }

        assertFalse(hasError);
        assertEquals(researcher, TestHelpers.researcher1);
    }

    @Test
    public void getAllGroups() throws Exception {
        // ToDo: Fix pls
        List<Group> groups = Database.getAllGroups();
        List<Group> expected = TestHelpers.testGroups();
        assertTrue(groups.equals(expected));
    }

    @Test
    public void addGroup() {
        Group group = null;
        boolean hasError = false;
        try {
            group = Database.addGroup(TestHelpers.groupCreate);
        } catch (AddGroupException e) {
            hasError = true;
        }

        assertFalse(hasError);
        assertEquals(group, TestHelpers.groupCreate);
    }

    @Test
    public void createInvite() {
        boolean hasError = false;
        try {
            Database.createInvite(TestHelpers.inviteCreate);
        } catch (CreateInviteException ex) {
            hasError = true;
        }
        assertFalse(hasError);
    }

    @Test
    public void deleteGroup() {
        boolean hasError = false;

        try {
            Database.deleteGroup(TestHelpers.group1.getId());
        } catch (DeleteGroupException e) {
            hasError = true;
        }

        assertFalse(hasError);
    }

    @Test
    public void getCprFromInvite() throws SQLException, ClassNotFoundException {
        assertEquals(Database.getCPRFromKey(TestHelpers.invite1.getKey()), TestHelpers.invite1.getCpr());
    }

    @Test
    public void createParticipant() throws SQLException, ClassNotFoundException {
        boolean hasError = false;

        try {
            Database.createParticipant(TestHelpers.participantCreate, "power123");
        } catch (CreateUserException e) {
            hasError = true;
        }

        assertFalse(hasError);
    }

    @Test
    public void getParticipant() {
        Participant createdParticipant = null;
        boolean hasError = false;
        try {
            createdParticipant = Database.getParticipant(TestHelpers.participant1.getEmail(), "power123");
        } catch (LoginException ex) {
            hasError = true;
        }
        assertFalse(hasError);
        assertEquals(createdParticipant, TestHelpers.participant1);

    }

    @Test
    public void clearInviteFromKey() {
        boolean hasError = false;
        try {
            Database.clearInviteFromKey(TestHelpers.invite1.getKey());
        } catch (CprKeyNotFoundException e) {
            hasError = true;
        }
        assertFalse(hasError);
    }

    @Test
    public void getAllParticipants() {
        boolean hasError = false;
        List<Participant> participants = null;

        try {
            participants = Database.getAllParticipants();
        } catch (GetAllParticipantsException e) {
            hasError = true;
        }
        List<Participant> expected = TestHelpers.participants();
        assertTrue(participants.equals(expected));
        assertFalse(hasError);
    }

    @Test
    public void removeGroupMember() {
        boolean hasError = false;
        try {
            Database.removeParticipantFromGroup(TestHelpers.group1, TestHelpers.participant1);
        } catch (RemoveParticipantFromGroupException e) {
            hasError = true;
        }

        assertFalse(hasError);
    }

    @Test
    public void addGroupMember() {
        boolean hasError = false;
        List<Participant> participants = null;

        try {
            Database.addGroupMember(TestHelpers.group1, TestHelpers.participant2);
            participants = Database.getGroupMembers(TestHelpers.group1);
        } catch (AddGroupMemberException e) {
            hasError = true;
        } catch (GetGroupMemberException e) {
            hasError = true;
        }

        assertTrue(participants.contains(TestHelpers.participant2));
        assertFalse(hasError);
    }

    @Test
    public void getMembersByGroup() {
        boolean hasError = false;
        List<Participant> participants = null;
        try {
            participants = Database.getGroupMembers(TestHelpers.group1);
        } catch (GetGroupMemberException e) {
            hasError = true;
        }

        assertEquals(participants.size(), 1);
        assertEquals(participants.get(0), TestHelpers.participant1);
        assertFalse(hasError);
    }

    @Test
    public void addAndGetSurvey() throws Exception {
        List<Survey> surveys = TestHelpers.testSurveys();
        List<Survey> addedSurveys;

        for (Survey survey : surveys) {
            survey.setId(Database.addSurvey(survey));
        }

        for (Survey survey : surveys) {
            assertTrue(survey.equals(Database.getSurvey(survey.getId())));
        }
    }

    @Test
    public void getUsersSurveys() throws Exception {
        List<Survey> surveys = Database.getUsersSurveys(TestHelpers.participant1);

        assertTrue(surveys.size() > 0 && surveys.get(0).getId() == TestHelpers.survey1.getId());
    }

    @Test
    public void linkModuleToGroup() throws Exception {
        Database.linkModuleToGroup(TestHelpers.survey2.getId(), TestHelpers.group2.getId());
        List<Integer> linkedSurveys = Database.getModuleLinks(TestHelpers.survey2);
        assertTrue(linkedSurveys.get(0).equals(TestHelpers.group2.getId()));
    }

    @Test
    public void getModuleLinks() throws P8Exception, SQLException, ClassNotFoundException {
        List<Integer> linkedSurveys = Database.getModuleLinks(TestHelpers.survey1);
        assertTrue(linkedSurveys.get(0).equals(TestHelpers.group1.getId()));
    }

}