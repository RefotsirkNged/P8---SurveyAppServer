package sw806f18.server;

<<<<<<<
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;
=======
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
>>>>>>>

import java.sql.Connection;
import java.sql.SQLException;

<<<<<<<
import static org.junit.Assert.*;
import static sw806f18.server.TestHelpers.populateDatabase;
import static sw806f18.server.TestHelpers.resetDatabase;
=======
import java.util.List;
>>>>>>>

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

<<<<<<<
    @Before
    public void setUp() throws Exception
    {
        resetDatabase();
        populateDatabase();
    }

    @After
    public void tearDown() throws Exception
    {
        //resetDatabase();

//        Connection con = null;
//        try {
//
//            con = createConnection();
//            Statement stmt = con.createStatement();
//
//            String q = "DELETE FROM users WHERE email = '" + email + "';";
//
//            stmt.execute(q);
//            stmt.close();
//
//            closeConnection(con);
//
//        } catch (SQLException e) {
//            //Send stacktrace to log
//            throw new DeleteUserException("Server error, contact system administrator", e);
//        } catch (ClassNotFoundException e) {
//            //Send stacktrace to log
//            throw new DeleteUserException("Server error, contact system administrator", e);
//        }
    }


    private String fixedKey = "abc";

    private boolean createHelperInvite() {
        boolean created = true;
        try{
            Database.createInvite("0123456789", fixedKey);
=======
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
>>>>>>>
        }
        catch(CreateInviteException ex){
            created = false;
        }
        return created;
    }

<<<<<<<
    @Test
    public void createInvite() {
        assertTrue(createHelperInvite());
    }
=======
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
>>>>>>>

<<<<<<<
    @Test
    public void getCprFromInvite() throws SQLException, ClassNotFoundException {
        assertEquals(Database.getCPRFromKey("abc"), "0123456789");
    }
=======
    @Test
    public void removeGroupMember() {
        assertTrue(false);
    }
>>>>>>>

<<<<<<<
    @Test
    public void createParticipant() throws SQLException, ClassNotFoundException {
        assertTrue(createHelperInvite());
=======
    @Test
    public void findUserByName() {
        assertTrue(false);
    }
>>>>>>>

<<<<<<<
        Participant participant = new Participant(-1, "test1@testesen.dk", "0123456798");

        boolean success = true;

        try {
            Database.createParticipant(participant, "power123");
        }
        catch (CreateUserException e)
        {
            success = false;
        }

        assertTrue(success);
=======
    @Test
    public void addGroupMember() {
        assertTrue(false);
>>>>>>>
    }

<<<<<<<
    @Test
    public void getParticipant() {

        Participant participant = new Participant(-1, "test@testesen.dk", "0123456789");
        Participant createdParticipant = null;
        boolean success = true;



        try {
            createdParticipant = Database.getParticipant(participant.email, "power123");
        }
        catch (LoginException ex){
            success = false;
        }

        assertTrue(participant.equals(createdParticipant));
        assertTrue(success);
=======
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
>>>>>>>
    }

    @Test
    public void clearInviteFromKey(){
        boolean success = true;
        try{
            Database.clearInviteFromKey("abc");
        }
        catch(CPRKeyNotFoundException ex){
            success = false;
        }
        assertTrue(success);
    }

}