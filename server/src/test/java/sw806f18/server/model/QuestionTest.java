package sw806f18.server.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;

@RunWith(TestRunner.class)
public class QuestionTest {
    private Tidy tidy;

    @Before
    public void setUp() throws Exception {
        tidy = new Tidy();
        tidy.setXHTML(true);
    }

    @Test
    public void getHTMLTextQuestion() throws Exception {
        String title = "Test text question";
        String description = "This is a text question";

        Question question = new TextQuestion(1, title, description);
        String html = question.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "h4").equals(title));
        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "p").equals(description));
        Assert.assertTrue(TestHelpers.getHTMLTagAttribute(htmlDoc, "textarea", "name")
                                              .equals(title.replace(' ', '_')));
        Assert.assertTrue(TestHelpers.getHTMLTagAttribute(htmlDoc, "textarea", "type").equals("text"));
    }

    @Test
    public void getHTMLDropdownQuestion() throws Exception {
        String title = "Test text question";
        String description = "This is a text question";
        ArrayList<String> values = new ArrayList<String>();

        values.add("A");
        values.add("B");
        values.add("C");

        Question question = new DropdownQuestion(-1,
            Question.Type.STRING,
            title,
            description,
            values);
        String html = question.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        NodeList options = htmlDoc.getElementsByTagName("option");

        for (int i = 0; i < options.getLength(); i++) {
            Assert.assertTrue(values.get(i).equals(TestHelpers.getHTMLDocAttribute(options.item(i),"value")));
        }

        //assertTrue(tidy.getParseErrors() == 0);
        //assertTrue(tidy.getParseWarnings() == 0);
        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "h4").equals(title));
        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "p").equals(description));
        Assert.assertTrue(TestHelpers.getHTMLTagAttribute(htmlDoc, "select", "name")
                                              .equals(title.replace(' ', '_')));


    }

    @Test
    public void getHTMLNumberQuestion() throws Exception {
        String title = "Test text question";
        String description = "This is a text question";

        Question question = new NumberQuestion(1, title, description);
        String html = question.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "h4").equals(title));
        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "p").equals(description));
        Assert.assertTrue(TestHelpers.getHTMLTagAttribute(htmlDoc, "input", "name")
                .equals(title.replace(' ', '_')));
        Assert.assertTrue(TestHelpers.getHTMLTagAttribute(htmlDoc, "input", "type").equals("number"));
    }
}