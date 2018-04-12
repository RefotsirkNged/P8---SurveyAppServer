package sw806f18.server.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sw806f18.server.Configurations;
import sw806f18.server.Security;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.*;

public class RelationalDatabase {

    /**
     * Creates a new RelationalDatabase connection to the PostgreSQL RelationalDatabase.
     *
     * @return An open RelationalDatabase connection.
     * @throws SQLException           SQL Exception.
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
     * Gets a user from the Database.
     *
     * @param connection An open Database connection.
     * @param email      User email.
     * @param password   User password.
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
     *
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
     *
     * @param group group.
     * @return ID of group.
     * @throws AddGroupException Exception.
     */
    static Group addGroup(Group group) throws AddGroupException {
        Connection con;
        int id = 0;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "INSERT INTO groups (name, hub) VALUES ('"
                    + group.getName() + "', " + group.getHub() + ") RETURNING id";
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            con.close();

            return new Group(id, group.getName(), group.getHub());
        } catch (SQLException e) {
            throw new AddGroupException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new AddGroupException(e.getMessage());
        }
    }

    /**
     * Deletes group in database.
     *
     * @param id ID of group to delete.
     * @throws DeleteGroupException Exceptions.
     */
    static void deleteGroup(int id) throws DeleteGroupException {
        Connection con;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String q1 = "DELETE FROM hasgroup WHERE groupid=" + id;
            statement.executeUpdate(q1);

            String q2 = "DELETE FROM hasmodule WHERE groupid=" + id;
            statement.executeUpdate(q2);

            String q3 = "DELETE FROM groups WHERE id=" + id;
            statement.executeUpdate(q3);
            con.close();
        } catch (SQLException e) {
            throw new DeleteGroupException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new DeleteGroupException(e.getMessage());
        }
    }

    /**
     * Validates if the user is a researcher.
     *
     * @param id Id of researcher.
     * @return Boolean value specifying if the id belongs to a researcher.
     * @throws SQLException Exception.
     */
    static boolean isResearcher(int id) {
        Connection connection = null;
        try {
            connection = createConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) FROM researcher WHERE id = " + id;
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            } else {
                return false;
            }
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Attempts login and returns id of researcher if successful.
     *
     * @param email    Email.
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

            if (userid == -1 || !isResearcher(userid)) {
                throw new LoginException("Invalid email or password!");
            } else {
                Statement statement = connection.createStatement();
                String query = "SELECT r.phone AS phone, u.firstname AS firstname, u.lastname AS lastname"
                        + " FROM researcher r, users u WHERE r.id = " + userid
                        + " AND r.id = u.id";
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    researcher = new Researcher(userid, email,
                            resultSet.getString("phone"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"));
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
     *
     * @param researcher Researcher object.
     * @param password   Password.
     * @throws CreateUserException Exception.
     */
    static Researcher createResearcher(Researcher researcher, String password)
            throws CreateUserException {
        Connection con = null;
        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt, firstname, lastname) "
                    + "VALUES ( '" + researcher.getEmail() + "' , '"
                    + Security.convertByteArrayToString(Security.hash(password, salt))
                    + "' , '" + Security.convertByteArrayToString(salt) + "',"
                    + " '" + researcher.getFirstName() + "',"
                    + " '" + researcher.getLastName() + "' ) "
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
            throw new CreateUserException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateUserException("Server error, contact system administrator", e);
        }

        try {
            return getResearcher(researcher.getEmail(), password);
        } catch (LoginException e) {
            throw new CreateUserException("Server error, contact system administrator", e);
        }
    }

    /**
     * Closes an open Database connection.
     *
     * @param c An open Database connection.
     * @throws SQLException Exception.
     */
    private static void closeConnection(Connection c) throws SQLException {
        c.close();
    }

    static void createInvite(Invite invite) throws CreateInviteException {
        Connection con = null;
        try {

            con = createConnection();
            Statement stmt = con.createStatement();
            String query = "INSERT INTO invite VALUES ('" + invite.getCpr() + "','" + invite.getKey() + "')";
            stmt.execute(query);
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateInviteException("Server error, contact system administrator", e);
        }
    }


    static String getCPRFromKey(String key) throws SQLException, ClassNotFoundException {
        Connection conn = createConnection();
        Statement stmt = conn.createStatement();

        String query = "SELECT cpr FROM invite WHERE key = '" + key + "'";

        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return res.getString("cpr");
        } else {
            return null;
        }
    }

    static void clearInviteFromKey(String key) throws CprKeyNotFoundException {

        try {
            Connection conn = createConnection();
            Statement stmt = conn.createStatement();
            String query = "DELETE FROM invite WHERE key = '" + key + "'";
            stmt.execute(query);
            conn.close();
        } catch (SQLException e) {
            throw new CprKeyNotFoundException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            throw new CprKeyNotFoundException("Server error, contact system administrator", e);
        }
    }

    static Participant getParticipant(String email, String password) throws LoginException {
        Connection connection = null;
        int userid = -1;
        Participant participant = null;

        try {
            connection = createConnection();
            userid = getUser(connection, email, password);

            if (userid == -1 || !isParticipant(userid)) {
                throw new LoginException("Invalid email or password!");
            } else {
                Statement statement = connection.createStatement();
                String query = "SELECT p.cpr AS cpr, u.firstname AS firstname, u.lastname AS lastname, "
                        + "p.primarygroup AS primarygroup"
                        + " FROM participants p, users u WHERE p.id = " + userid
                        + " AND p.id = u.id";
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    participant = new Participant(userid, email,
                            resultSet.getString("cpr"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getInt("primarygroup"));
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

    static boolean isParticipant(int id) {
        Connection connection = null;
        try {
            connection = createConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) FROM participants WHERE id = " + id;
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            } else {
                return false;
            }
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    static Participant createParticipant(Participant participant, String password) throws CreateUserException {
        Connection con = null;
        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt, firstname, lastname) "
                    + "VALUES ( '" + participant.getEmail() + "' , '"
                    + Security.convertByteArrayToString(Security.hash(password, salt))
                    + "' , '" + Security.convertByteArrayToString(salt)
                    + "', '" + participant.getFirstName() + "', '"
                    + participant.getLastName() + "' ) "
                    + "RETURNING id";

            ResultSet rs = stmt1.executeQuery(q1);
            rs.next();
            int id = rs.getInt(1);
            stmt1.close();

            Statement stmt2 = con.createStatement();
            String q2 = "INSERT INTO participants (id, cpr, birthday, primarygroup)"
                    + "VALUES (" + id + ", '" + participant.getCpr() + "', '"
                    + LocalDateTime.ofInstant(participant.getBirthday().toInstant(),
                    ZoneId.systemDefault()) + "', "
                    + participant.getPrimaryGroup() + ")";
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
            return getParticipant(participant.getEmail(), password);
        } catch (LoginException e) {
            throw new CreateUserException("Server error, contact system administrator", e);
        }
    }


    static List<Participant> getAllParticipants() throws GetAllParticipantsException {
        List<Participant> ret = new ArrayList<>();
        Connection con = null;

        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();

            String q1 = "SELECT u.id id, u.email email, u.firstname firstname, "
                    + "u.lastname lastname, p.cpr cpr, p.primarygroup primarygroup"
                    + " FROM users u, participants p WHERE u.id = p.id";

            ResultSet rs = stmt1.executeQuery(q1);
            while (rs.next()) {
                ret.add(new Participant(rs.getInt("id"), rs.getString("email"),
                        rs.getString("cpr"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getInt("primarygroup")));
            }
            closeConnection(con);
        } catch (SQLException e) {
            throw new GetAllParticipantsException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            throw new GetAllParticipantsException("Server error, contact system administrator", e);
        }

        return ret;
    }

    static void addGroupMember(Group group1, Participant participant1) throws AddGroupMemberException {
        Connection con = null;

        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();

            String q1 = "INSERT INTO hasgroup (participantid, groupid)"
                    + " VALUES (" + participant1.getId() + ", " + group1.getId() + ")";

            stmt1.executeUpdate(q1);
            closeConnection(con);
        } catch (SQLException e) {
            throw new AddGroupMemberException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            throw new AddGroupMemberException("Server error, contact system administrator", e);
        }
    }

    static void removeParticipantFromGroup(Group group, Participant participant)
            throws RemoveParticipantFromGroupException {
        Connection con = null;

        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();

            String q1 = "DELETE FROM hasgroup WHERE participantid = " + participant.getId()
                    + " AND groupid = " + group.getId();

            stmt1.executeUpdate(q1);
            closeConnection(con);
        } catch (SQLException e) {
            throw new RemoveParticipantFromGroupException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            throw new RemoveParticipantFromGroupException("Server error, contact system administrator", e);
        }
    }

    static List<Participant> getGroupMembers(Group group1) throws GetGroupMemberException {
        List<Participant> ret = new ArrayList<>();
        Connection con = null;

        try {
            con = createConnection();
            Statement stmt1 = con.createStatement();

            String q1 = "SELECT u.id id, u.email email, u.firstname firstname, "
                    + "u.lastname lastname, p.cpr cpr, p.primarygroup primarygroup"
                    + " FROM users u, participants p, hasgroup h WHERE u.id = p.id AND h.groupid = " + group1.getId()
                    + " AND h.participantid = u.id";

            ResultSet rs = stmt1.executeQuery(q1);
            while (rs.next()) {
                ret.add(new Participant(rs.getInt("id"), rs.getString("email"),
                        rs.getString("cpr"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getInt("primarygroup")));
            }
            closeConnection(con);
        } catch (SQLException e) {
            throw new GetGroupMemberException("Server error, contact system administrator", e);
        } catch (ClassNotFoundException e) {
            throw new GetGroupMemberException("Server error, contact system administrator", e);
        }

        return ret;
    }

    static int addSurvey(Survey s) throws SurveyException {
        Connection con;
        int id = 0;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "INSERT INTO modules (name, frequencyvalue, "
                    + "frequencytype, description) VALUES ('" + s.getTitle() + "', "
                    + s.getFrequencyValue() + ", '" + s.getFrequencyType()
                    + "', '" + s.getDescription() + "') RETURNING id";

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

    static List<Integer> getUsersSurveyIDs(User user) throws SurveyException {
        List<Integer> ids = new ArrayList<>();
        Connection con;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "SELECT hasmodule.moduleid FROM hasgroup, hasmodule WHERE hasgroup.participantid = "
                    + user.getId()
                    + " AND hasgroup.groupid = hasmodule.groupid";

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
            con.close();

        } catch (SQLException e) {
            throw new SurveyException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        }

        return ids;
    }

    /**
     * Links a module to a group.
     *
     * @param moduleID ID of a module.
     * @param groupID  ID of a group.
     */
    static void setModuleLink(int moduleID, int groupID) throws P8Exception {
        Connection con = null;
        int id = 0;

        try {
            con = createConnection();
            Statement isAlreadyConnected = con.createStatement();
            Statement statement = con.createStatement();
            String query = "INSERT INTO hasModule (groupid, moduleid) "
                    + "VALUES ( " + groupID + " , " + moduleID + " )";
            statement.execute(query);
            con.close();
        } catch (SQLException e) {
            throw new SurveyException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        }
    }

    static int addHub() throws HubException {
        Connection con;
        int id = 0;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "INSERT INTO hubs DEFAULT VALUES RETURNING id";
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            throw new HubException("Server error. Contact system administrator");
        } catch (ClassNotFoundException e) {
            throw new HubException("Server error. Contact system administrator");
        }
        return id;
    }

    /**
     * Get hub ID by user ID.
     *
     * @return Hub ID.
     * @throws HubException Exception.
     */
    static int getHubIdByUser(int userId) throws HubException {
        Connection con;

        try {
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "SELECT g.hub AS hubid FROM participants p, \"groups\" g "
                    + "WHERE p.id=" + userId + " AND p.primarygroup=g.id";
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();

            int hubID = resultSet.getInt("hubid");
            con.close();
            return hubID;
        } catch (SQLException e) {
            throw new HubException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new HubException(e.getMessage());
        }
    }

    static  List<Integer> getModuleLinks(int surveyID) throws SurveyException {
        List<Integer> linkedGroups = new ArrayList<>();
        Connection con = null;
        try {
            con = createConnection();
            Statement getLinkedGroups = con.createStatement();
            String getLinkedGroupsQuery = "SELECT groupid FROM hasModule WHERE moduleid = " + surveyID;
            ResultSet result = getLinkedGroups.executeQuery(getLinkedGroupsQuery);
            while (result.next()) {
                linkedGroups.add(result.getInt(1));
            }
            con.close();
        } catch (SQLException e) {
            throw new SurveyException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        }
        return linkedGroups;
    }

    static void addAnswer(Answer answer) throws AnswerException {
        String query = "INSERT INTO hasanswered (participantid, moduleid, timestamp) VALUES (?,?,?)";

        try {
            Connection connection = createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, answer.getUserID());
            preparedStatement.setInt(2, answer.getSurvey().getId());
            preparedStatement.setTimestamp(3, new Timestamp(answer.getTimeStamp().getTime()));
            preparedStatement.execute();

            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new AnswerException("Server error. Contact system administrator.", e);
        }
    }

    /**
     * Get modules by user.
     *
     * @param userId User ID.
     * @return List of modules metadata.
     */
    static List<Survey> getModulesByUser(int userId) throws GetModulesByUserException {
        Connection con;
        try {
            List<Survey> surveys = new ArrayList<>();
            con = createConnection();
            Statement statement = con.createStatement();
            String query = "SELECT m.id AS id, m.name AS name, m.description AS description\n"
                    + "FROM modules m, hasmodule hm, groups g, hasgroup hg\n"
                    + "WHERE m.id = hm.moduleid AND\n"
                    + "hm.groupid = g.id AND\n"
                    + "hg.groupid = g.id AND\n"
                    + "hg.participantid = " + userId + " AND\n"
                    + "m.id NOT IN (SELECT m.id\n"
                    + "            FROM modules m, hasanswered h\n"
                    + "            WHERE h.participantid = " + userId + " AND\n"
                    + "            h.moduleid = m.id AND\n"
                    + "            h.timestamp < (SELECT\n"
                    + "                           CASE WHEN frequencytype='ONCE' THEN to_timestamp(1)\n"
                    + "                                WHEN frequencytype='DAYS' "
                    + "THEN CURRENT_DATE - INTERVAL '1 day' * frequencyvalue\n"
                    + "                                WHEN frequencytype='WEEKS' "
                    + "THEN CURRENT_DATE - INTERVAL '1 week' * frequencyvalue\n"
                    + "                                WHEN frequencytype='MONTHS' "
                    + "THEN CURRENT_DATE - INTERVAL '1 month' * frequencyvalue\n"
                    + "                                WHEN frequencytype='YEARS'"
                    + " THEN CURRENT_DATE - INTERVAL '1 year' * frequencyvalue\n"
                    + "                                WHEN frequencytype='BIRTHDAY' "
                    + "THEN (SELECT to_timestamp(DATE_PART('year', CURRENT_DATE)-1 || ' ' || "
                    + "DATE_PART('month', birthday) || ' ' || DATE_PART('day', birthday), 'YYYY-MM-DD') "
                    + "FROM participants WHERE id = " + userId + ")\n"
                    + "                                ELSE to_timestamp(frequencyvalue)\n"
                    + "                           END\n"
                    + "                           FROM modules\n"
                    + "                           WHERE id = m.id))";

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                surveys.add(new Survey(resultSet.getInt("id"),
                        resultSet.getString("name"), resultSet
                        .getString("description")));
            }
            con.close();
            return surveys;
        } catch (SQLException e) {
            throw new GetModulesByUserException("Server error. Contact system administrator.");
        } catch (ClassNotFoundException e) {
            throw new GetModulesByUserException("Server error. Contact system administrator.");
        }
    }
}