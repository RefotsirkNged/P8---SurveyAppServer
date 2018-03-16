package sw806f18.server;

<<<<<<<
import com.sun.mail.pop3.POP3Store;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.Participant;

=======
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

>>>>>>>
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
<<<<<<<
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
=======

>>>>>>>

import sw806f18.server.model.Group;

public class TestHelpers {
    public static final String RESEARCHER_LOGIN_PATH = "researcher/login";
    public static final String RESEARCHER_GROUPMANAGER_PATH = "researcher/groupmanager";
    public static final String RENEW_TOKEN_PATH = "renewtoken";

    public static final String VALID_RESEARCHER_EMAIL = "researcher1@email.com";
    public static final String VALID_RESEARCHER_PASSWORD = "pypass";

    public static final String INVALID_RESEARCHER_EMAIL = "fake1@email.com";
    public static final String INVALID_RESEARCHER_PASSWORD = "fake";

    private TestHelpers() {
        // Don't instantiate me
    }

<<<<<<<
    public static JsonObject getPayload(Response response) {
=======
    /**
     * Get payload.
     * @param response Response.
     * @return Payload.
     */
    public static JsonObject getPayload(Response response) {
>>>>>>>
        String content = response.readEntity(String.class);
        JsonReader jsonReader = Json.createReader(new StringReader(content));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

<<<<<<<
    public static Response login(WebTarget target, String path, String email, String password) {
        return target.path(path).request().header("email", email).header("password", password).post(Entity.text(""));
=======
    /**
     * Login function.
     * @param target Web target.
     * @param path Endpoint.
     * @param email Email.
     * @param password Password.
     * @return Response.
     */
    public static Response login(WebTarget target, String path, String email, String password) {
        return target.path(path).request().header("email", email)
                .header("password", password).post(Entity.text(""));
>>>>>>>
    }

<<<<<<<
    public static String getResearcherLoginToken(WebTarget target) {
        Response response = login(target, "researcher/login", VALID_RESEARCHER_EMAIL, VALID_RESEARCHER_PASSWORD);
=======
    /**
     * Get all groups.
     * @param target Web target.
     * @param path Endpoint.
     * @param token Token.
     * @return All groups.
     */
    public static Response getAllGroups(WebTarget target, String path, String token) {
        return target.path(path).request().header("token", token).get();
    }

    /**
     * Add group to database.
     * @param target Web target.
     * @param path Endpoint.
     * @param name Group name
     * @param token Token.
     * @return
     */
    public static Response addGroup(WebTarget target, String path, String name, String token) {
        return target.path(path).request().header("name", name)
                .header("token", token).put(Entity.text(""));
    }

    /**
     * Delete group in the database.
     * @param target Web target.
     * @param path Endpoint.
     * @param id Group id.
     * @param token Token.
     * @return Response.
     */
    public static Response deleteGroup(WebTarget target, String path, int id, String token) {
        return target.path(path).request().header("id", id).header("token", token).delete();
    }

    /**
     * Get the test researcher's login token.
     * @param target Web target.
     * @return Login token.
     */
    public static String getResearcherLoginToken(WebTarget target) {
        Response response = login(target, "researcher/login",
                VALID_RESEARCHER_EMAIL, VALID_RESEARCHER_PASSWORD);
>>>>>>>
        JsonObject jsonObject = getPayload(response);
        return jsonObject.getString("token");
    }
<<<<<<<

    public static String getKeyFromParticipantEmail() throws MessagingException, IOException {
        Properties properties = new Properties();
        // properties.put("mail.store.protocol", "pop3");
        properties.put("mail.pop3.host", "pop.gmail.com");
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        properties.put("mail.debug.auth", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        emailSession.setDebug(true);
        POP3Store emailStore = (POP3Store) emailSession.getStore("pop3s");

        emailStore.connect("pop.gmail.com","sw806f18@gmail.com", "p0wer123");

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

    private static String getTextFromMessage(Message message) throws IOException, MessagingException {
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
        if (count == 0)
            throw new MessagingException("Multipart with no body parts not supported.");
        boolean multipartAlt = new ContentType(mimeMultipart.getContentType()).match("multipart/alternative");
        if (multipartAlt)
            // alternatives appear in an order of increasing
            // faithfulness to the original content. Customize as req'd.
            return getTextFromBodyPart(mimeMultipart.getBodyPart(count - 1));
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
        } else if (bodyPart.getContent() instanceof MimeMultipart){
            result = getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
        }
        return result;
    }

    public static void populateDatabase() throws CreateUserException, CreateInviteException {
        Participant participant1 = new Participant(-1, "test@testesen.dk", "0123456789");
        Database.createParticipant(participant1, "power123");
        Database.createInvite("0123456789", "abc");
    }

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://" + Configurations.instance.postgresIp() + ":" + Configurations.instance.postgresPort() + "/" + Configurations.instance.postgresDatabase(),
                        Configurations.instance.postgresUser(), Configurations.instance.postgresPassword());
        return c;
    }

    public static void closeConnection(Connection c) throws SQLException {
        c.close();
    }

    public static void resetDatabase() throws SQLException, ClassNotFoundException, IOException {
        Connection connection = createConnection();

        String query = "SELECT truncate_tables('postgres')";

        Statement statement = connection.createStatement();

        statement.execute(query);

        closeConnection(connection);
    }
=======

    /**
     * Fetch testing groups.
     * @return Test groups.
     */
    public static List<Group> testGroups() {
        List<Group> list = new ArrayList<>();
        list.add(new Group(1, "Group 1", 0));
        list.add(new Group(2, "Group 2", 0));
        list.add(new Group(3, "Group 3", 0));
        return list;
    }
>>>>>>>
}
