package sw806f18.server.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import sw806f18.server.Configurations;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

@RunWith(TestRunner.class)
public class SurveyTest {
    @Test
    public void removeQuestion() throws Exception {
        Question q = TestHelpers.survey2.getQuestions().get(0);
        TestHelpers.survey2.removeQuestion(q);

        assertTrue(!TestHelpers.survey2.getQuestions().contains(q));
    }

    private Survey survey;
    private String title = "This is a survey";
    private String description = "Here we describe the survey";

    @Before
    public void setUp() throws Exception {
        survey = new Survey(title, description);
        survey.addQuestion(new TextQuestion(1, "Text question", "Text question description"));

        List<String> values = new ArrayList<>();
        values.add("A");
        values.add("B");
        values.add("C");

        survey.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
            "Drop question",
            "Drop question description", values));
        survey.addQuestion(new NumberQuestion(3, "Number question",
            "Number question description"));
        Configurations.instance = new Configurations("test-config.json");
    }

    @Test
    public void addQuestion() throws Exception {
        Question tempQuestion = new TextQuestion(5, title, description);
        survey.addQuestion(tempQuestion);

        assertTrue(survey.getQuestions().get(survey.getQuestions().size() - 1).equals(tempQuestion));
    }

    @Test
    public void addQuestionAtIndex() throws Exception {
        Question tempQuestion = new TextQuestion(5, title, description);
        survey.addQuestion(tempQuestion, 1);

        assertTrue(survey.getQuestions().get(1).equals(tempQuestion));
    }

    @Test
    public void swapQuestion() throws Exception {
        survey.moveQuestion(0, 1);

        assertTrue(survey.getQuestions().get(0).id == 2);
        assertTrue(survey.getQuestions().get(1).id == 1);
    }

    @Test
    public void getHtml() throws Exception {
        Tidy tidy = new Tidy();

        String html = survey.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        assertTrue(tidy.getParseWarnings() == 0);
        assertTrue(tidy.getParseErrors() == 0);
        assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "title").equals(title));
    }
}