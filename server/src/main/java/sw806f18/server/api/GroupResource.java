package sw806f18.server.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.HubException;
import sw806f18.server.exceptions.P8Exception;
import sw806f18.server.exceptions.SurveyException;
import sw806f18.server.model.Hub;
import sw806f18.server.model.Survey;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "group/{id}")
public class GroupResource {
    /**
     * Get Surveys.
     *
     * @param token Token.
     * @return Surveys Metadata
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/surveys")
    public ResponseEntity getSurveys(@PathVariable(value = "id") int id,
                                     @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            List<Survey> modules;

            try {
                modules = Database.getGroupLinks(id);
            } catch (SurveyException e) {
                return ResponseEntity.ok().build();
            }

            String jsonGroup = "{ \"modules\": [ ";
            for (int i = 0; i < modules.size(); i++) {
                if (i == 0) {
                    jsonGroup += modules.get(i).getJsonObject();
                } else {
                    jsonGroup += ", " + modules.get(i).getJsonObject();
                }
            }
            jsonGroup += "]}";
            return ResponseEntity.ok(jsonGroup);
        }

        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Delete link.
     *
     * @param token    Token.
     * @param id       ID.
     * @param moduleId module
     * @return Surveys Metadata
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/link/delete")
    public ResponseEntity deleteLink(@PathVariable(value = "id") int id,
                             @RequestHeader(value = "module") int moduleId,
                             @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.removeGroupLink(id, moduleId);
                return ResponseEntity.ok().build();
            } catch (SurveyException e) {
                return ResponseEntity.badRequest().body(new Error("No such link"));
            }
        }

        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Linking a group to a survey.
     * @param id
     * @param moduleId
     * @param token
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, path = "/link/add")
    public ResponseEntity linkSurveyToGroup(@PathVariable(value = "id") int id,
                                            @RequestHeader(value = "module") int moduleId,
                                            @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.linkModuleToGroup(moduleId, id);
            } catch (SurveyException e) {
                return ResponseEntity.badRequest().body(new Error(e.getMessage()));
            } catch (P8Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }
}
