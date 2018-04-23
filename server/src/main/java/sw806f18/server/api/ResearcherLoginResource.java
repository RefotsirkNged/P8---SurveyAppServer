package sw806f18.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.JsonBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "researcher/login")
public class ResearcherLoginResource {
    /**
     * Allows an admin to log into the system.
     * @param email The email address of the researcher.
     * @param password The password of the researcher.
     * @return A JSON string with either a login "token" or an "error" message.
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestHeader(value = "email") String email,
                                @RequestHeader(value = "password") String password,
                                HttpServletResponse response) {
        try {
            int userid = Database.getResearcher(email, password).getId();
            String token = Authentication.instance.getToken(userid);
            return ResponseEntity.ok(JsonBuilder.buildMessage("token", token));
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "Invalid email or password!");

            return ResponseEntity.ok(errorNode.toString());
        }
    }
}
