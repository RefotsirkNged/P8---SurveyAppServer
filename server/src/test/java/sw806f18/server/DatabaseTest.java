package sw806f18.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static sw806f18.server.TestHelpers.populateDatabase;
import static sw806f18.server.TestHelpers.resetDatabase;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class DatabaseTest {
    private String email = "test@testington.com";

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
        }
        catch(CreateInviteException ex){
            created = false;
        }
        return created;
    }

    @Test
    public void createInvite() {
        assertTrue(createHelperInvite());
    }

    @Test
    public void getCprFromInvite() throws SQLException, ClassNotFoundException {
        assertEquals(Database.getCPRFromKey("abc"), "0123456789");
    }

    @Test
    public void createParticipant() throws SQLException, ClassNotFoundException {
        assertTrue(createHelperInvite());

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
    }

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