package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import sw806f18.server.Constants;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.exceptions.NotImplementedException;
import sw806f18.server.model.Question;
import sw806f18.server.model.Survey;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunner.class)
public class SurveyResourceTest {
    @Test
    public void deleteQuestionFromSurvey() throws Exception {
        Response response = TestListener.target
                .path("survey")
                .path(Integer.toString(TestHelpers.survey2.getId()))
                .path("question")
                .path(Integer.toString(TestHelpers.survey2.getQuestions().get(0).getId()))
                .request()
                .delete();

        assertEquals(response.getStatus(), 200);
    }

    @Test
    public void surveySubmittedWithError() throws ParserConfigurationException, NotImplementedException {
        Tidy tidy = new Tidy();
        MultivaluedMap<String,String> form = new MultivaluedHashMap<>();

        Survey survey = TestHelpers.survey2;
        String answer1 = "This is a test";
        String answer2 = "123a";
        String answer3 = "123";

        for (Question q : survey.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    form.add(q.getHtmlID(), answer1);
                    q.setValue(answer1);
                    break;
                case NUMBER:
                    form.add(q.getHtmlID(), answer2);
                    q.setValue(answer2);
                    break;
                case DROPDOWN:
                    form.add(q.getHtmlID(), answer3);
                    q.setValue(answer3);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }


        Response response = TestListener.target.path("survey").path(Integer.toString(TestHelpers.survey2.getId()))
            .request()
            .post(Entity.form(form));

        assertEquals(response.getStatus(), 200);
        String entity = response.readEntity(String.class);
        assertTrue(!entity.equals(SurveyResource.getReturnHTML(Constants.hubUrl)));

        InputStream stream = new ByteArrayInputStream(entity.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        for (Node node : TestHelpers.getListOfHTMLNodesFromTag(htmlDoc, "div", "class", "question")) {
            boolean valueFound = false;
            for (Question q : survey.getQuestions()) {
                switch (q.getInput()) {
                    case TEXT:
                        if (TestHelpers.getHTMLNodeFromTag(node, "textarea") != null
                                && q.getValue().equals(
                                        TestHelpers.getHTMLTagData(TestHelpers.getHTMLNodeFromTag(node, "textarea")))) {
                            valueFound = true;
                        }
                        break;
                    case NUMBER:
                        if (TestHelpers.getHTMLNodeFromTag(node, "input") != null
                                && q.getValue().equals(
                                        ((Element)TestHelpers.getHTMLNodeFromTag(node, "input"))
                                                .getAttribute("value"))) {
                            valueFound = true;
                        }
                        break;
                    case DROPDOWN:
                        if (TestHelpers.getHTMLNodeFromTag(node, "select") != null
                                && q.getValue().equals(
                                        TestHelpers.getHTMLDocAttribute(
                                                TestHelpers.getHTMLNodeFromTagAndAttribute(
                                                        TestHelpers.getHTMLNodeFromTag(node, "select"),
                                                        "option",
                                                        "selected",
                                                        "selected"),
                                                "value"))) {
                            valueFound = true;
                        }
                        break;
                    default:
                        throw new NotImplementedException("Test for input type not defined");
                }

                if (valueFound) {
                    break;
                }
            }

            assertTrue(valueFound);
        }
    }

    @Test
    public void surveySubmittedWithoutError() throws InterruptedException, NotImplementedException {
        MultivaluedMap<String,String> form = new MultivaluedHashMap<>();
        for (Question q : TestHelpers.survey2.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    form.add(q.getHtmlID(), "This is a test");
                    break;
                case NUMBER:
                    form.add(q.getHtmlID(), "123");
                    break;
                case DROPDOWN:
                    form.add(q.getHtmlID(), "123");
                    break;
                default:
                    throw new NotImplementedException("Test for input type not defined");
            }
        }


        Response response = TestListener.target.path("survey").path(Integer.toString(TestHelpers.survey2.getId()))
            .request()
            .post(Entity.form(form));

        assertEquals(response.getStatus(), 200);
        assertEquals(response.readEntity(String.class), SurveyResource.getReturnHTML(Constants.hubUrl));
    }
}
