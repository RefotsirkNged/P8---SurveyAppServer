package sw806f18.server.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import sw806f18.server.Authentication;
import sw806f18.server.Configurations;
import sw806f18.server.Main;
import sw806f18.server.TestHelpers;
import sw806f18.server.exceptions.LinkException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static sw806f18.server.TestHelpers.createConnection;
import static sw806f18.server.TestHelpers.survey1;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class SurveyTest {
    private Survey survey;
    private String title = "This is a survey";
    private String description = "Here we describe the survey";

    private HttpServer server;
    private WebTarget target;
    private String token;

    @Before
    public void setUp() throws Exception {
        survey = new Survey(title, description);
        survey.addQuestion(new TextQuestion(1,"Text question", "Text question description"));

        List<String> values = new ArrayList<>();
        values.add("A");
        values.add("B");
        values.add("C");

        survey.addQuestion(new DropdownQuestion(2,Question.Type.STRING,
                "Drop question",
                "Drop question description", values));
        survey.addQuestion(new NumberQuestion(3,"Number question",
                "Number question description"));
        Configurations.instance = new Configurations("test-config.json");
        token = Authentication.instance.getToken(TestHelpers.researcher1.getId());
        server = Main.startServer();
        Client c = ClientBuilder.newClient();


        target = c.target(Main.BASE_URI);
        TestHelpers.resetDatabase();
        TestHelpers.populateDatabase();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        TestHelpers.resetDatabase();
    }

    @Test
    public void addQuestion() throws Exception {
        Question tempQuestion = new TextQuestion(5, title, description);
        survey.addQuestion(tempQuestion);

        Assert.assertTrue(survey.getQuestions().get(survey.getQuestions().size() - 1).equals(tempQuestion));
    }

    @Test
    public void addQuestionAtIndex() throws Exception {
        Question tempQuestion = new TextQuestion(5, title, description);
        survey.addQuestion(tempQuestion, 1);

        Assert.assertTrue(survey.getQuestions().get(1).equals(tempQuestion));
    }

    @Test
    public void swapQuestion() throws Exception {
        survey.moveQuestion(0, 1);

        Assert.assertTrue(survey.getQuestions().get(0).id == 2);
        Assert.assertTrue(survey.getQuestions().get(1).id == 1);
    }

    @Test
    public void getHtml() throws Exception {
        Tidy tidy = new Tidy();

        String html = survey.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        Assert.assertTrue(tidy.getParseErrors() == 0);
        Assert.assertTrue(tidy.getParseWarnings() == 0);
        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "title").equals(title));
    }
}