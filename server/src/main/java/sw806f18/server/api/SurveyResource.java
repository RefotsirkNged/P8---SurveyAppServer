package sw806f18.server.api;

import sw806f18.server.Authentication;
import sw806f18.server.Database;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.model.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by augustkorvell on 14/03/2018.
 */
@Path("survey")
public class SurveyResource {

    /**
     * Get a survey as html by id
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public InputStream getSurvey(@PathParam("id") int id)
    {
        Survey survey = new Survey("Hello world", Integer.toString(id));
        survey.addQuestion(new TextQuestion(1,"Text question", "Text question description"));

        List<String> values = new ArrayList<>();
        values.add("A");
        values.add("B");
        values.add("C");

        survey.addQuestion(new DropdownQuestion(2, Question.Type.STRING, "Drop question", "Drop question description", values));
        survey.addQuestion(new NumberQuestion(3,"Number question", "Number question description"));
        System.out.println("Test-----------" + survey.getHTML());

        InputStream stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
        return stream;
    }
}
