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

    //TODO Need to use token
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

    //TODO Need to use token.
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
                                 MultivaluedMap<String, String> formParams) {
        InputStream stream;
        Survey survey = Database.getSurvey(id);
        boolean hasWarnings = false;

        for (Question q : survey.getQuestions()) {
            if (formParams.containsKey(q.getHtmlID())) {
                String value = formParams.getFirst(q.getHtmlID());
                q.setValue(value);

                switch (q.getInput()) {
                    case DROPDOWN:
                    case TEXT:

                        break;
                    case NUMBER:
                        try {

                            if (!value.equals("")) {
                                Integer.parseInt(value);
                            }
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

    //TODO Delete when finish
    /**
     * Post test survey.
     */
    @GET
    @Path("/test")
    public void postTestSurvey() {
        Survey survey1 = new Survey("Spørgeskema under graviditetsforløb",
                "Dette spørgeskema indholder spørgsmål vedrørende din livsstil og dit helbred.");
        survey1.addQuestion(new NumberQuestion(3,"Alkohol",
                "Hvor mange genstande drikker du om ugen:"));
        survey1.addQuestion(new NumberQuestion(3,"Rygning",
                "Hvor mange cigaretter ryger du om dagen:"));

        List<String> bistrolStoolChart = new ArrayList<>();
        bistrolStoolChart.add("");
        bistrolStoolChart.add("Type 1: Separate hårde klumper, som nødder (svære at klemme ud)");
        bistrolStoolChart.add("Type 2: Pølseformede med buler");
        bistrolStoolChart.add("Type 3: Som en pølse, men med sprækker i overfladen");
        bistrolStoolChart.add("Type 4: Som en pølse eller en slange, jævn og blød");
        bistrolStoolChart.add("Type 5: Bløde klumper med tydelige kanter (lette at klemme ud)");
        bistrolStoolChart.add("Type 6: Luftige småstykker med ujævn overflade, grødet konsistens");
        bistrolStoolChart.add("Type 7: Vandet og uden substans, fuldstændig flydende");

        survey1.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Afføring",
                "Hvordan vil du beskrive din afføring efter et gennemsnitligt toiletbesøg:",
                bistrolStoolChart));
        survey1.addQuestion(new TextQuestion(1,"Sygdomsepisoder",
                "Hvilke sygdosepisoder har du haft inden for det sidste år:"));

        survey1.addStyleProperty("body",
                "background-image",
                "url(\"https://images.pexels.com/photos/414667/pexels-photo-414667.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260\")");

        survey1.addStyleProperty("p, h1, h2, h3, h4", "color", "white");




        Survey survey2 = new Survey("Barn 6 - 10 år",
                "Dette spørgeskema vedrører dit barn.");
        survey2.addQuestion(new NumberQuestion(3,"Barents højde",
                "Skriv barnets højde i centimeter:"));
        survey2.addQuestion(new NumberQuestion(3,"Barnets vægt",
                "Skriv barents vægt i gram:"));

        try {
            Database.addSurvey(survey1);
        } catch (SurveyException e) {
            e.printStackTrace();
        }
    }
}
