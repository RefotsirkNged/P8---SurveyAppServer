package sw806f18.server;

import org.junit.Before;
import org.junit.Test;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;

import java.util.List;

import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.DeleteGroupException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Group;

/**
 * Created by augustkorvell on 13/03/2018.
 */

public class RelationalDatabaseTest {
    private String email = "test@testington.com";

    /**
     * Before.
     *
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");
        TestHelpers.resetDatabase();
        TestHelpers.populateDatabase();
    }

    @Test
    public void createGetDeleteResearcher() throws Exception {
        Researcher researcher = new Researcher(email, "50505050");
        assertTrue(RelationalDatabase.createResearcher(researcher, "1234").equals(researcher));
        assertTrue(RelationalDatabase.getResearcher(email, "1234").equals(researcher));

        RelationalDatabase.deleteResearcher(researcher.email);
        boolean deleted = false;


        try {
            RelationalDatabase.getResearcher(researcher.email, "1234");
        } catch (LoginException e) {
            deleted = true;
        }

        assertTrue(deleted);
    }

    @Test
    public void getAllGroups() throws Exception {
        // ToDo: Fix pls
        List<Group> groups = RelationalDatabase.getAllGroups();
        List<Group> expected = TestHelpers.testGroups();
        assertTrue(groups.equals(expected));
    }

    @Test
    public void addGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(RelationalDatabase.addGroup(group.getName()));
            List<Group> groups = RelationalDatabase.getAllGroups();
            assertTrue(groups.contains(group));
            RelationalDatabase.deleteGroup(group.getId());
        } catch (GetGroupsException e) {
            e.printStackTrace();
        } catch (DeleteGroupException e) {
            e.printStackTrace();
        } catch (AddGroupException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createInvite() {
        assertTrue(createHelperInvite());
    }

    private boolean createHelperInvite() {
        boolean created = true;
        try {
            RelationalDatabase.createInvite("0123456789", "abc");
        } catch (CreateInviteException ex) {
            created = false;
        }
        return created;
    }

    @Test
    public void deleteGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(RelationalDatabase.addGroup(group.getName()));
            RelationalDatabase.deleteGroup(group.getId());
            List<Group> groups = RelationalDatabase.getAllGroups();
            assertFalse(groups.contains(group));
        } catch (GetGroupsException e) {
            e.printStackTrace();
        } catch (DeleteGroupException e) {
            e.printStackTrace();
        } catch (AddGroupException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCprFromInvite() throws SQLException, ClassNotFoundException {
        assertEquals(RelationalDatabase.getCPRFromKey("abc"), "0123456789");
    }

    @Test
    public void removeGroupMember() {
        assertTrue(false);
    }

    @Test
    public void findUserByName() {
        assertTrue(false);
    }

    @Test
    public void createParticipant() throws SQLException, ClassNotFoundException {
        assertTrue(createHelperInvite());


        Participant participant = new Participant(-1, "test1@testesen.dk", "0123456798");

        boolean success = true;

        try {
            RelationalDatabase.createParticipant(participant, "power123");
        } catch (CreateUserException e) {
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void addGroupMember() {
        assertTrue(false);
    }

    @Test
    public void getParticipant() {

        Participant participant = new Participant(-1, "test@testesen.dk", "0123456789");
        Participant createdParticipant = null;
        boolean success = true;


        try {
            createdParticipant = RelationalDatabase.getParticipant(participant.email, "power123");
        } catch (LoginException ex) {
            success = false;
        }

        assertTrue(participant.equals(createdParticipant));
        assertTrue(success);
    }

    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://"
                                + Configurations.instance.postgresIp() + ":"
                                + Configurations.instance.postgresPort() + "/postgres",
                        Configurations.instance.postgresUser(),
                        Configurations.instance.postgresPassword());
        return c;
    }

    @Test
    public void clearInviteFromKey() {
        boolean success = true;
        try {
            RelationalDatabase.clearInviteFromKey("abc");
        } catch (CPRKeyNotFoundException ex) {
            success = false;
        }
        assertTrue(success);
    }

}