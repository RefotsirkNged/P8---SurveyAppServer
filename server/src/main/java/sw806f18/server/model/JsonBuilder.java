package sw806f18.server.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JsonBuilder {
    private JsonBuilder() {
    }

    /**
     * Helper that builds formatted JSON error message.
     * @param value The value to include in the error.
     * @return A JSON string with the error message.
     */
    public static String buildError(String value) {
        return buildMessage("error", value);
    }

    /**
     * Builds a simple JSON string with a single key/value.
     * @param key The key of the JSON string.
     * @param value The value of the JSON string.
     * @return A JSON string with the key and value.
     */
    public static String buildMessage(String key, String value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(key, value);
        return node.toString();
    }

    /**
     * Builds a simple JSON string with a single key/value.
     * @param key The key of the JSON string.
     * @param value The value of the JSON string.
     * @return A JSON string with the key and value.
     */
    public static String buildMessage(String key, int value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(key, value);
        return node.toString();
    }
}
