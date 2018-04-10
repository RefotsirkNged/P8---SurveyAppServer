package sw806f18.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.Constants;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.model.Question;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunner.class)
public class SurveyResourceTest {
    @Test
    public void surveySubmittedWithError() {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        for (Question q : TestHelpers.survey2.getQuestions()) {
            switch (q.getInput()) {
                case TEXT:
                    form.add(q.getHtmlID(), "This is a test");
                    break;
                case NUMBER:
                    form.add(q.getHtmlID(), "123a");
                    break;
                case DROPDOWN:
                    form.add(q.getHtmlID(), "123");
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
        assertTrue(!response.readEntity(String.class).equals(SurveyResource.getReturnHTML(Constants.hubUrl)));

        //Need to check for wrong parameter in qustion
        assertTrue(false);
    }

    @Test
    public void surveySubmittedWithoutError() throws InterruptedException {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
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
                    break;
            }
        }


        Response response = TestListener.target.path("survey").path(Integer.toString(TestHelpers.survey2.getId()))
            .request()
            .post(Entity.form(form));

        assertEquals(response.getStatus(), 200);
        assertEquals(response.readEntity(String.class), SurveyResource.getReturnHTML(Constants.hubUrl));
    }
}
