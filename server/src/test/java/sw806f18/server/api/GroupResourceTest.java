package sw806f18.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.exceptions.SurveyException;
import sw806f18.server.model.Survey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RunWith(TestRunner.class)
public class GroupResourceTest {
    @Test
    public void getGroupLinks() throws IOException {
        HttpURLConnection response = TestHelpers.getWithToken(TestHelpers.GROUP_PATH + "/"
                        + TestHelpers.group1.getId() + "/surveys",
                TestHelpers.tokenResearcher1);
        assertEquals(200, response.getResponseCode());
        JsonNode object = TestHelpers.getJsonPayload(response);
        JsonNode array = object.get("modules");
        List<Survey> modules = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonNode element = array.get(i);
            modules.add(new Survey(element.get("id").asInt(),
                    element.get("title").asText(), element.get("description").asText()));
        }

        List<Survey> expected = new ArrayList<>();
        expected.add(TestHelpers.survey1);
        expected.add(TestHelpers.survey2);

        for (int i = 0; i < modules.size(); i++) {
            assertEquals(modules.get(i).getId(), expected.get(i).getId());
            assertTrue(modules.get(i).getTitle().equals(expected.get(i).getTitle()));
            assertTrue(modules.get(i).getDescription().equals(expected.get(i).getDescription()));
        }
    }

    @Test
    public void removeGroupLink() throws IOException {
        HttpURLConnection response = TestHelpers.removeGroupLink(TestHelpers.GROUP_PATH + "/"
                        + TestHelpers.group1.getId() + "/link/delete",
                TestHelpers.survey2.getId(),
                TestHelpers.tokenResearcher1);
        assertEquals(200, response.getResponseCode());

        boolean hasError = false;
        List<Survey> modules = null;
        try {
            modules = Database.getGroupLinks(TestHelpers.group1.getId());
        } catch (SurveyException e) {
            hasError = true;
        }
        assertFalse(hasError);
        assertEquals(1, modules.size());
        assertEquals(modules.get(0).getId(), TestHelpers.survey1.getId());
    }

    @Test
    public void linkModuleToGroup() throws P8Exception, SQLException, ClassNotFoundException, IOException {
        HttpURLConnection response = TestHelpers.linkModuleToSurvey(TestHelpers.GROUP_PATH + "/"
                        + TestHelpers.group2.getId() + "/link/add",
                TestHelpers.survey1.getId(), TestHelpers.tokenResearcher1);
        assertEquals(200, response.getResponseCode());

        List<Integer> linkedGroups = Database.getModuleLinks(TestHelpers.survey1);
        assertTrue(linkedGroups.contains(TestHelpers.group2.getId()));
    }
}
