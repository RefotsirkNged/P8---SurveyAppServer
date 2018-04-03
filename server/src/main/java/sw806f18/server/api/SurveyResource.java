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
        Survey survey = new Survey("Barn 0-5 år",
                                                    "Dette spørgeskema vedrører dit barn.");
        survey.addQuestion(new NumberQuestion(3,"Barents højde",
                                                    "Skriv barnets højde i centimeter:"));
        survey.addQuestion(new NumberQuestion(3,"Barnets vægt",
                                                    "Skriv barents vægt i gram:"));

        List<String> values = new ArrayList<>();
        values.add("0");
        values.add("1-5");
        values.add("6-10");
        values.add("10+");

        survey.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Barnets helbred",
                "Hvor mange gange har din barn været syg inden for de sidste 6 måneder:",
                values));
        survey.addQuestion(new TextQuestion(1,"Barnets livret", "Udfyld kun hvis relevant:"));

        survey.addStyleProperty("body", "background-image", "url(\"https://images.pexels.com/photos/414667/pexels-photo-414667.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260\")");
        survey.addStyleProperty("p, h1, h2, h3, h4", "color", "white");

        InputStream stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
        return stream;
    }
}
