package sw806f18.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.pop3.POP3Store;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.*;
import sw806f18.server.model.*;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class TestHelpers {
    public static final String RESEARCHER_LOGIN_PATH = "researcher/login";
    public static final String RESEARCHER_GROUPMANAGER_PATH = "researcher/groupmanager";
    public static final String RESEARCHER_GROUPMANAGER_MEMBER_PATH = "researcher/groupmanager/member";
    public static final String RESEARCHER_PARTICIPANT_PATH = "researcher/participant";
    public static final String RESEARCHER_PARTICIPANT_ALL_PATH = "researcher/participant/all";
    public static final String RESEARCHER_GROUPMANAGER_LINK_PATH = "researcher/groupmanager/link";
    public static final String RESEARCHER_GROUPMANAGER_SURVEYS_PATH = "researcher/groupmanager/surveys";

    public static final String PARTICIPANT_PATH = "participant";
    public static final String PARTICIPANT_LOGIN_PATH = "participant/login";
    public static final String PARTICIPANT_HUB_PATH = "participant/hub";
    public static final String PARTICIPANT_HUB_MODULES_PATH = "participant/hub/modules";
    public static final String GROUP_PATH = "group";
    public static final String SURVEY_PATH = "survey";

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
    public static Survey survey1 = new Survey("Test Title", "This is a survey for testing tortoises");

    public static final Invite invite1 = new Invite("0011223344", "qwerty");
    public static final Invite inviteCreate = new Invite("4433221100", "asdfgh");

    public static Survey survey2 = new Survey("Test survey 2", "");
    public static Survey survey3 = new Survey("Test survey 3", "");
    public static Survey surveyAnswer1 = new Survey("Test Answers survey", "This is a test of some answer");

    public static Hub hub1 = new Hub();
    public static Hub hub2 = new Hub();
    public static Hub hub3 = new Hub();

    /**
     * Populates database with test data.
     *
     * @throws CreateUserException     Exception.
     * @throws CreateInviteException   Exception.
     * @throws AddGroupException       Exception.
     * @throws AddGroupMemberException Exception.
     * @throws P8Exception             Exception.
     */
    public static void populateDatabase() throws
        P8Exception, SQLException, IOException, ClassNotFoundException {
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
        survey2.addQuestion(new NumberQuestion(3, "Alkohol",
            "Hvor mange genstande drikker du om ugen:"));
        survey2.addQuestion(new TextQuestion(3, "Rygning",
            "Hvor mange cigaretter ryger du om dagen:"));

        List<String> bistrolStoolChart = new ArrayList<>();
        bistrolStoolChart.add("123");

        survey2.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Afføring",
                "Hvordan vil du beskrive din afføring efter et gennemsnitligt toiletbesøg:",
                bistrolStoolChart));

        surveyAnswer1.addQuestion(new NumberQuestion(3, "Alkohol",
                "Hvor mange genstande drikker du om ugen:"));
        surveyAnswer1.addQuestion(new TextQuestion(3, "Rygning",
                "Hvor mange cigaretter ryger du om dagen:"));

        surveyAnswer1.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Afføring",
                "Hvordan vil du beskrive din afføring efter et gennemsnitligt toiletbesøg:",
                bistrolStoolChart));

        String answer1 = "1";
        String answer2 = "123a";
        String answer3 = "123";

        for (Question q : surveyAnswer1.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    q.setValue(answer1);
                    break;
                case NUMBER:
                    q.setValue(answer2);
                    break;
                case DROPDOWN:
                    q.setValue(answer3);
                    break;
                default:
                    break;
            }
        }

        survey1.setId(Database.addSurvey(survey1));
        survey2.setId(Database.addSurvey(survey2));
        survey3.setId(Database.addSurvey(survey3));

        hub2.setModules(new ArrayList<>());
        hub2.addModule(survey1);

        Database.addGroupMember(group1, participant1);
        TestHelpers.addDefaultHub();
        Database.linkModuleToGroup(survey1.getId(), group1.getId());
        Database.linkModuleToGroup(survey2.getId(), group1.getId());
    }

    private TestHelpers() {
        // Don't instantiate me
    }

    /**
     * Get payload in JSON format.
     *
     * @param connection An open connection to the server
     * @return Payload.
     */
    public static JsonNode getJsonPayload(HttpURLConnection connection) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(getStringPayload(connection));
    }

    /**
     * Get payload in String format.
     * @param connection An open connection to the server.
     * @return Payload.
     * @throws IOException
     */
    public static String getStringPayload(HttpURLConnection connection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();

        return result.toString();
    }

    /**
     * Login function.
     *
     * @param path     Endpoint.
     * @param email    Email.
     * @param password Password.
     * @return An open connection to the server.
     */
    public static HttpURLConnection login(String path, String email, String password) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        return getHttpConnection(path, "POST", null, map, null, null);
    }

    /**
     * Add group member.
     *
     * @param path        Path.
     * @param participant Participant.
     * @param group       Group.
     * @param token       Token.
     * @return Response.
     */
    public static HttpURLConnection addGroupMember(
        String path,
        Participant participant,
        Group group,
        String token
    ) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupID", Integer.toString(group.getId()));
        map.put("userID", Integer.toString(participant.getId()));
        return getHttpConnection(path, "PUT", token, map, null, null);
    }

    /**
     * Get group members.
     *
     * @param path  Path.
     * @param group Group.
     * @param token Token.
     * @return Response.
     */
    public static HttpURLConnection getGroupMembers(String path, Group group, String token) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupID", Integer.toString(group.getId()));
        return getHttpConnection(path, "GET", token, map, null, null);
    }

    /**
     * Get group members.
     *
     * @param path   Path.
     * @param moduleId  Module.
     * @param token  Token.
     * @return Response.
     */
    public static HttpURLConnection removeGroupLink(String path, int moduleId, String token) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("module", Integer.toString(moduleId));
        return getHttpConnection(path, "POST", token, map, null, null);
    }

    /**
     * Get with a token.
     *
     * @param path  Path.
     * @param token Token.
     * @return Response.
     */
    public static HttpURLConnection getWithToken(String path, String token) throws IOException {
        return getHttpConnection(path, "GET", token, null, null, null);
    }

    /**
     * Delete with a token.
     *
     * @param path  Path.
     * @param token Token.
     * @return Response.
     */
    public static HttpURLConnection deleteWithToken(String path, String token) throws IOException {
        return getHttpConnection(path, "DELETE", token, null, null, null);
    }

    /**
     * Get hub.
     *
     * @param path  Path.
     * @param token Token.
     * @return Response.
     */
    public static HttpURLConnection getHub(String path, String token) throws IOException {
        return getHttpConnection(path, "GET", token, null, null, null);
    }

    /**
     * Get HTML from resource.
     *
     * @param path   Path.
     * @return Response.
     */
    public static String getHTML(String path) throws IOException {
        return getStringPayload(getHttpConnection(path, "GET", null, null, null, null));
    }

    /**
     * Remove group member.
     *
     * @param path        Path.
     * @param participant Participant.
     * @param group       Group.
     * @param token       Token.
     */
    public static HttpURLConnection removeGroupMember(
        String path,
        Participant participant,
        Group group,
        String token
    ) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupID", Integer.toString(group.getId()));
        map.put("userID", Integer.toString(participant.getId()));
        return getHttpConnection(path, "DELETE", token, map, null, null);
    }

    /**
     * Link module to survey request.
     *
     * @param path
     * @param surveyID
     * @param token
     * @return
     */
    public static HttpURLConnection linkModuleToSurvey(String path,
                                                       int surveyID, String token) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("module", Integer.toString(surveyID));
        return getHttpConnection(path, "PUT", token, map, null, null);
    }

    /**
     * Link module to survey request.
     *
     * @param path Path.
     * @param cpr CPR.
     * @param email Email.
     * @param token Token.
     * @return
     */
    public static HttpURLConnection inviteParticipant(String path,
                                                       String cpr, String email, String token) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("cpr", cpr);
        map.put("email", email);
        return getHttpConnection(path, "POST", token, map, null, null);
    }

    /**
     * Create Participant.
     * @param path Path.
     * @param cpr cpr.
     * @param email Email.
     * @param firstname Firstname.
     * @param lastname Lastname.
     * @return Connection.
     * @throws IOException IOException.
     */
    public static HttpURLConnection createParticipant(String path, String cpr, String email,
            String firstname, String lastname) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("cpr", cpr);
        map.put("email", email);
        map.put("firstname", firstname);
        map.put("lastname", lastname);
        return getHttpConnection(path, "POST", null, map, null, null);
    }

    /**
     * Add group to database.
     *
     * @param path  Endpoint.
     * @param name  Group name
     * @param token Token.
     * @return
     */
    public static HttpURLConnection addGroup(String path, String name, String token) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        return getHttpConnection(path, "PUT", token, map, null, null);
    }

    /**
     * Delete group in the database.
     *
     * @param path  Endpoint.
     * @param id    Group id.
     * @param token Token.
     * @return Response.
     */
    public static HttpURLConnection deleteGroup(String path, int id, String token) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", Integer.toString(id));
        return getHttpConnection(path, "DELETE", token, map, null, null);
    }

    /**
     * Delete group in the database.
     *
     * @param path  Endpoint.
     * @param token Token.
     * @return Response.
     */
    public static HttpURLConnection getModulesByUser(String path, String token) throws IOException {
        return getHttpConnection(path, "GET", token, null, null, null);
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
            if (message.getSubject().equals("Join MERSY")) {
                key = getTextFromMessage(message);
            }
        }

        for (Message message : messages) {
            message.setFlag(Flags.Flag.DELETED, true);
        }

        emailFolder.close(true);

        return key.replace("\r\n", "");
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
     *
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

            survey.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
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
     *
     * @param list        List.
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

    /**
     * Get content of attribute from a tag.
     *
     * @param doc       Html document.
     * @param tag       tag to find content of.
     * @param attribute attribute to return.
     * @return
     */
    public static String getHTMLTagAttribute(Document doc, String tag, String attribute) {
        NodeList nodes = doc.getElementsByTagName(tag);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element) nodes.item(i);
            NodeList children = ele.getChildNodes();

            return ele.getAttribute(attribute);
        }
        return "-1";
    }

    /**
     * Gets the first HTML node with a tag.
     *
     * @param node Parent node.
     * @param tag  Tag to look for.
     * @return Node corresponding to parameteres.
     */
    public static Node getHTMLNodeFromTag(Node node, String tag) {
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element) nodes.item(i);

            if (ele.getTagName().equals(tag)) {
                return ele;
            }
        }
        return null;
    }

    /**
     * Gets HTML node from tags and attributes.
     *
     * @param node           Parent node.
     * @param tag            Tag to look for.
     * @param attribute      Attribute to look for.
     * @param attributeValue Attribute value to look for.
     * @return Node corresponding to parameteres.
     */
    public static Node getHTMLNodeFromTagAndAttribute(Node node, String tag, String attribute, String attributeValue) {
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element) nodes.item(i);

            if (ele.getTagName().equals(tag) && ele.getAttribute(attribute).equals(attributeValue)) {
                return ele;
            }
        }
        return null;
    }

    /**
     * Get nodes from a tag where attribute is equels to a value.
     *
     * @param doc            Html document.
     * @param tag            tag to find content of.
     * @param attribute      attribute to check for.
     * @param attributeValue value to check for.
     * @return
     */
    public static List<Node> getListOfHTMLNodesFromTag(
        Document doc,
        String tag,
        String attribute,
        String attributeValue) {

        NodeList nodes = doc.getElementsByTagName(tag);
        List<Node> results = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (getHTMLDocAttribute(nodes.item(i), "class").equals(attributeValue)) {
                results.add(nodes.item(i));
            }
        }

        return results;
    }

    /**
     * Get an attribute from a HTML document.
     *
     * @param node
     * @param attribute
     * @return
     */
    public static String getHTMLDocAttribute(Node node, String attribute) {
        return ((Element) node).getAttribute(attribute);

    }

    /**
     * Get content of HTML tag.
     *
     * @param doc Html document.
     * @param tag Tag to return content from.
     * @return Returns content of HTML tag.
     */
    public static String getHTMLTagData(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element) nodes.item(i);
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

    /**
     * Get content of HTML tag from node.
     *
     * @param node Node from which to find data.
     * @return value.
     */
    public static String getHTMLTagData(Node node) {
        Element ele = (Element) node;
        NodeList children = ele.getChildNodes();

        for (int j = 0; j < children.getLength(); j++) {
            if (children.item(j).getNodeType() == Node.TEXT_NODE) {
                org.w3c.tidy.DOMTextImpl tempEle = (org.w3c.tidy.DOMTextImpl) children.item(j);
                return tempEle.getData();
            }

        }

        return "-1";
    }

    /**
     * Create a connection to the specified server.
     *
     * @param url         The relative URI of the resource
     * @param method      http method (POST/PUT/GET/DELETE)
     * @param token       The user auth token to send with the request.
     *                    Can be null if authentication isn't required.
     * @param headers     Headers that should be added to the request. Can be null.
     * @param contentType The content type of the request.
     * @param body        Any body content that can be added to the request. Can be null.
     * @return An open conenction to the server.
     * @throws IOException
     */
    public static HttpURLConnection getHttpConnection(
        String url,
        String method,
        String token,
        Map<String, String> headers,
        String contentType, String body
    ) throws IOException {
        URL eurl = new URL("http://localhost:8081/api/" + url);
        HttpURLConnection connection = (HttpURLConnection) eurl.openConnection();
        connection.setRequestMethod(method);

        if (token != null) {
            connection.setRequestProperty("Cookie", "token=" + token + ";");
        }

        if (headers != null) {
            connection.setDoOutput(true);
            headers.forEach(connection::setRequestProperty);
        }

        if (contentType != null) {
            connection.setRequestProperty("Content-Type", contentType);
        }

        if (body != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }

        return connection;
    }
}
