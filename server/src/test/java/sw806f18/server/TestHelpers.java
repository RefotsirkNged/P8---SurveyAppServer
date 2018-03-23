package sw806f18.server;

import com.sun.mail.pop3.POP3Store;

import sw806f18.server.database.Database;
import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.AddGroupMemberException;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;

public class TestHelpers {
    public static final String RESEARCHER_LOGIN_PATH = "researcher/login";
    public static final String RESEARCHER_GROUPMANAGER_PATH = "researcher/groupmanager";
    public static final String RENEW_TOKEN_PATH = "renewtoken";

    public static final String PASSWORD = "power123";
    public static final String INVALID_RESEARCHER_EMAIL = "fake1@email.com";
    public static final String INVALID_RESEARCHER_PASSWORD = "fake";

    public static Researcher researcher1 = new Researcher("res1@earch.er", "88888888", "res", "earch");
    public static Researcher researcherCreate = new Researcher("test@testington.com", "50505050", "test", "test");

    public static Participant participant1 = new Participant(-1, "test1@testesen.dk", "0123456789", "partic", "ipant1");
    public static Participant participant2 = new Participant(-1, "test2@testesen.dk", "0123456780", "name", "one");
    public static Participant participantCreate = new Participant(-1, "test3@testesen.dk", "0123456798", "test", "test");

    public static Group group1 = new Group("Group 1", 0);
    public static Group group2 = new Group("Group 2", 0);
    public static Group group3 = new Group("Group 3", 0);
    public static Group groupCreate = new Group("TestGroup", 0);

    public static final Invite invite1 = new Invite("0011223344", "qwerty");
    public static final Invite inviteCreate = new Invite("4433221100", "asdfgh");

    public static void populateDatabase() throws CreateUserException, CreateInviteException, AddGroupException, AddGroupMemberException {
        // Create researchers
        researcher1 = Database.createResearcher(researcher1, PASSWORD);

        // Create test participants
        participant1 = Database.createParticipant(participant1, PASSWORD);
        participant2 = Database.createParticipant(participant2, PASSWORD);

        // Create invites
        Database.createInvite(invite1);

        group1 = Database.addGroup(group1);
        group2 = Database.addGroup(group2);
        group3 = Database.addGroup(group3);

        Database.addGroupMember(group1, participant1);
    }

    private TestHelpers() {
        // Don't instantiate me
    }

