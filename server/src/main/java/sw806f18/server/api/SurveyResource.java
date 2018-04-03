package sw806f18.server.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sw806f18.server.database.Database;
import sw806f18.server.model.DropdownQuestion;
import sw806f18.server.model.NumberQuestion;
import sw806f18.server.model.Question;
import sw806f18.server.model.Survey;
import sw806f18.server.model.TextQuestion;




/**
 * Created by augustkorvell on 14/03/2018.
 */
@Path("survey")
public class SurveyResource {

    /**
     * Get a survey as html by id.
     * @param id
     * @return stream
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public InputStream getSurvey(@PathParam("id") int id) {
        Survey survey = Database.getSurvey(id);


        InputStream stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
        return stream;
    }
}
