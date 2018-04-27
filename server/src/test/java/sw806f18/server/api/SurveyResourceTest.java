package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import sw806f18.server.Constants;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.NotImplementedException;
import sw806f18.server.model.Question;
import sw806f18.server.model.Survey;
import sw806f18.server.model.TextQuestion;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class SurveyResourceTest {
    @Test
    public void deleteQuestionFromSurvey() throws Exception {
        HttpURLConnection response = TestHelpers.deleteWithToken(TestHelpers.SURVEY_PATH + "/"
                        + TestHelpers.survey2.getId()
                        + "/question/" + TestHelpers.survey2.getQuestions().get(0).getId(),
                TestHelpers.tokenResearcher1);

        assertEquals(response.getResponseCode(), 200);
    }

    @Test
    public void surveySubmittedWithError() throws ParserConfigurationException, NotImplementedException {
        try {
            Tidy tidy = new Tidy();
            Map<String, String> form = new HashMap<>();

            Survey survey = TestHelpers.survey2;
            String answer1 = "This is a test";
            String answer2 = "123a";
            String answer3 = "123";

            for (Question q : survey.getQuestions()) {
                switch (q.getInput()) {
                    case TEXT:
                        form.put(q.getHtmlID(), answer1);
                        q.setValue(answer1);
                        break;
                    case NUMBER:
                        form.put(q.getHtmlID(), answer2);
                        q.setValue(answer2);
                        break;
                    case DROPDOWN:
                        form.put(q.getHtmlID(), answer3);
                        q.setValue(answer3);
                        break;
                    default:
                        assertTrue(false);
                        break;
                }
            }

            HttpURLConnection connection = TestHelpers.getHttpConnection(
                    "survey/" + TestHelpers.survey2.getId() + "/answer",
                    "POST",
                    TestHelpers.token1,
                    null,
                    "application/x-www-form-urlencoded",
                    null
            );

            connection.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            Set<String> keys = form.keySet();
            Iterator keyIter = keys.iterator();
            String content = "";
            for (int i = 0; keyIter.hasNext(); i++) {
                Object key = keyIter.next();
                if (i != 0) {
                    content += "&";
                }
                content += key + "=" + URLEncoder.encode(form.get(key), "UTF-8");
            }

            String preparedContent = Base64.getMimeEncoder().encodeToString(content.getBytes("UTF-8"));
            out.writeBytes(preparedContent);
            out.flush();
            out.close();

            assertEquals(connection.getResponseCode(), 200);

            String entity = TestHelpers.getStringPayload(connection);
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
                                    TestHelpers.getHTMLTagData(TestHelpers.getHTMLNodeFromTag(node, "textarea"))
                                            .replace("+", " "))) {
                                valueFound = true;
                            }
                            break;
                        case NUMBER:
                            if (TestHelpers.getHTMLNodeFromTag(node, "input") != null
                                    && q.getValue().equals(
                                    ((Element) TestHelpers.getHTMLNodeFromTag(node, "input"))
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
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void surveySubmittedWithoutError() throws InterruptedException, NotImplementedException {
        Map<String, String> form = new HashMap<>();
        for (Question q : TestHelpers.survey2.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    form.put(q.getHtmlID(), "This is a test");
                    break;
                case NUMBER:
                    form.put(q.getHtmlID(), "123");
                    break;
                case DROPDOWN:
                    form.put(q.getHtmlID(), "123");
                    break;
                default:
                    throw new NotImplementedException("Test for input type not defined");
            }
        }

        try {
            HttpURLConnection connection = TestHelpers.getHttpConnection(
                    "survey/" + TestHelpers.survey2.getId() + "/answer",
                    "POST",
                    TestHelpers.token1,
                    null,
                    "application/x-www-form-urlencoded",
                    null
            );
            connection.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            Set<String> keys = form.keySet();
            Iterator keyIter = keys.iterator();
            String content = "";
            for (int i = 0; keyIter.hasNext(); i++) {
                Object key = keyIter.next();
                if (i != 0) {
                    content += "&";
                }
                content += key + "=" + URLEncoder.encode(form.get(key), "UTF-8");
            }

            String preparedContent = Base64.getMimeEncoder().encodeToString(content.getBytes("UTF-8"));

            out.writeBytes(preparedContent);
            out.flush();
            out.close();

            assertEquals(connection.getResponseCode(), 200);
            assertEquals(TestHelpers.getStringPayload(connection).replace("\n", ""),
                    SurveyResource.getReturnHTML(Constants.hubUrl).replace("\n", ""));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addQuestionToSurvey() throws IOException {
        Question question = new TextQuestion("Blå Ford", "Parker Ordenligt");
        Survey survey = TestHelpers.survey2;
        survey.addQuestion(question);

        ObjectMapper objectMapper = new ObjectMapper();

        HttpURLConnection connection = TestHelpers.getHttpConnection(
                "survey/" + survey.getId() + "/question",
                "POST",
                TestHelpers.tokenResearcher1,
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(question)
        );

        assertEquals(200, connection.getResponseCode());
        Survey survey1 = Database.getSurvey(survey.getId());

        boolean contains = false;
        for (Question q : survey1.getQuestions()) {
            if (q.getTitle().equals(question.getTitle()) && q.getDescription().equals(question.getDescription())) {
                contains = true;
            }
        }
        assertTrue(contains);
    }

    @Test
    public void addEmptySurvey() throws IOException {
        HttpURLConnection connection = TestHelpers.getHttpConnection(
                "survey/",
                "POST",
                TestHelpers.tokenResearcher1,
                null,
                null,
                null
        );

        assertEquals(200, connection.getResponseCode());
        int id = Integer.parseInt(TestHelpers.getStringPayload(connection));
        Survey survey = Database.getSurvey(id);
        assertNotNull(survey);
        assertTrue(survey.getTitle().equals(""));
        assertTrue(survey.getDescription().equals(""));
        assertEquals(survey.getId(), id);
    }

    @Test
    public void updateSurveyMetadata() throws IOException {
        Survey survey = TestHelpers.survey2;
        survey.setTitle("New Title");
        survey.setDescription("New Description");

        ObjectMapper objectMapper = new ObjectMapper();
        HttpURLConnection connection = TestHelpers.getHttpConnection(
                "survey/" + survey.getId(),
                "PUT",
                TestHelpers.tokenResearcher1,
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(survey)
        );

        assertEquals(200, connection.getResponseCode());
        Survey newSurvey = Database.getSurvey(survey.getId());
        assertTrue(survey.equals(newSurvey));

    }
}