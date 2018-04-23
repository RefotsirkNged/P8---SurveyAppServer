package sw806f18.server.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.*;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.GetModulesByUserException;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.model.Hub;
import sw806f18.server.model.Survey;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "participant/hub")
public class ParticipantHubResource {
    /**
     * GET method for getting modules by user ID.
     *
     * @param token
     * @return Modules
     */
    @RequestMapping(path = "/modules", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getModules(@CookieValue(value = "token") String token) {
        DecodedJWT decodedJWT = Authentication.instance.decodeToken(token);
        int userId = decodedJWT.getClaim("userid").asInt();
        List<Survey> modules = new ArrayList<>();

        try {
            modules = Database.getModulesByUser(userId);
        } catch (GetModulesByUserException e) {
            return ResponseEntity.badRequest().body(new Error(e.getMessage()));
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Survey m : modules) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", m.getId());
            node.put("title", m.getTitle());
            node.put("description", m.getDescription());

            arrayNode.add(node);
        }

        ObjectNode modulesNode = mapper.createObjectNode();
        modulesNode.put("modules", arrayNode);

        return ResponseEntity.ok(modulesNode.toString());
    }

    /**
     * Get hub HTML.
     *
     * @param token Token.
     * @return HTML for hub.
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity getHub(@CookieValue(value = "token") String token) {
        DecodedJWT decodedJWT = Authentication.instance.decodeToken(token);
        int userId = decodedJWT.getClaim("userid").asInt();
        Hub hub;

        try {
            hub = Hub.buildHub(userId);
            InputStreamResource inputStreamResource =
                new InputStreamResource(
                    new ByteArrayInputStream(hub.getHTML().getBytes(StandardCharsets.UTF_8)));
            return ResponseEntity.ok(inputStreamResource);

        } catch (HubException e) {
            String error = "System error. Contact sytem administrator.";
            InputStreamResource inputStreamResource =
                new InputStreamResource(
                    new ByteArrayInputStream(error.getBytes(StandardCharsets.UTF_8)));
            return ResponseEntity.ok(inputStreamResource);
        }
    }
}
