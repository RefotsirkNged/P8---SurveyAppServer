package sw806f18.server.model;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class SurveyTest {
    private Survey survey;
    private  String title = "This is a survey";
    private String description = "Here we describe the survey";


    @Before
    public void setUp() throws Exception{
        survey = new Survey(title, description);
        survey.addQuestion(new TextQuestion(1,"Text question", "Text question description"));

        List<String> values = new ArrayList<>();
        values.add("A");
        values.add("B");
        values.add("C");

        survey.addQuestion(new DropdownQuestion(2,Question.Type.STRING, "Drop question", "Drop question description", values));
        survey.addQuestion(new NumberQuestion(3,"Number question", "Number question description"));
    }

    @Test
    public void addQuestion() throws Exception {
        Question tempQuestion = new TextQuestion(5, title, description);
        survey.addQuestion(tempQuestion);

        assertTrue(survey.questions.get(survey.questions.size()-1).equals(tempQuestion));
    }

    @Test
    public void addQuestionAtIndex() throws Exception {
        Question tempQuestion = new TextQuestion(5, title, description);
        survey.addQuestion(tempQuestion, 1);

        assertTrue(survey.questions.get(1).equals(tempQuestion));
    }

    @Test
    public void swapQuestion() throws Exception {
        survey.moveQuestion(0, 1);

        assertTrue(survey.questions.get(0).id == 2);
        assertTrue(survey.questions.get(1).id == 1);
    }

    @Test
    public void getHtml() throws Exception {
        Tidy tidy = new Tidy();

        String html = survey.getHTML();

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        assertTrue(tidy.getParseErrors() == 0);
        assertTrue(tidy.getParseWarnings() == 0);
        assertTrue(getHTMLTagData(htmlDoc, "title").equals(title));
        //assertTrue(getHTMLTagData(htmlDoc, "h3").equals(description));
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