package sw806f18.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Group;
import sw806f18.server.model.Researcher;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class DatabaseTest {
    private String email = "test@testington.com";

    @Before
    public void setUp() throws Exception
    {
        Connection con = null;
        try {

            con = createConnection();
            Statement stmt = con.createStatement();

            String q = "DELETE FROM users WHERE email = '" + email + "';";

            stmt.execute(q);
            stmt.close();

            closeConnection(con);

        } catch (SQLException e) {
            //Send stacktrace to log
            throw new DeleteUserException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new DeleteUserException("Server error, contact system administrator", e);
        }
    }

    @After
    public void tearDown() throws Exception
    {
        Connection con = null;
        try {

            con = createConnection();
            Statement stmt = con.createStatement();

            String q = "DELETE FROM users WHERE email = '" + email + "';";

            stmt.execute(q);
            stmt.close();

            closeConnection(con);
        } catch (SQLException e) {
            //Send stacktrace to log
            throw new DeleteUserException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new DeleteUserException("Server error, contact system administrator", e);
        }
    }

    @Test
    public void createGetDeleteResearcher() throws Exception {
        Researcher researcher = new Researcher(email,"50505050");
        assertTrue(Database.createResearcher(researcher, "1234").equals(researcher));
        assertTrue(Database.getResearcher(email, "1234").equals(researcher));

        Database.deleteResearcher(researcher.email);
        boolean deleted = false;


        try{
            Database.getResearcher(researcher.email, "1234");
        }
        catch (LoginException e){
            deleted = true;
        }

        assertTrue(deleted);
    }

    @Test
    public void getAllGroups() throws Exception{
        List<Group> groups = Database.getAllGroups();
        List<Group> expected = TestHelpers.testGroups();
        assertTrue(groups.equals(expected));
    }

    @Test
    public void addGroup(){
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
    public void deleteGroup(){
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

    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://" + Configurations.instance.postgresIp() + ":" + Configurations.instance.postgresPort() + "/postgres",
                        Configurations.instance.postgresUser(), Configurations.instance.postgresPassword());
        return c;
    }

    private static void closeConnection(Connection c) throws SQLException {
        c.close();
    }
}