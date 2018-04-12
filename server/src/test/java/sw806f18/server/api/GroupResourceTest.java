package sw806f18.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestListener;
import sw806f18.server.TestRunner;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.exceptions.SurveyException;
import sw806f18.server.model.Survey;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RunWith(TestRunner.class)
public class GroupResourceTest {
    @Test
    public void getGroupLinks() {
        Response response = TestHelpers.getWithToken(TestListener.target,
                TestHelpers.GROUP_PATH + "/" + TestHelpers.group1.getId() + "/surveys",
                TestHelpers.tokenResearcher1);
        assertEquals(200, response.getStatus());
        JsonObject object = TestHelpers.getPayload(response);
        JsonArray array = object.getJsonArray("modules");
        List<Survey> modules = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject element = array.get(i).asJsonObject();
            modules.add(new Survey(element.getInt("id"), element.getString("title"), element.getString("description")));
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
    public void removeGroupLink() {
        Response response = TestHelpers.removeGroupLink(TestListener.target,
                TestHelpers.GROUP_PATH + "/" + TestHelpers.group1.getId() + "/link/delete",
                TestHelpers.survey2.getId(),
                TestHelpers.tokenResearcher1);
        assertEquals(200, response.getStatus());

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
    public void linkModuleToGroup() throws P8Exception, SQLException, ClassNotFoundException {
        Response response = TestHelpers.linkModuleToSurvey(TestListener.target,
                TestHelpers.GROUP_PATH + "/" + TestHelpers.group1.getId() + "/link/add",
                TestHelpers.survey1.getId(), TestHelpers.tokenResearcher1);
        assertEquals(200, response.getStatus());

        List<Integer> linkedGroups = Database.getModuleLinks(TestHelpers.survey1);
        assertTrue(linkedGroups.contains(TestHelpers.group1.getId()));
    }
}
