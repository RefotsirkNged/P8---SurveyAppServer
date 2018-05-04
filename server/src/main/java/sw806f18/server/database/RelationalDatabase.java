package sw806f18.server.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static void closeConnection(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeStatement(Statement s) {
        if (s != null) {
            try {
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeResultSet(ResultSet r) {
        if (r != null) {
            try {
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
                + "WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            return -1;
        }

        byte[] saltedPassword = Security
                .convertStringToByteArray(resultSet.getString("password"));
        byte[] salt = Security
                .convertStringToByteArray(resultSet.getString("salt"));
        int id = resultSet.getInt("id");

        byte[] hashedPassword = Security.hash(password, salt);

        closeStatement(statement);
        closeResultSet(resultSet);

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
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            con = createConnection();
            statement = con.createStatement();
            String query = "SELECT * FROM groups";
            resultSet = statement.executeQuery(query);
            List<Group> groups = new ArrayList<Group>();

            while (resultSet.next()) {
                groups.add(new Group(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("hub")));
            }
            return groups;
        } catch (SQLException | ClassNotFoundException e) {
            throw new GetGroupsException(e.getMessage());
        } finally {
            closeConnection(con);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    /**
     * Get list of all modules from database.
     *
     * @return List of all surveys.
     * @throws SurveyException Exception.
     */
    static List<Survey> getAllModules() throws SurveyException {
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            con = createConnection();
            statement = con.createStatement();
            String query = "SELECT name,description,id FROM modules";
            resultSet = statement.executeQuery(query);
            List<Survey> modules = new ArrayList<>();

            while (resultSet.next()) {
                modules.add(new Survey(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")));
            }
            return modules;
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(con);
            closeStatement(statement);
            closeResultSet(resultSet);
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
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int id = 0;

        try {
            connection = createConnection();
            String query = "INSERT INTO groups (name, hub) VALUES (?, ?) RETURNING id";
            statement = connection.prepareStatement(query);
            statement.setString(1, group.getName());
            statement.setInt(2, group.getHub());
            resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt(1);

            return new Group(id, group.getName(), group.getHub());
        } catch (SQLException | ClassNotFoundException e) {
            throw new AddGroupException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    /**
     * Deletes group in database.
     *
     * @param id ID of group to delete.
     * @throws DeleteGroupException Exceptions.
     */
    static void deleteGroup(int id) throws DeleteGroupException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = createConnection();
            String q1 = "DELETE FROM hasgroup WHERE groupid= ?";
            statement = connection.prepareStatement(q1);
            statement.setInt(1, id);
            statement.executeUpdate();

            String q2 = "DELETE FROM hasmodule WHERE groupid= ?";
            statement = connection.prepareStatement(q2);
            statement.setInt(1, id);
            statement.executeUpdate();

            String q3 = "DELETE FROM groups WHERE id= ?";
            statement = connection.prepareStatement(q3);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new DeleteGroupException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
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
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = createConnection();
            String query = "SELECT COUNT(*) FROM researcher WHERE id =  ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            } else {
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
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
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        int userid = -1;
        Researcher researcher = null;

        try {
            connection = createConnection();

            userid = getUser(connection, email, password);

            if (userid == -1 || !isResearcher(userid)) {
                throw new LoginException("Invalid email or password!");
            } else {
                String query = "SELECT r.phone AS phone, u.firstname AS firstname, u.lastname AS lastname"
                        + " FROM researcher r, users u WHERE r.id = ? AND r.id = u.id";
                statement = connection.prepareStatement(query);
                statement.setInt(1, userid);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    researcher = new Researcher(userid, email,
                            resultSet.getString("phone"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new LoginException("Server error, contact system administrator");
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
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
        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        ResultSet resultSet = null;


        try {
            connection = createConnection();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt, firstname, lastname) "
                    + "VALUES ( ?, ?, ?, ?, ? ) RETURNING id";
            statement1 = connection.prepareStatement(q1);
            statement1.setString(1, researcher.getEmail());
            statement1.setString(2, Security.convertByteArrayToString(Security.hash(password, salt)));
            statement1.setString(3, Security.convertByteArrayToString(salt));
            statement1.setString(4, researcher.getFirstName());
            statement1.setString(5, researcher.getLastName());

            resultSet = statement1.executeQuery();
            resultSet.next();
            int id = resultSet.getInt(1);

            String q2 = "INSERT INTO researcher (id, phone)"
                    + "VALUES ( ?, ?)";
            statement2 = connection.prepareStatement(q2);
            statement2.setInt(1, id);
            statement2.setInt(2, Integer.parseInt(researcher.phone));
            statement2.executeUpdate();

        } catch (SQLException e) {
            //Send stacktrace to log
            throw new CreateUserException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateUserException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement1);
            closeStatement(statement2);
            closeResultSet(resultSet);
        }

        try {
            return getResearcher(researcher.getEmail(), password);
        } catch (LoginException e) {
            throw new CreateUserException("Server error, contact system administrator", e);
        }
    }

    static void createInvite(Invite invite) throws CreateInviteException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = createConnection();
            String query = "INSERT INTO invite VALUES ( ?, ? )";
            statement = connection.prepareStatement(query);
            statement.setString(1, invite.getCpr());
            statement.setString(2, invite.getKey());
            statement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateInviteException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static String getCPRFromKey(String key) throws CprKeyNotFoundException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = createConnection();

            String query = "SELECT cpr FROM invite WHERE key = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, key);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("cpr");
            } else {
                return null;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new CprKeyNotFoundException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    static void clearInviteFromKey(String key) throws CprKeyNotFoundException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = createConnection();
            String query = "DELETE FROM invite WHERE key = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, key);
            statement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new CprKeyNotFoundException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static Participant getParticipant(String email, String password) throws LoginException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        int userid = -1;
        Participant participant = null;

        try {
            connection = createConnection();
            userid = getUser(connection, email, password);

            if (userid == -1 || !isParticipant(userid)) {
                throw new LoginException("Invalid email or password!");
            } else {
                String query = "SELECT p.cpr AS cpr, u.firstname AS firstname, u.lastname AS lastname, "
                        + "p.primarygroup AS primarygroup"
                        + " FROM participants p, users u WHERE p.id = ? AND p.id = u.id";
                statement = connection.prepareStatement(query);
                statement.setInt(1, userid);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    participant = new Participant(userid, email,
                            resultSet.getString("cpr"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getInt("primarygroup"));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new LoginException("Server error, contact system administrator");
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }

        return participant;
    }

    static boolean isParticipant(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();
            String query = "SELECT COUNT(*) FROM participants WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            } else {
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    static Participant createParticipant(Participant participant, String password) throws CreateUserException {
        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(email, password, salt, firstname, lastname) "
                    + "VALUES ( ?, ?, ?, ?, ? )"
                    + "RETURNING id";
            statement1 = connection.prepareStatement(q1);
            statement1.setString(1, participant.getEmail());
            statement1.setString(2, Security.convertByteArrayToString(Security.hash(password, salt)));
            statement1.setString(3, Security.convertByteArrayToString(salt));
            statement1.setString(4, participant.getFirstName());
            statement1.setString(5, participant.getLastName());

            resultSet = statement1.executeQuery();
            resultSet.next();
            int id = resultSet.getInt(1);

            String q2 = "INSERT INTO participants (id, cpr, birthday, primarygroup)"
                    + "VALUES ( ?, ?, ?, ?)";
            statement2 = connection.prepareStatement(q2);
            statement2.setInt(1, id);
            statement2.setInt(2, Integer.parseInt(participant.getCpr().trim()));    // TODO: Loss of leading zeros!!!
            statement2.setTimestamp(3, Timestamp.valueOf(LocalDateTime.ofInstant(participant.getBirthday().toInstant(),
                    ZoneId.systemDefault())));
            statement2.setInt(4, participant.getPrimaryGroup());
            statement2.executeUpdate();
        } catch (SQLException e) {
            //Send stacktrace to log
            throw new CreateUserException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            //Send stacktrace to log
            throw new CreateUserException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement1);
            closeStatement(statement2);
            closeResultSet(resultSet);
        }

        try {
            return getParticipant(participant.getEmail(), password);
        } catch (LoginException e) {
            throw new CreateUserException("Server error, contact system administrator", e);
        }
    }

    static List<Participant> getAllParticipants() throws GetAllParticipantsException {
        List<Participant> ret = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();
            statement = connection.createStatement();

            String query = "SELECT u.id id, u.email email, u.firstname firstname, "
                    + "u.lastname lastname, p.cpr cpr, p.primarygroup primarygroup"
                    + " FROM users u, participants p WHERE u.id = p.id";

            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                ret.add(new Participant(resultSet.getInt("id"), resultSet.getString("email"),
                        resultSet.getString("cpr"), resultSet.getString("firstname"),
                        resultSet.getString("lastname"), resultSet.getInt("primarygroup")));
            }
            closeConnection(connection);
        } catch (SQLException | ClassNotFoundException e) {
            throw new GetAllParticipantsException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }

        return ret;
    }

    static void addGroupMember(Group group1, Participant participant1) throws AddGroupMemberException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = createConnection();

            String query = "INSERT INTO hasgroup (participantid, groupid)"
                    + " VALUES ( ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, participant1.getId());
            statement.setInt(2, group1.getId());

            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new AddGroupMemberException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static void removeParticipantFromGroup(Group group, Participant participant)
            throws RemoveParticipantFromGroupException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = createConnection();

            String query = "DELETE FROM hasgroup WHERE participantid = ? AND groupid = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, participant.getId());
            statement.setInt(2, group.getId());

            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RemoveParticipantFromGroupException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static List<Participant> getGroupMembers(Group group1) throws GetGroupMemberException {
        List<Participant> ret = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();

            String query = "SELECT u.id id, u.email email, u.firstname firstname, "
                    + "u.lastname lastname, p.cpr cpr, p.primarygroup primarygroup"
                    + " FROM users u, participants p, hasgroup h WHERE u.id = p.id AND h.groupid = ?"
                    + " AND h.participantid = u.id";
            statement = connection.prepareStatement(query);
            statement.setInt(1, group1.getId());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ret.add(new Participant(resultSet.getInt("id"), resultSet.getString("email"),
                        resultSet.getString("cpr"), resultSet.getString("firstname"),
                        resultSet.getString("lastname"), resultSet.getInt("primarygroup")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new GetGroupMemberException("Server error, contact system administrator", e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }

        return ret;
    }

    static int addSurvey(Survey s) throws SurveyException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        int id = 0;

        try {
            connection = createConnection();
            String query = "INSERT INTO modules (name, frequencyvalue, "
                    + "frequencytype, description) VALUES ( ?, ?, ?::FREQUENCYTYPE, ?) RETURNING id";
            statement = connection.prepareStatement(query);
            statement.setString(1, s.getTitle());
            statement.setLong(2, s.getFrequencyValue());
            statement.setString(3, s.getFrequencyType().name());
            statement.setString(4, s.getDescription());
            resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt(1);
            ;
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }

        for (Question q : s.getQuestions()) {
            q.setId(addQuestionToSurvey(q, id));
        }

        return id;
    }

    private static int addQuestionToSurvey(Question question, int surveyID) throws SurveyException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        int id = 0;

        try {
            connection = createConnection();

            String query = "INSERT INTO questions (name, description, tag, moduleId) VALUES (?, ?, ?, ?) RETURNING id";
            int tagId = getTagId(connection, question);

            statement = connection.prepareStatement(query);
            statement.setString(1, question.getTitle());
            statement.setString(2, question.getDescription());
            statement.setInt(3, tagId);
            statement.setInt(4, surveyID);

            resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt(1);
        } catch (SQLException | ClassNotFoundException | P8Exception e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
        return id;
    }

    static List<Integer> getUsersSurveyIDs(User user) throws SurveyException {
        List<Integer> ids = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();
            String query = "SELECT hasmodule.moduleid FROM hasgroup, hasmodule WHERE hasgroup.participantid = ? "
                    + "AND hasgroup.groupid = hasmodule.groupid";
            statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
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
        Connection connection = null;
        PreparedStatement statement = null;

        int id = 0;

        try {
            connection = createConnection();
            String query = "INSERT INTO hasModule (groupid, moduleid) "
                    + "VALUES ( ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, groupID);
            statement.setInt(2, moduleID);
            statement.execute();

        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static int addHub() throws HubException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        int id = 0;

        try {
            connection = createConnection();
            statement = connection.createStatement();
            String query = "INSERT INTO hubs DEFAULT VALUES RETURNING id";
            resultSet = statement.executeQuery(query);
            resultSet.next();
            id = resultSet.getInt(1);
        } catch (SQLException | ClassNotFoundException e) {
            throw new HubException("Server error. Contact system administrator");
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
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
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();
            String query = "SELECT g.hub AS hubid FROM participants p, \"groups\" g "
                    + "WHERE p.id= ? AND p.primarygroup=g.id";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            resultSet.next();

            int hubID = resultSet.getInt("hubid");
            return hubID;
        } catch (SQLException | ClassNotFoundException e) {
            throw new HubException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    static List<Integer> getModuleLinks(int surveyID) throws SurveyException {
        List<Integer> linkedGroups = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        try {
            connection = createConnection();
            String query = "SELECT groupid FROM hasModule WHERE moduleid = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, surveyID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                linkedGroups.add(resultSet.getInt(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }

        return linkedGroups;
    }

    static void addAnswer(Answer answer) throws AnswerException {
        String query = "INSERT INTO hasanswered (participantid, moduleid, timestamp) VALUES (?,?,?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = createConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, answer.getUserID());
            preparedStatement.setInt(2, answer.getSurvey().getId());
            preparedStatement.setTimestamp(3, new Timestamp(answer.getTimeStamp().getTime()));
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new AnswerException("Server error. Contact system administrator.", e);
        } finally {
            closeConnection(connection);
            closeStatement(preparedStatement);
        }
    }

    /**
     * Get modules by user.
     *
     * @param userId User ID.
     * @return List of modules metadata.
     */
    static List<Survey> getModulesByUser(int userId) throws GetModulesByUserException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            List<Survey> surveys = new ArrayList<>();
            connection = createConnection();
            String query = "SELECT m.id AS id, m.name AS name, m.description AS description\n"
                    + "FROM modules m, hasmodule hm, groups g, hasgroup hg\n"
                    + "WHERE m.id = hm.moduleid AND\n"
                    + "hm.groupid = g.id AND\n"
                    + "hg.groupid = g.id AND\n"
                    + "hg.participantid = ? AND\n"
                    + "m.id NOT IN (SELECT m.id\n"
                    + "            FROM modules m, hasanswered h\n"
                    + "            WHERE h.participantid = ? AND\n"
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
                    + "FROM participants WHERE id = ?)\n"
                    + "                                ELSE to_timestamp(frequencyvalue)\n"
                    + "                           END\n"
                    + "                           FROM modules\n"
                    + "                           WHERE id = m.id))";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                surveys.add(new Survey(resultSet.getInt("id"),
                        resultSet.getString("name"), resultSet
                        .getString("description")));
            }
            return surveys;
        } catch (SQLException | ClassNotFoundException e) {
            throw new GetModulesByUserException("Server error. Contact system administrator.");
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    static void removeQuestionFromSurvey(int questionId) throws SurveyException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = createConnection();
            String query = "DELETE FROM questions WHERE id= ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, questionId);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static List<Survey> getGroupLinks(int groupId) throws SurveyException {
        List<Survey> modules = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = createConnection();
            String query = "SELECT m.id AS id, m.name AS title, m.description AS description "
                    + "FROM hasModule h, modules m WHERE h.groupid = ? AND m.id = h.moduleid";
            statement = connection.prepareStatement(query);
            statement.setInt(1, groupId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                modules.add(new Survey(resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
        return modules;
    }

    static void removeGroupLink(int groupId, int moduleId) throws SurveyException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = createConnection();
            String query = "DELETE FROM hasmodule WHERE groupid= ? AND moduleid= ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, groupId);
            statement.setInt(2, moduleId);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    static void updateSurvey(Survey survey) throws SurveyException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = createConnection();
            String q1 = "UPDATE modules SET name=?, "
                    + "frequencyvalue=?,frequencytype=?::FREQUENCYTYPE,description=? WHERE id=?";
            stmt = con.prepareStatement(q1);
            stmt.setString(1, survey.getTitle());
            stmt.setLong(2, survey.getFrequencyValue());
            stmt.setString(3, survey.getFrequencyType().name());
            stmt.setString(4, survey.getDescription());
            stmt.setInt(5, survey.getId());
            stmt.execute();

            for (Question q : survey.getQuestions()) {
                if (q.getId() == -1) {
                    q.setId(RelationalDatabase.addQuestionToSurvey(q, survey.getId()));
                } else {
                    updateQuestion(q);
                }
            }
        } catch (SQLException | ClassNotFoundException | P8Exception e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeStatement(stmt);
            closeConnection(con);
        }
    }

    static int getTagId(Connection con, Question q) throws SQLException, P8Exception {
        PreparedStatement statement = null;
        try {
            String tagQuery = "INSERT INTO tags (name, tagtype) VALUES (?,?) ON CONFLICT DO NOTHING";
            statement = con.prepareStatement(tagQuery);
            statement.setString(1, q.getTag());
            statement.setInt(2, q.getType().getValue());
            statement.execute();
            String getTagQuery = "SELECT id, tagtype FROM tags WHERE ? = name";
            statement = con.prepareStatement(getTagQuery);
            statement.setString(1, q.getTag());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if (resultSet.getInt(2) != q.getType().getValue()) {
                throw new P8Exception("wrong input type");
            }
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }


    }

    static void updateQuestion(Question q) throws SurveyException, P8Exception {
        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = createConnection();
            int tagId = getTagId(con, q);
            String query = "UPDATE questions SET name=?,description=?, tag=? WHERE id=?";
            statement = con.prepareStatement(query);
            statement.setString(1, q.getTitle());
            statement.setString(2, q.getDescription());
            statement.setInt(3, tagId);
            statement.setInt(4, q.getId());
            statement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new SurveyException(e.getMessage());
        } finally {
            closeConnection(con);
            closeStatement(statement);
        }
    }

    static List<String> getAllTags() throws P8Exception {
        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = createConnection();
            String query = "SELECT name FROM tags";
            statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<String> tags = new ArrayList<>();
            while (resultSet.next()) {
                tags.add(resultSet.getString(1));
            }
            return tags;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new P8Exception(e.getMessage());
        } finally {
            closeConnection(con);
            closeStatement(statement);
        }
    }

    static List<String> getIntTags() throws P8Exception {
        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = createConnection();
            String query = "SELECT name FROM tags WHERE tagtype = ?";
            statement = con.prepareStatement(query);
            statement.setInt(1, Question.Type.INT.getValue());
            ResultSet resultSet = statement.executeQuery();
            List<String> tags = new ArrayList<>();
            while (resultSet.next()) {
                tags.add(resultSet.getString(1));
            }
            return tags;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new P8Exception(e.getMessage());
        } finally {
            closeConnection(con);
            closeStatement(statement);
        }
    }

    static List<String> getStringTags() throws P8Exception {
        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = createConnection();
            String query = "SELECT name FROM tags WHERE tagtype = ?";
            statement = con.prepareStatement(query);
            statement.setInt(1, Question.Type.STRING.getValue());
            ResultSet resultSet = statement.executeQuery();
            List<String> tags = new ArrayList<>();
            while (resultSet.next()) {
                tags.add(resultSet.getString(1));
            }
            return tags;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new P8Exception(e.getMessage());
        } finally {
            closeConnection(con);
            closeStatement(statement);
        }
    }

    static List<Tuple<String, Integer>> queryTags(List<QueryRow> query) {
        //Todo
        return null;
    }
}