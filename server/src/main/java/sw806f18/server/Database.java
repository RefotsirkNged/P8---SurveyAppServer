package sw806f18.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {

    /**
     * Creates a new Database connection to the PostgreSQL Database.
     * @return An open Database connection.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://" + Configurations.instance.postgresIp() + ":" + Configurations.instance.postgresPort() + "/postgres",
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
    static int getUser(Connection connection, String email, String password) throws SQLException {
        String query = "SELECT id, password, salt FROM users WHERE username = '" + email + "'";
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

    public static List<group> getAllGroups(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM groups";
        ResultSet resultSet = statement.executeQuery(query);
        List<group> groups = new ArrayList<group>();

        if (resultSet.next()) {
            groups.add(new group(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("hub")));
        }
        return groups;
    }

    /**
     * Validates if the user is a researcher
     * @param connection An open Database connection
     * @param id Id of researcher
     * @return Boolean value specifying if the id belongs to a researcher.
     * @throws SQLException
     */
    static boolean isResearcher(Connection connection, int id) throws SQLException {
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
     * Closes an open Database connection
     * @param c An open Database connection
     * @throws SQLException
     */
    static void closeConnection(Connection c) throws SQLException {
        c.close();
    }
}