    /**
     * Get payload.
     *
     * @param response Response.
     * @return Payload.
     */
    public static JsonObject getPayload(Response response) {
        String content = response.readEntity(String.class);
        JsonReader jsonReader = Json.createReader(new StringReader(content));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    /**
     * Login function.
     *
     * @param target   Web target.
     * @param path     Endpoint.
     * @param email    Email.
     * @param password Password.
     * @return Response.
     */
    public static Response login(WebTarget target, String path, String email, String password) {
        return target.path(path).request().header("email", email)
                .header("password", password).post(Entity.text(""));
    }

    /**
     * Get all groups.
     *
     * @param target Web target.
     * @param path   Endpoint.
     * @param token  Token.
     * @return All groups.
     */
    public static Response getAllGroups(WebTarget target, String path, String token) {
        return target.path(path).request().header("token", token).get();
    }

    /**
     * Add group to database.
     *
     * @param target Web target.
     * @param path   Endpoint.
     * @param name   Group name
     * @param token  Token.
     * @return
     */
    public static Response addGroup(WebTarget target, String path, String name, String token) {
        return target.path(path).request().header("name", name)
                .header("token", token).put(Entity.text(""));
    }

    /**
     * Delete group in the database.
     *
     * @param target Web target.
     * @param path   Endpoint.
     * @param id     Group id.
     * @param token  Token.
     * @return Response.
     */
    public static Response deleteGroup(WebTarget target, String path, int id, String token) {
        return target.path(path).request().header("id", id).header("token", token).delete();
    }

    /**
     * Get the test researcher's login token.
     *
     * @param target Web target.
     * @return Login token.
     */
    public static String getResearcherLoginToken(WebTarget target) {
        Response response = login(target, "researcher/login",
                researcher1.getEmail(), PASSWORD);
        JsonObject jsonObject = getPayload(response);
        return jsonObject.getString("token");
    }

    /**
     * Extract key from an email.
     * @return string
     * @throws MessagingException Ex
     * @throws IOException Ex
     */
    public static String getKeyFromParticipantEmail() throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.pop3.host", "pop.gmail.com");
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        properties.put("mail.debug.auth", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        emailSession.setDebug(true);
        POP3Store emailStore = (POP3Store) emailSession.getStore("pop3s");

        emailStore.connect("pop.gmail.com", "sw806f18@gmail.com", "p0wer123");

        Folder emailFolder = emailStore.getFolder("INBOX");
        emailFolder.open(Folder.READ_WRITE);

        Message[] messages = emailFolder.getMessages();

        String key = null;

        for (Message message : messages) {
            if (message.getSubject().equals("invite")) {
                key = getTextFromMessage(message);
            }
        }

        for (Message message : messages) {
            message.setFlag(Flags.Flag.DELETED, true);
        }

        emailFolder.expunge();

        return key;
    }

    private static String getTextFromMessage(Message message) throws IOException,
                                                                     MessagingException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws IOException, MessagingException {

        int count = mimeMultipart.getCount();
        if (count == 0) {
            throw new MessagingException("Multipart with no body parts not supported.");
        }
        boolean multipartAlt = new ContentType(mimeMultipart.getContentType())
                                                .match("multipart/alternative");
        if (multipartAlt) {
            return getTextFromBodyPart(mimeMultipart.getBodyPart(count - 1));
        }
        String result = "";
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            result += getTextFromBodyPart(bodyPart);
        }
        return result;
    }

    private static String getTextFromBodyPart(
            BodyPart bodyPart) throws IOException, MessagingException {

        String result = "";
        if (bodyPart.isMimeType("text/plain")) {
            result = (String) bodyPart.getContent();
        } else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            result = org.jsoup.Jsoup.parse(html).text();
        } else if (bodyPart.getContent() instanceof MimeMultipart) {
            result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        return result;
    }

    /**
     * Create a connection to the database.
     * @return Connection
     * @throws SQLException Ex
     * @throws ClassNotFoundException Ex
     */
    public static Connection createConnection() throws SQLException, ClassNotFoundException {
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

    public static void closeConnection(Connection c) throws SQLException {
        c.close();
    }

    /**
     * Reset the database completely.
     * @throws SQLException Ex
     * @throws ClassNotFoundException Ex
     * @throws IOException Ex
     */
    public static void resetDatabase() throws SQLException, ClassNotFoundException, IOException {
        Connection connection = createConnection();

        String query = "SELECT truncate_tables('postgres')";

        Statement statement = connection.createStatement();

        statement.execute(query);

        closeConnection(connection);
    }

    /**
     * Fetch testing groups.
     *
     * @return Test groups.
     */
    public static List<Group> testGroups() {
        List<Group> list = new ArrayList<>();
        list.add(group1);
        list.add(group2);
        list.add(group3);
        return list;
    }

    /**
     * @return Test surveys.
     */
    public static List<Survey> testSurveys(){
        List<Survey> results = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String title = "Test" + i;
            String description = "Description" + i;
            Survey survey = new Survey(title, description);
            survey.addQuestion(new TextQuestion(1,"Text question" + i, "Text question description" + i));

            List<String> values = new ArrayList<>();
            values.add("A" + i);
            values.add("B" + i);
            values.add("C" + i);

            survey.addQuestion(new DropdownQuestion(2,Question.Type.STRING, "Drop question" + i, "Drop question description" + i, values));
            survey.addQuestion(new NumberQuestion(3,"Number question" + 1, "Number question description" + 1));

            results.add(survey);
        }


        return results;
    }

    /**
     * Fetch testing participants.
     *
     * @return Test participants.
     */
    public static List<Participant> participants() {
        List<Participant> list = new ArrayList<>();
        list.add(participant1);
        list.add(participant2);
        return list;
    }
}
