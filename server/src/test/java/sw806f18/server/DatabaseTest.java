package sw806f18.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.DeleteGroupException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Group;
import sw806f18.server.model.Researcher;

/**
 * Created by augustkorvell on 13/03/2018.
 */

public class DatabaseTest {
    private String email = "test@testington.com";

    /**
     * Before.
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");
    }

    @Test
    public void createGetDeleteResearcher() throws Exception {
        Researcher researcher = new Researcher(email,"50505050");
        assertTrue(Database.createResearcher(researcher, "1234").equals(researcher));
        assertTrue(Database.getResearcher(email, "1234").equals(researcher));

        Database.deleteResearcher(researcher.email);
        boolean deleted = false;


        try {
            Database.getResearcher(researcher.email, "1234");
        } catch (LoginException e) {
            deleted = true;
        }

        assertTrue(deleted);
    }

    @Test
    public void getAllGroups() throws Exception {
        List<Group> groups = Database.getAllGroups();
        List<Group> expected = TestHelpers.testGroups();
        assertTrue(groups.equals(expected));
    }

    @Test
    public void addGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(Database.addGroup(group.getName()));
            List<Group> groups = Database.getAllGroups();
            assertTrue(groups.contains(group));
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
    public void deleteGroup() {
        Group group = new Group("TestGroup", 0);
        try {
            group.setId(Database.addGroup(group.getName()));
            Database.deleteGroup(group.getId());
            List<Group> groups = Database.getAllGroups();
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
    public void removeGroupMember() {
        assertTrue(false);
    }

    @Test
    public void findUserByName() {
        assertTrue(false);
    }

    @Test
    public void addGroupMember() {
        assertTrue(false);
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

    private static void closeConnection(Connection c) throws SQLException {
        c.close();
    }
}