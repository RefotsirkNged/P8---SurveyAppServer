package sw806f18.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.exceptions.DeleteGroupException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.exceptions.LoginException;

import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;

/**
 * Created by augustkorvell on 13/03/2018.
 */

public class DatabaseTest {
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
        Assert.assertTrue(Database.createResearcher(researcher, "1234").equals(researcher));
        Assert.assertTrue(Database.getResearcher(email, "1234").equals(researcher));

        Database.deleteResearcher(researcher.email);
        boolean deleted = false;


        try {
            Database.getResearcher(researcher.email, "1234");
        } catch (LoginException e) {
            deleted = true;
        }

        Assert.assertTrue(deleted);
    }

    @Test
    public void getAllGroups() throws Exception {
        // ToDo: Fix pls
        List<Group> groups = Database.getAllGroups();
        List<Group> expected = TestHelpers.testGroups();
        Assert.assertTrue(groups.equals(expected));
    }

    @Test
    public void addGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(Database.addGroup(group.getName()));
            List<Group> groups = Database.getAllGroups();
            Assert.assertTrue(groups.contains(group));
            Database.deleteGroup(group.getId());
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
        Assert.assertTrue(createHelperInvite());
    }

    private boolean createHelperInvite() {
        boolean created = true;
        try {
            Database.createInvite("0123456789", "abc");
        } catch (CreateInviteException ex) {
            created = false;
        }
        return created;
    }

    @Test
    public void deleteGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(Database.addGroup(group.getName()));
            Database.deleteGroup(group.getId());
            List<Group> groups = Database.getAllGroups();
            Assert.assertFalse(groups.contains(group));
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
        Assert.assertEquals(Database.getCprFromKey("abc"), "0123456789");
    }

    @Test
    public void removeGroupMember() {
        Assert.assertTrue(false);
    }

    @Test
    public void findUserByName() {
        Assert.assertTrue(false);
    }

    @Test
    public void createParticipant() throws SQLException, ClassNotFoundException {
        Assert.assertTrue(createHelperInvite());


        Participant participant = new Participant(-1, "test1@testesen.dk", "0123456798");

        boolean success = true;

        try {
            Database.createParticipant(participant, "power123");
        } catch (CreateUserException e) {
            success = false;
        }

        Assert.assertTrue(success);
    }

    @Test
    public void addGroupMember() {
        Assert.assertTrue(false);
    }

    @Test
    public void getParticipant() {

        Participant participant = new Participant(-1, "test@testesen.dk", "0123456789");
        Participant createdParticipant = null;
        boolean success = true;


        try {
            createdParticipant = Database.getParticipant(participant.email, "power123");
        } catch (LoginException ex) {
            success = false;
        }

        Assert.assertTrue(participant.equals(createdParticipant));
        Assert.assertTrue(success);
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
            Database.clearInviteFromKey("abc");
        } catch (CprKeyNotFoundException ex) {
            success = false;
        }
        Assert.assertTrue(success);
    }

}