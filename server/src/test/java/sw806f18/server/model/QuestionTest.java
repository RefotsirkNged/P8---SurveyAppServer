package sw806f18.server.model;

import com.sun.tools.javac.util.Convert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import javax.validation.constraints.AssertTrue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class QuestionTest {
    private Tidy tidy;

    @Before
    public void setUp() throws Exception
    {
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

        //assertTrue(tidy.getParseErrors() == 0);
        //assertTrue(tidy.getParseWarnings() == 0);
        assertTrue(getHTMLTagData(htmlDoc, "h4").equals(title));
        assertTrue(getHTMLTagData(htmlDoc, "p").equals(description));
        assertTrue(getHTMLTagAttribute(htmlDoc, "input", "id").equals(title.replace(' ', '_')));
        assertTrue(getHTMLTagAttribute(htmlDoc, "input", "type").equals("text"));
    }

    @Test
    public void getHTMLDropdownQuestion() throws Exception {
        String title = "Test text question";
        String description = "This is a text question";
        ArrayList<String> values = new ArrayList<String>();

        values.add("A");
        values.add("B");
        values.add("C");

        Question question = new DropdownQuestion(-1, Question.Type.STRING, title, description, values);
        String html = question.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        NodeList options = htmlDoc.getElementsByTagName("option");

        for (int i = 0; i < options.getLength(); i++) {
            assertTrue(values.get(i).equals(getHTMLDocAttribute(options.item(i),"value")));
        }

        //assertTrue(tidy.getParseErrors() == 0);
        //assertTrue(tidy.getParseWarnings() == 0);
        assertTrue(getHTMLTagData(htmlDoc, "h4").equals(title));
        assertTrue(getHTMLTagData(htmlDoc, "p").equals(description));
        assertTrue(getHTMLTagAttribute(htmlDoc, "select", "id").equals(title.replace(' ', '_')));


    }

    @Test
    public void getHTMLNumberQuestion() throws Exception {
        String title = "Test text question";
        String description = "This is a text question";

        Question question = new NumberQuestion(1, title, description);
        String html = question.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        //assertTrue(tidy.getParseErrors() == 0);
        //assertTrue(tidy.getParseWarnings() == 0);
        assertTrue(getHTMLTagData(htmlDoc, "h4").equals(title));
        assertTrue(getHTMLTagData(htmlDoc, "p").equals(description));
        assertTrue(getHTMLTagAttribute(htmlDoc, "input", "id").equals(title.replace(' ', '_')));
        assertTrue(getHTMLTagAttribute(htmlDoc, "input", "type").equals("number"));
    }


    private String getHTMLTagData(Document doc, String tag){
        NodeList nodes = doc.getElementsByTagName(tag);

        for (int i = 0; i < nodes.getLength(); i++){
            Element ele = (Element)nodes.item(i);
            NodeList children = ele.getChildNodes();

            for (int j = 0; j < children.getLength(); j++){
                if(children.item(j).getNodeType() == Node.TEXT_NODE){
                    org.w3c.tidy.DOMTextImpl tempEle = (org.w3c.tidy.DOMTextImpl) children.item(j);
                    return tempEle.getData();
                }

            }
        }

        return "-1";
    }

    private String getHTMLTagAttribute(Document doc, String tag, String attribute){
        NodeList nodes = doc.getElementsByTagName(tag);

        for (int i = 0; i < nodes.getLength(); i++){
            Element ele = (Element)nodes.item(i);
            NodeList children = ele.getChildNodes();

            return ele.getAttribute(attribute);
        }

        return "-1";
    }

    private String getHTMLDocAttribute(Node node, String attribute){
        return ((Element)node).getAttribute(attribute);

        //return "-1";
    }
}