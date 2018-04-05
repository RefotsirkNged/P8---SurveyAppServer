package sw806f18.server.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import sw806f18.server.Constants;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.SurveyException;
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

    /**
     * Get a survey as html by id.
     * @param id
     * @return stream
     */
    @POST
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public InputStream getSurvey(@PathParam("id") int id,
                                 @Context UriInfo info,
                                 MultivaluedMap<String, String> formParams) {
        InputStream stream;
        Survey survey = Database.getSurvey(id);
        boolean hasWarnings = false;

        for (Question q : survey.getQuestions()) {
            if (info.getQueryParameters().containsKey(q.getHtmlID())) {
                String value = info.getQueryParameters().getFirst(q.getHtmlID());
                q.setValue(value);

                switch (q.getInput()) {
                    case DROPDOWN:
                    case TEXT:
                        if (!q.getValue().equals("0")) {
                            q.setWarning("TEST FEJL");
                            hasWarnings = true;
                        }
                        break;
                    case NUMBER:
                        try {
                            Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            q.setWarning("Må kun indholde heltal");
                            hasWarnings = true;
                        }
                        break;
                    default:
                        q.setWarning("FEJL!");
                        break;
                }
            }
        }

        if (hasWarnings) {
            stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
        } else {
            stream = new ByteArrayInputStream(getReturnHTML(Constants.hubUrl).getBytes(StandardCharsets.UTF_8));
        }

        return stream;
    }

    //TODO: Move this maybe
    /**
     * Redirect to URL.
     * @param url url to redirect to.
     * @return HTML that will redirect.
     */
    private String getReturnHTML(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");

        builder.append("<head>");
        builder.append("<meta charset='UTF-8' content='width=device-width, initial-scale=1.0'>");
        builder.append("<script type='text/javascript'>");

        builder.append("window.onload = function() {\n"
                + "    // similar behavior as clicking on a link\n"
                + "    window.location.href = \"" + url + "\";\n"
                + "}");

        builder.append("</script>");

        builder.append("</head>");

        builder.append("<body>");

        builder.append("</body>");

        builder.append("</html>");

        return  builder.toString();
    }

    /**
     * Post test survey.
     */
    @GET
    @Path("/test")
    public void postTestSurvey() {
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

        try {
            Database.addSurvey(survey);
        } catch (SurveyException e) {
            e.printStackTrace();
        }
    }
}
