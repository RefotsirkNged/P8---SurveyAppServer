package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunner.class)
public class TagResourceTest {

    @Test
    public void getAllTags() throws IOException {
        HttpURLConnection connection = TestHelpers.getHttpConnection(
                "tag",
                "GET",
                TestHelpers.tokenResearcher1,
                null,
                "application/x-www-form-urlencoded",
                null
        );

        assertEquals(connection.getResponseCode(), 200);
        JsonNode jsonNode = TestHelpers.getJsonPayload(connection);
        JsonNode jsonArray = jsonNode.get("tags");
        assertEquals(jsonArray.size(), 2);
        for (int i = 0; i < jsonArray.size(); i++) {
            assertTrue(jsonArray.get(i).asText().equals(TestHelpers.text1)
                    || jsonArray.get(i).asText().equals(TestHelpers.number1));
        }
    }

    @Test
    public void getIntTags() throws IOException {
        HttpURLConnection connection = TestHelpers.getHttpConnection(
                "tag/int",
                "GET",
                TestHelpers.token1,
                null,
                "application/x-www-form-urlencoded",
                null
        );

        assertEquals(connection.getResponseCode(), 200);
        JsonNode jsonNode = TestHelpers.getJsonPayload(connection);
        JsonNode jsonArray = jsonNode.get("tags");
        assertEquals(jsonNode.size(), 1);
        for (int i = 0; i < jsonNode.size(); i++) {
            assertTrue(jsonArray.get(i).equals(TestHelpers.number1));
        }
    }

    @Test
    public void getStringTags() throws IOException {
        HttpURLConnection connection = TestHelpers.getHttpConnection(
                "tag/String",
                "GET",
                TestHelpers.token1,
                null,
                "application/x-www-form-urlencoded",
                null
        );

        assertEquals(connection.getResponseCode(), 200);
        JsonNode jsonNode = TestHelpers.getJsonPayload(connection);
        JsonNode jsonArray = jsonNode.get("tags");
        assertEquals(jsonNode.size(), 1);
        for (int i = 0; i < jsonNode.size(); i++) {
            assertTrue(jsonArray.get(i).equals(TestHelpers.text1));
        }
    }
}
