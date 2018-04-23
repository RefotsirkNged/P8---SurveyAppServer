package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.model.Hub;
import sw806f18.server.model.Survey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class ParticipantHubResourceTest {

    @Test
    public void getModulesByUser() {
        HttpURLConnection response = null;
        try {
            response = TestHelpers.getModulesByUser(TestHelpers.PARTICIPANT_HUB_MODULES_PATH,
                TestHelpers.token1);

            assertEquals(200, response.getResponseCode());
            JsonNode payload = TestHelpers.getJsonPayload(response);

            ArrayList<Survey> expected = new ArrayList<>();
            ArrayList<Survey> actual = new ArrayList<>();
            expected.add(TestHelpers.survey1);
            expected.add(TestHelpers.survey2);

            JsonNode modules = payload.get("modules");

            for (int i = 0; i < modules.size(); i++) {
                JsonNode node = modules.get(i);

                Survey survey = new Survey(
                    node.get("id").asInt(),
                    node.get("title").textValue(),
                    node.get("description").asText()
                );
                actual.add(survey);
            }

            assertEquals(actual.size(), expected.size());

            for (int i = 0; i < actual.size(); i++) {
                Survey s1 = actual.get(i);
                Survey s2 = expected.get(i);
                assertTrue(s1.getTitle().equals(s2.getTitle()));
                assertTrue(s1.getDescription().equals(s2.getDescription()));
                assertEquals(s1.getId(), s2.getId());
                assertEquals(s1.getFrequencyType(), s2.getFrequencyType());
                assertEquals(s1.getFrequencyValue(), s2.getFrequencyValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getHub() {
        boolean hasError = false;

        HttpURLConnection connection = null;
        try {
            connection = TestHelpers.getHub(TestHelpers.PARTICIPANT_HUB_PATH, TestHelpers.token1);
            String expected = null;
            expected = Hub.buildHub(TestHelpers.participant1.getId()).getHTML().replace("\n", "");

            String actual = TestHelpers.getStringPayload(connection).replace("\n", "");

            assertEquals(actual, expected);
        } catch (IOException | HubException e) {
            e.printStackTrace();
            fail();
        }
    }
}
