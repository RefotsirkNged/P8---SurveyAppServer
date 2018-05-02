package sw806f18.server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sw806f18.server.Configurations;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.Participant;

import java.sql.SQLException;

@RestController
@RequestMapping(path = "participant")
public class ParticipantResource {

    /**
     * Creates a participant with the given parameters.
     *
     * @param cpr
     * @param email
     * @param firstname
     * @param lastname
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createParticipant(@RequestHeader("cpr") String cpr,
                                            @RequestHeader("email") String email,
                                            @RequestHeader("firstname") String firstname,
                                            @RequestHeader("lastname") String lastname) {

        Participant participant = new Participant(-1, email, cpr, firstname, lastname,
                Configurations.instance.getPrimaryGroup());
        try {
            Database.createParticipant(participant, cpr);
            return ResponseEntity.ok().build();
        } catch (CreateUserException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
