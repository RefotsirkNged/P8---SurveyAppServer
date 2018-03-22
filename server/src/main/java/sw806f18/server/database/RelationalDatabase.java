package sw806f18.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sw806f18.server.Configurations;
import sw806f18.server.Security;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.*;

public class RelationalDatabase {

    /**
     * Creates a new RelationalDatabase connection to the PostgreSQL RelationalDatabase.
     * @return An open RelationalDatabase connection.
     * @throws SQLException SQL Exception.
     * @throws ClassNotFoundException Class Not Found Exception.
     */
    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://"
                                + Configurations.instance.getPostgresIp() + ":"
                                + Configurations.instance.getPostgresPort() + "/"
                                + Configurations.instance.getPostgresDatabase(),
                        Configurations.instance.getPostgresUser(),
                        Configurations.instance.getPostgresPassword());
        return c;
    }

    /**
     * Gets a user from the RelationalDatabase.
     * @param connection An open RelationalDatabase connection.
     * @param email User email.
     * @param password User password.
     * @return An integer for the user id, or -1 if the user isn't found.
     * @throws SQLException SQL Exception.
     */
    private static int getUser(Connection connection, String email, String password)
            throws SQLException {
        String query = "SELECT id, password, salt FROM users "
                + "WHERE email = '" + email + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (!resultSet.next()) {
            return -1;
        }

        byte[] saltedPassword = Security
                .convertStringToByteArray(resultSet.getString("password"));
        byte[] salt = Security
                .convertStringToByteArray(resultSet.getString("salt"));
        int id = resultSet.getInt("id");

        byte[] hashedPassword = Security.hash(password, salt);

        if (Arrays.equals(saltedPassword, hashedPassword)) {
            return id;
        } else {
            return -1;
        }
    }

    /**
     * Get list of all groups from database.
     * @return List of all groups.
     * @throws GetGroupsException Exception.
     */
    static List<Group> getAllGroups() throws GetGroupsException {
        Connection con;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "SELECT * FROM groups";
            ResultSet resultSet = statement.executeQuery(query);
            List<Group> groups = new ArrayList<Group>();

            while (resultSet.next()) {
                groups.add(new Group(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("hub")));
            }
            con.close();
            return groups;
        } catch (SQLException e) {
            throw new GetGroupsException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new GetGroupsException(e.getMessage());
        }
    }

    /**
     * Adds group to database.
     * @param name Name of group.
     * @return ID of group.
     * @throws AddGroupException Exception.
     */
    static int addGroup(String name) throws AddGroupException {
        Connection con;
        int id = 0;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "INSERT INTO groups (name, hub) VALUES ('"
                    + name + "', null) RETURNING id";
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            throw new AddGroupException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new AddGroupException(e.getMessage());
        }
        return id;
    }

    /**
     * Deletes group in database.
     * @param id ID of group to delete.
     * @throws DeleteGroupException Exceptions.
     */
    static void deleteGroup(int id) throws DeleteGroupException {
        Connection con;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "DELETE FROM groups WHERE id=" + id;
            statement.executeUpdate(query);
            con.close();
        } catch (SQLException e) {
            throw new DeleteGroupException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new DeleteGroupException(e.getMessage());
        }
    }

    /**
     * Validates if the user is a researcher.
     * @param connection An open RelationalDatabase connection.
     * @param id Id of researcher.
     * @return Boolean value specifying if the id belongs to a researcher.
     * @throws SQLException Exception.
     */
    private static boolean isResearcher(Connection connection, int id) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT COUNT(*) FROM researcher WHERE id = " + id;
        ResultSet resultSet = statement.executeQuery(query);

        if (resultSet.next()) {
            return resultSet.getInt(1) == 1;
        } else {
            return false;
        }
    }

    /**
     * Attempts login and returns id of researcher if successful.
     * @param email Email.
     * @param password Password.
     * @return Researcher object.
     * @throws LoginException Exception.
     */
    static Researcher getResearcher(String email, String password) throws LoginException {

        Connection connection = null;
        int userid = -1;
        Researcher researcher = null;

        try {
            connection = createConnection();

            userid = getUser(connection, email, password);

            if (userid == -1 || !isResearcher(connection, userid)) {
                throw new LoginException("Invalid email or password!");
            } else {
                Statement statement = connection.createStatement();
                String query = "SELECT phone FROM researcher WHERE id = " + userid;
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    researcher = new Researcher(userid, email, resultSet.getString("phone"));
                }
            }

            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoginException("Server error, contact system administrator");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new LoginException("Server error, contact system administrator");
        }

        return researcher;
    }

    /**
     * Adds a new researcher to the database.
     * @param researcher Researcher object.
     * @param password Password.
     * @throws CreateUserException Exception.
     */
    static Researcher createResearcher(Researcher researcher, String password)
            throws CreateUserException {
        Connection con = null;
        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt) "
                    + "VALUES ( '" + researcher.email + "' , '"
                    + Security.convertByteArrayToString(Security.hash(password, salt))
                    + "' , '" + Security.convertByteArrayToString(salt) + "' ) "
                    + "RETURNING id";

            ResultSet rs = stmt1.executeQuery(q1);
            rs.next();
            int id = rs.getInt(1);
            stmt1.close();

            Statement stmt2 = con.createStatement();
            String q2 = "INSERT INTO researcher (id, phone)"
                    + "VALUES (" + id + ", " + researcher.phone + ")";
            stmt2.executeUpdate(q2);
            stmt2.close();
            closeConnection(con);
        } catch (SQLException e) {
            //Send stacktrace to log
            throw new CreateUserException("Email is already in use", e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateUserException("Server error, contact system administrator", e);
        }

        try {
            return getResearcher(researcher.email, password);
        } catch (LoginException e) {
            throw new CreateUserException("Server error, contact system administrator", e);
        }
    }

    /**
     * Deletes a researcher by removing it from the database.
     * @param email Email.
     */
    static void deleteResearcher(String email) throws DeleteUserException {
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

    /**
     * Closes an open RelationalDatabase connection.
     * @param c An open RelationalDatabase connection.
     * @throws SQLException Exception.
     */
    private static void closeConnection(Connection c) throws SQLException {
        c.close();
    }

    static void createInvite(String cpr, String key) throws CreateInviteException {
        Connection con = null;
        try {

            con = createConnection();
            Statement stmt = con.createStatement();
            String query = "INSERT INTO invite VALUES ('" + cpr + "','" + key + "')";
            stmt.execute(query);
        }
        catch (SQLException | ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateInviteException("Server error, contact system administrator", e);
        }
    }


    static String getCPRFromKey(String key) throws SQLException, ClassNotFoundException {
        Connection conn = createConnection();
        Statement stmt = conn.createStatement();

        String query = "SELECT cpr FROM invite WHERE key = '" + key + "'";

        ResultSet res = stmt.executeQuery(query);
        if (res.next()){
            return res.getString("cpr");
        }
        else
            return null;
    }

    static void clearInviteFromKey(String key) throws CPRKeyNotFoundException{

        try{
            Connection conn = createConnection();
            Statement stmt = conn.createStatement();
            String query = "DELETE FROM invite WHERE key = '" + key + "'";
            stmt.execute(query);
        }
        catch (SQLException e) {
            throw new CPRKeyNotFoundException("Server error, contact system administrator", e);
        }
        catch (ClassNotFoundException e) {
            throw new CPRKeyNotFoundException("Server error, contact system administrator", e);
        }
    }

    static Participant getParticipant(String email, String password) throws LoginException{
        Connection connection = null;
        int userid = -1;
        Participant participant = null;

        try {
            connection = createConnection();
            userid = getUser(connection, email, password);

            if(userid == -1 || !isParticipant(connection, userid))
            {
                throw new LoginException("Invalid email or password!");
            }
            else{
                Statement statement = connection.createStatement();
                String query = "SELECT cpr FROM persons WHERE id = " + userid;
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    participant = new Participant(userid, email, resultSet.getString("cpr"));
                }
            }

            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoginException("Server error, contact system administrator");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new LoginException("Server error, contact system administrator");
        }


        return participant;


    }

    static boolean isParticipant(Connection conn, int id) throws SQLException {
        Statement stmt = conn.createStatement();
        String query = "SELECT COUNT(*) FROM persons WHERE id = " + id;
        ResultSet res = stmt.executeQuery(query);
        if (res.next()){
            return res.getInt(1) == 1;
        }
        else
            return false;
    }

    static Participant createParticipant(Participant participant, String password) throws CreateUserException{
        Connection con = null;
        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt) " +
                    "VALUES ( '" + participant.email + "' , '" + Security.convertByteArrayToString(Security.hash(password, salt)) + "' , '" + Security.convertByteArrayToString(salt) + "' ) " +
                    "RETURNING id";

            ResultSet rs = stmt1.executeQuery(q1);
            rs.next();
            int id = rs.getInt(1);
            stmt1.close();

            Statement stmt2 = con.createStatement();
            String q2 = "INSERT INTO persons (id, cpr)" +
                    "VALUES (" + id + ", '" + participant.cpr + "')";
            stmt2.executeUpdate(q2);
            stmt2.close();
            closeConnection(con);
        } catch (SQLException e) {
            //Send stacktrace to log
            throw new CreateUserException("Email is already in use", e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateUserException("Server error, contact system administrator", e);
        }

        try {
            return getParticipant(participant.email, password);
        } catch (LoginException e) {
            throw new CreateUserException("Server error, contact system administrator", e);
        }
    }

    static int addSurvey(Survey s) throws SurveyException{
        Connection con;
        int id = 0;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "INSERT INTO modules (name) VALUES ('" + s.getTitle() + "') RETURNING id";
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            throw new SurveyException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        }
        return id;
    }

    static List<Integer> getUsersSurveyIDs(User user){
        throw new NotImplementedException();
    }
}
