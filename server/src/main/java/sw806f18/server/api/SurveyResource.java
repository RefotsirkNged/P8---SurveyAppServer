package sw806f18.server.api;

import sw806f18.server.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
        Survey survey = new Survey("Barn 0-5 år", "Dette spørgeskema vedrører dit barn.");
        survey.addQuestion(new NumberQuestion(3,"Barents højde", "Skriv barnets højde i centimeter:"));
        survey.addQuestion(new NumberQuestion(3,"Barnets vægt", "Skriv barents vægt i gram:"));

        List<String> values = new ArrayList<>();
        values.add("0");
        values.add("1-5");
        values.add("6-10");
        values.add("10+");

        survey.addQuestion(new DropdownQuestion(2, Question.Type.STRING, "Barnets helbred", "Hvor mange gange har din barn været syg inden for de sidste 6 måneder:", values));
        survey.addQuestion(new TextQuestion(1,"Barnets livret", "Udfyld kun hvis relevant:"));


        InputStream stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
        return stream;
    }
}
