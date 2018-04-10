package sw806f18.server;

import com.sun.mail.pop3.POP3Store;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import sw806f18.server.database.Database;
import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.AddGroupMemberException;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.DropdownQuestion;
import sw806f18.server.model.Group;
import sw806f18.server.model.Invite;
import sw806f18.server.model.NumberQuestion;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Question;
import sw806f18.server.model.Researcher;
import sw806f18.server.model.Survey;
import sw806f18.server.model.TextQuestion;

public class TestHelpers {
    public static final String RESEARCHER_LOGIN_PATH = "researcher/login";
    public static final String RESEARCHER_GROUPMANAGER_PATH = "researcher/groupmanager";
    public static final String RESEARCHER_GROUPMANAGER_MEMBER_PATH = "researcher/groupmanager/member";
    public static final String RESEARCHER_PARTICIPANT_PATH = "researcher/participant";
    public static final String RESEARCHER_PARTICIPANT_ALL_PATH = "researcher/participant/all";
    public static final String SURVEY_PATH = "survey";

    public static final String PARTICIPANT_LOGIN_PATH = "participant/login";
    public static final String PARTICIPANT_HUB_PATH = "participant/hub";
    public static final String PARTICIPANT_HUB_MODULES_PATH = "participant/hub/modules";

    public static final String PASSWORD = "power123";
    public static final String INVALID_EMAIL = "fake1@email.com";
    public static final String INVALID_PASSWORD = "fake";

    public static Researcher researcher1 =
            new Researcher("res1@earch.er", "88888888", "res", "earch");
    public static String tokenResearcher1;
    public static Researcher researcherCreate =
            new Researcher("test@testington.com", "50505050", "test", "test");

    public static Participant participant1 =
            new Participant(-1, "test1@testesen.dk", "0123456789", "partic", "ipant1", 0);
    public static String token1;
    public static Participant participant2 =
            new Participant(-1, "test2@testesen.dk", "0123456780", "name", "one", 0);
    public static String token2;
    public static Participant participantCreate =
            new Participant(-1, "test3@testesen.dk", "0123456798", "test", "test", 0);

    public static Group group1 = new Group("Group 1", 0);
    public static Group group2 = new Group("Group 2", 0);
    public static Group group3 = new Group("Group 3", 0);
    public static Group groupCreate = new Group("TestGroup", 0);
    public static Survey survey1 = new Survey("Test Title", "This is a survey for testing porpesses");

    public static final Invite invite1 = new Invite("0011223344", "qwerty");
    public static final Invite inviteCreate = new Invite("4433221100", "asdfgh");

    public static Survey survey2 = new Survey("Test survey 2", "");
    public static Survey survey3 = new Survey("Test survey 3", "");

    public static Hub hub1 = new Hub();
    public static Hub hub2 = new Hub();
    public static Hub hub3 = new Hub();

