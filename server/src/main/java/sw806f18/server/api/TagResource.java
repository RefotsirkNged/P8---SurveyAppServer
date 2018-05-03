package sw806f18.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.model.Survey;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(path = "/tag")
public class TagResource {
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllTags(@CookieValue(value = "token") String token) throws P8Exception {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            List<String> tags = Database.getAllTags();
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();
            for (String tag : tags) {
                ObjectNode objectnode = mapper.createObjectNode();
                objectnode.put("name", tag);
                arrayNode.add(objectnode);
            }
            ObjectNode arraytags = mapper.createObjectNode();
            arraytags.put("tags", arrayNode);
            return ResponseEntity.ok(arraytags.toString());
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }
}


