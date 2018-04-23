package sw806f18.server.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.model.Researcher;

/**
 * Created by chrae on 06-03-2018.
 */
@RestController
@RequestMapping(path = "/researcher")
public class ResearcherResource {
    /**
     * Method adding researcher.
     *
     * @return String with status of creation.
     */
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity addResearcher(@RequestHeader("email") String email,
                                        @RequestHeader("password") String password,
                                        @RequestHeader("firstname") String firstname,
                                        @RequestHeader("lastname") String lastname,
                                        @RequestHeader("phone") String phone) {

        try {
            Database.createResearcher(new Researcher(email, phone, firstname, lastname), password);
        } catch (CreateUserException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Error(e.getMessage()));
        }

        return ResponseEntity.ok().build();
    }
}
