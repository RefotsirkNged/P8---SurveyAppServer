package sw806f18.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.Participant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping(path = "participant/login")
public class ParticipantLoginResource {
    /**
     * POST method for performing a login as a participant.
     * @param email
     * @param password
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity login(@RequestHeader(value = "email") String email,
                                @RequestHeader(value = "password") String password,
                                HttpServletResponse response) {
        try {
            Participant participant = Database.getParticipant(email, password);
            String token = Authentication.instance.getToken(participant.getId());

            Cookie cookie = new Cookie("token", token);
            //cookie.setHttpOnly(true);

            response.addCookie(cookie);

            return ResponseEntity.ok().build();
        } catch (LoginException e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "Invalid email or password!");

            return ResponseEntity.ok(errorNode.toString());
        }
    }

    /**
     * Get login page HTML.
     * @return HTML for hub.
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public InputStream getLoginPage() {
        // TODO: FIX ME!!!
        // TODO: I Belong to some unfinished userstory

        StringBuilder sb = new StringBuilder();
        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
