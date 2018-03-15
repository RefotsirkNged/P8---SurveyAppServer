package sw806f18.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.exceptions.DeleteUserException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

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
        resetDatabase();

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


    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://" + Configurations.instance.postgresIp() + ":" + Configurations.instance.postgresPort() + "/" + Configurations.instance.postgresDatabase(),
                        Configurations.instance.postgresUser(), Configurations.instance.postgresPassword());
        return c;
    }

    private static void closeConnection(Connection c) throws SQLException {
        c.close();
    }

    private String fixedKey = "abc";

    private boolean createHelperInvite() {
        boolean created = true;

        Database.createInvite("0123456789", fixedKey);

        return created;
    }

    @Test
    public void createInvite() {
        assertTrue(createHelperInvite());
    }

    @Test
    public void getCprFromInvite() {
        assertTrue(createHelperInvite());
        assertEquals(Database.getCPRFromKey(fixedKey), "0123456789");
    }

    @Test
    public void createParticipant() throws SQLException, ClassNotFoundException {
        assertTrue(createHelperInvite());

        Participant participant = new Participant(-1, "sw806f18@gmail.com", "0123456789");

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
        assertTrue(createHelperInvite());

        Participant participant = new Participant(-1, "sw806f18@gmail.com", "0123456789");

        boolean success = true;

        try {
            Database.createParticipant(participant, "power123");
        }
        catch (CreateUserException e)
        {
            success = false;
        }

        Participant createdParticipant = Database.getParticipant(participant.email, "power123");

        assertEquals(participant, createdParticipant);
    }

    public static void resetDatabase() throws SQLException, ClassNotFoundException, IOException {
        Connection connection = createConnection();

        String clearSchema = new String(Files.readAllBytes(Paths.get("../sql/ClearDatabase.sql")));
        String createSchema = new String(Files.readAllBytes(Paths.get("../sql/CreateSchema.sql")));
        String schema = clearSchema + "\n" + createSchema;

        Statement statement = connection.createStatement();

        statement.execute(schema);

        closeConnection(connection);
    }
}