    /**
     * Populates database with test data.
     * @throws CreateUserException Exception.
     * @throws CreateInviteException Exception.
     * @throws AddGroupException Exception.
     * @throws AddGroupMemberException Exception.
     * @throws P8Exception Exception.
     */
    public static void populateDatabase() throws
            P8Exception, SQLException {
        // Create researchers
        researcher1 = Database.createResearcher(researcher1, PASSWORD);
        tokenResearcher1 = Authentication.instance.getToken(researcher1.getId());

        // Create invites
        Database.createInvite(invite1);

        hub1.setStyle(new HashMap<>());
        hub2.setStyle(new HashMap<>());
        hub3.setStyle(new HashMap<>());
        hub1.addStyleProperty(Hub.Input.CARD, "background", "#FFF");
        hub1.addStyleProperty(Hub.Input.CARD, "border", "1px solid #0a0a0a");
        hub1.addStyleProperty(Hub.Input.BODY, "background-color", "#000");

        hub2.addStyleProperty(Hub.Input.CARD, "background", "#FFF");
        hub2.addStyleProperty(Hub.Input.CARD, "border", "1px solid #0a0a0a");
        hub2.addStyleProperty(Hub.Input.BODY, "background-color", "#000");

        hub3.addStyleProperty(Hub.Input.CARD, "background", "#FFF");

        hub1 = Database.addHub(hub1);
        group1.setHub(hub1.getId());
        group2.setHub(hub1.getId());
        group3.setHub(hub1.getId());
        groupCreate.setHub(hub1.getId());

        group1 = Database.addGroup(group1);
        group2 = Database.addGroup(group2);
        group3 = Database.addGroup(group3);

        // Create test participants
        participant1.setPrimaryGroup(group1.getId());
        participant2.setPrimaryGroup(group2.getId());

        participant1 = Database.createParticipant(participant1, PASSWORD);
        token1 = Authentication.instance.getToken(participant1.getId());
        participant2 = Database.createParticipant(participant2, PASSWORD);
        token2 = Authentication.instance.getToken(participant2.getId());

        survey2 = new Survey("Spørgeskema under graviditetsforløb",
                "Dette spørgeskema indholder spørgsmål vedrørende din livsstil og dit helbred.");
        survey2.addQuestion(new NumberQuestion(3,"Alkohol",
                "Hvor mange genstande drikker du om ugen:"));
        survey2.addQuestion(new TextQuestion(3,"Rygning",
                "Hvor mange cigaretter ryger du om dagen:"));
        List<String> bistrolStoolChart = new ArrayList<>();
        bistrolStoolChart.add("123");

        survey2.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Afføring",
                "Hvordan vil du beskrive din afføring efter et gennemsnitligt toiletbesøg:",
                bistrolStoolChart));

        survey1.setId(Database.addSurvey(survey1));
        survey2.setId(Database.addSurvey(survey2));

        hub2.setModules(new ArrayList<>());
        hub2.addModule(survey1);

        Database.addGroupMember(group1, participant1);
        Database.linkModuleToGroup(survey1, group1);
        Database.linkModuleToGroup(survey2, group1);
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
     * Add group member.
     *
     * @param target      Target.
     * @param path        Path.
     * @param participant Participant.
     * @param group       Group.
     * @param token       Token.
     * @return Response.
     */
    public static Response addGroupMember(WebTarget target, String path,
                                          Participant participant, Group group, String token) {
        return target.path(path).request().header("groupID", group.getId())
            .header("userID", participant.getId()).header("token", token).put(Entity.text(""));
    }

    /**
     * Get group members.
     *
     * @param target Target.
     * @param path   Path.
     * @param group  Group.
     * @param token  Token.
     * @return Response.
     */
    public static Response getGroupMembers(WebTarget target, String path, Group group, String token) {
        return target.path(path).request().header("groupID", group.getId())
            .header("token", token).get();
    }

    /**
     * Get all participants.
     *
     * @param target Target.
     * @param path   Path.
     * @param token  Token.
     * @return Response.
     */
    public static Response getAll(WebTarget target, String path, String token) {
        return target.path(path).request().header("token", token).get();
    }

    /**
     * Get hub.
     *
     * @param target Target.
     * @param path   Path.
     * @param token  Token.
     * @return Response.
     */
    public static String getHub(WebTarget target, String path, String token) {
        return target.path(path + "/" + token).request().get(String.class);
    }

    /**
     * Remove group member.
     *
     * @param target      Target.
     * @param path        Path.
     * @param participant Participant.
     * @param group       Group.
     * @param token       Token.
     */
    public static Response removeGroupMember(WebTarget target, String path,
                                             Participant participant, Group group, String token) {
        return target.path(path).request().header("groupID", group.getId())
            .header("userID", participant.getId()).header("token", token).delete();
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
     * Delete group in the database.
     *
     * @param target Web target.
     * @param path   Endpoint.
     * @param token  Token.
     * @return Response.
     */
    public static Response getModulesByUser(WebTarget target, String path, String token) {
        return target.path(path).request().header("token", token).get();
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
     *
     * @return string
     * @throws MessagingException Ex
     * @throws IOException        Ex
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

        emailFolder.close(true);

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
     *
     * @return Connection
     * @throws SQLException           Ex
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
     *
     * @throws SQLException           Ex
     * @throws ClassNotFoundException Ex
     * @throws IOException            Ex
     */
    public static void resetDatabase() throws SQLException, ClassNotFoundException, IOException {
        Connection connection = createConnection();

        String query = "SELECT truncate_tables('postgres')";

        Statement statement = connection.createStatement();

        statement.execute(query);

        closeConnection(connection);

        Database.cleanMongoDB();
    }

    /**
     * Add default hub.
     *
     * @throws SQLException           Ex
     * @throws ClassNotFoundException Ex
     * @throws IOException            Ex
     */
    public static void addDefaultHub() throws SQLException, ClassNotFoundException, IOException {
        Connection connection = createConnection();

        String query = "INSERT INTO hubs(id) VALUES (0)";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
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
     * Generates surveys for test database.
     * @return Test surveys.
     */
    public static List<Survey> testSurveys() {
        List<Survey> results = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String title = "Test" + i;
            String description = "Description" + i;
            Survey survey = new Survey(title, description);
            survey.addQuestion(new TextQuestion(1,
                                                "Text question" + i,
                                                "Text question description" + i));

            List<String> values = new ArrayList<>();
            values.add("A" + i);
            values.add("B" + i);
            values.add("C" + i);

            survey.addQuestion(new DropdownQuestion(2,Question.Type.STRING,
                                                    "Drop question" + i,
                                                    "Drop question description" + i, values));
            survey.addQuestion(new NumberQuestion(3,
                                                  "Number question" + 1,
                                                  "Number question description" + 1));

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

    /**
     * Check if a list of participants contains a specific without mail.
     * @param list List.
     * @param participant Participant.
     * @return True is list contains. false otherwise.
     */
    public static boolean containsNoMail(List<Participant> list, Participant participant) {
        for (Participant p : list) {
            if (p.equalsNoMail(participant)) {
                return true;
            }
        }
        return false;
    }


    public static String getHTMLTagData(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element)nodes.item(i);
            NodeList children = ele.getChildNodes();

            for (int j = 0; j < children.getLength(); j++) {
                if (children.item(j).getNodeType() == Node.TEXT_NODE) {
                    org.w3c.tidy.DOMTextImpl tempEle = (org.w3c.tidy.DOMTextImpl) children.item(j);
                    return tempEle.getData();
                }

            }
        }

        return "-1";
    }

    public static String getHTMLTagAttribute(Document doc, String tag, String attribute) {
        NodeList nodes = doc.getElementsByTagName(tag);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element)nodes.item(i);
            NodeList children = ele.getChildNodes();

            return ele.getAttribute(attribute);
        }
        return "-1";
    }

    public static String getHTMLDocAttribute(Node node, String attribute) {
        return ((Element)node).getAttribute(attribute);

    }
}
