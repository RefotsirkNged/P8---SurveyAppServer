package sw806f18.server.api;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.internal.guava.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.*;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.model.Hub;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Survey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(TestRunner.class)
public class ParticipantHubResourceTest {

    @Test
    public void getModulesByUser() {
        Response response = TestHelpers.getModulesByUser(TestListener.target, TestHelpers.PARTICIPANT_HUB_MODULES_PATH,
            TestHelpers.token1);
        assertEquals(200, response.getStatus());
        JsonObject json = TestHelpers.getPayload(response);

        ArrayList<Survey> expected = new ArrayList<>();
        ArrayList<Survey> actual = new ArrayList<>();
        expected.add(TestHelpers.survey1);
        expected.add(TestHelpers.survey2);

        JsonArray jsonArr = json.getJsonArray("modules");
        for (int i = 0; i < jsonArr.size(); i++) {
            JsonObject obj = jsonArr.get(i).asJsonObject();
            Survey survey = new Survey(obj.getInt("id"), obj.getString("title"), obj.getString("description"));
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
    }

    @Test
    public void getHub() {
        boolean hasError = false;

        String actual = TestHelpers.getHub(TestListener.target, TestHelpers.PARTICIPANT_HUB_PATH, TestHelpers.token1);
        String expected = null;
        try {
            expected = Hub.buildHub(TestHelpers.participant1.getId()).getHTML();
        } catch (HubException e) {
            hasError = true;
        }
        assertFalse(hasError);

        assertTrue(actual.equals(expected));
    }
}
