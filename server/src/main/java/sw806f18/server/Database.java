package sw806f18.server;

import sw806f18.server.api.ResearcherResource;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.exceptions.DeleteUserException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;

import java.sql.*;
import java.util.Arrays;

public class Database {

    /**
     * Creates a new Database connection to the PostgreSQL Database.
     * @return An open Database connection.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://" + Configurations.instance.postgresIp() + ":" + Configurations.instance.postgresPort() + "/" + Configurations.instance.postgresDatabase(),
                        Configurations.instance.postgresUser(), Configurations.instance.postgresPassword());
        return c;
    }

    /**
     * Gets a user from the Database.
     * @param connection An open Database connection.
     * @param email User email
     * @param password User password
     * @return An integer for the user id, or -1 if the user isn't found.
     * @throws SQLException
     */
    private static int getUser(Connection connection, String email, String password) throws SQLException {
        String query = "SELECT id, password, salt FROM users WHERE email = '" + email + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if(!resultSet.next()){
            return -1;
        }

        byte[] saltedPassword = Security.convertStringToByteArray(resultSet.getString("password"));
        byte[] salt = Security.convertStringToByteArray(resultSet.getString("salt"));
        int id = resultSet.getInt("id");

        byte[] hashedPassword = Security.hash(password, salt);

        if (Arrays.equals(saltedPassword, hashedPassword)) {
            return id;
        } else {
            return -1;
        }
    }

    /**
     * Validates if the user is a researcher
     * @param connection An open Database connection
     * @param id Id of researcher
     * @return Boolean value specifying if the id belongs to a researcher.
     * @throws SQLException
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
     * Attempts login and returns id of researcher if successful
     * @param email
     * @param password
     * @return
     * @throws LoginException
     */
    public static Researcher getResearcher(String email, String password) throws LoginException {

        Connection connection = null;
        int userid = -1;
        Researcher researcher = null;

        try {
            connection = createConnection();

            userid = getUser(connection, email, password);

            if(userid == -1 || !isResearcher(connection, userid))
            {
                throw new LoginException("Invalid email or password!");
            }
            else{
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
     * Adds a new researcher to the database
     * @param researcher
     * @param password
     * @throws CreateUserException
     */
    public static Researcher createResearcher(Researcher researcher, String password) throws CreateUserException {
        Connection con = null;
        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt) " +
                    "VALUES ( '" + researcher.email + "' , '" + Security.convertByteArrayToString(Security.hash(password, salt)) + "' , '" + Security.convertByteArrayToString(salt) + "' ) " +
                    "RETURNING id";

            ResultSet rs = stmt1.executeQuery(q1);
            rs.next();
            int id = rs.getInt(1);
            stmt1.close();

            Statement stmt2 = con.createStatement();
            String q2 = "INSERT INTO researcher (id, phone)" +
                    "VALUES (" + id + ", " + researcher.phone + ")";
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
     * Deletes a researcher by removing it from the database
     * @param email
     */
    public static void deleteResearcher(String email) throws DeleteUserException{
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
     * Closes an open Database connection
     * @param c An open Database connection
     * @throws SQLException
     */
    private static void closeConnection(Connection c) throws SQLException {
        c.close();
    }

    public static void createInvite(String cpr, String key) throws CreateInviteException{
        Connection con = null;
        try {

            con = createConnection();
            Statement stmt = con.createStatement();
        }
        catch (SQLException | ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateInviteException("Server error, contact system administrator", e);
        }

    }

    public static String getCPRFromKey(String key) {
        return null;
    }

    public static void clearInviteFromKey(String key) {
        return;
    }

    public static Participant getParticipant(String email, String password) {
        return null;
    }

    public static void createParticipant(Participant participant, String password) throws CreateUserException{

    }
}
