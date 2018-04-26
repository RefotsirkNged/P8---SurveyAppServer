package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw806f18.server.Authentication;
import sw806f18.server.Constants;
import sw806f18.server.database.Database;
import sw806f18.server.database.NoSqlDatabase;
import sw806f18.server.exceptions.AnswerException;
import sw806f18.server.exceptions.SurveyException;
import sw806f18.server.model.*;

import javax.websocket.server.PathParam;
import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Error;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Created by augustkorvell on 14/03/2018.
 */
@RestController
@RequestMapping(path = "/survey")
public class SurveyResource {

    //TODO Need to use token

    /**
     * Get a survey as html by id.
     *
     * @param id
     * @return stream
     */
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity getSurvey(@PathVariable(value = "id") int id) {
        Survey survey = Database.getSurvey(id);

        InputStreamResource inputStreamResource =
                new InputStreamResource(new ByteArrayInputStream(
                        survey.getHTML().getBytes(StandardCharsets.UTF_8)));
        return ResponseEntity.ok(inputStreamResource);
    }

    //TODO Need to use token.

    /**
     * Get a survey as html by id.
     *
     * @param id
     * @return stream
     */
    @RequestMapping(path = "/{id}/answer", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity postSurvey(@PathVariable("id") int id,
                                     @CookieValue("token") String token,
                                     @RequestBody String params)
            throws UnsupportedEncodingException {
        InputStream stream;
        Survey survey = Database.getSurvey(id);
        boolean hasWarnings = false;

        String content = new String(Base64.getDecoder().decode(params.substring(0, params.indexOf("="))));

        Map<String, String> formParams = new HashMap<>();
        String[] formMembers = content.split("&");
        for (String s : formMembers) {
            String[] keyValuePair = s.split("=");
            if (keyValuePair.length != 2) {
                formParams.put(keyValuePair[0], "");
            } else {
                formParams.put(keyValuePair[0], keyValuePair[1]);
            }
        }

        for (Question q : survey.getQuestions()) {
            if (formParams.containsKey(q.getHtmlID())) {
                String value = formParams.get(q.getHtmlID()); // formParams.getFirst(q.getHtmlID());
                q.setValue(value);

                switch (q.getInput()) {
                    case DROPDOWN:
                    case TEXT:
                        if (value.equals("0")) {
                            q.setWarning("TEST FEJL");
                            hasWarnings = true;
                        }
                        break;
                    case NUMBER:
                        if (value.equals("0")) {
                            q.setWarning("TEST FEJL");
                            hasWarnings = true;
                            break;
                        }
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
            int userID = Authentication.instance.getId(token);

            Answer answer = new Answer(userID, survey);


            try {
                Database.addAnswer(answer);
                stream = new ByteArrayInputStream(getReturnHTML(Constants.hubUrl).getBytes(StandardCharsets.UTF_8));
            } catch (AnswerException e) {
                e.printStackTrace();
                //TODO: Give a error message
                stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
            }

        }

        InputStreamResource inputStreamResource = new InputStreamResource(stream);
        return ResponseEntity.ok(inputStreamResource);
    }

    //TODO: Move this maybe

    /**
     * Redirect to URL.
     *
     * @param url url to redirect to.
     * @return HTML that will redirect.
     */
    public static String getReturnHTML(String url) {
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

        return builder.toString();
    }

    //TODO Delete when finish

    /**
     * Post test survey.
     */
    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public void postTestSurvey() {

        Survey survey1 = new Survey("Spørgeskema under graviditetsforløb",
                "Dette spørgeskema indeholder spørgsmål vedrørende din livsstil og dit helbred.");
        survey1.addStyleProperty("body", "background-color", "lightblue");

        survey1.addQuestion(new NumberQuestion(3, "Alkohol",
                "Hvor mange genstande drikker du om ugen?"));

        List<String> bristolStoolChart = new ArrayList<>();
        bristolStoolChart.add("");
        bristolStoolChart.add("Type 1: Separate hårde klumper, som nødder (svære at klemme ud)");
        bristolStoolChart.add("Type 2: Pølseformede med buler");
        bristolStoolChart.add("Type 3: Som en pølse, men med sprækker i overfladen");
        bristolStoolChart.add("Type 4: Som en pølse eller en slange, jævn og blød");
        bristolStoolChart.add("Type 5: Bløde klumper med tydelige kanter (lette at klemme ud)");
        bristolStoolChart.add("Type 6: Luftige småstykker med ujævn overflade, grødet konsistens");
        bristolStoolChart.add("Type 7: Vandet og uden substans, fuldstændig flydende");

        survey1.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Afføring",
                "Hvordan vil du beskrive din afføring efter et gennemsnitligt toiletbesøg?",
                bristolStoolChart));
        survey1.addQuestion(new TextQuestion(1, "Sygdomsepisoder",
                "Hvilke sygdomsepisoder har du haft inden for det sidste år?"));

        List<String> rygningList = new ArrayList<>();
        rygningList.add("Dagligt mere end 4");
        rygningList.add("Dagligt mindre end 4");
        rygningList.add("Ugentligt");
        rygningList.add("Ved sociale begivenheder");
        rygningList.add("Aldrig");

        survey1.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Rygning",
                "Hvor ofte ryger du?",
                rygningList));

        survey1.addQuestion(new TextQuestion("Graviditetsrelaterede begivenheder",
                "Har du haft nogen specielle episoder?"));
        survey1.addQuestion(new TextQuestion("Fødselsrelaterede begivenheder",
                "Skete der noget specielt under fødslen?"));
        survey1.addQuestion(new TextQuestion("Barselsrelaterede begivenheder",
                "Er der sket noget i din tid der hjemme?"));


        Survey survey2 = new Survey("Barn 6 - 10 år",
                "Dette spørgeskema vedrører dit barn.");
        survey2.addStyleProperty("body", "background-image", "url('https://media.istockphoto.com/photos/textured-blue-painted-background-picture-id534129318?k=6&m=534129318&s=612x612&w=0&h=5N2BeInhaXkV_G09cVoIaO2RWoNwGABqVbhw0U_0Jto=')");
        survey2.addStyleProperty("body", "background-size", "cover");

        survey2.addStyleProperty(".p8label", "font-family", "\"Comic Sans MS\", cursive, sans-serif");
        survey2.addStyleProperty(".p8label", "color", "white");


        List<String> ageChart = new ArrayList<>();
        ageChart.add("6 År");
        ageChart.add("7 År");
        ageChart.add("8 År");
        ageChart.add("9 År");
        ageChart.add("10 År");

        survey2.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Barnets alder",
                "Hvad er barnets alder?",
                ageChart));
        survey2.addQuestion(new NumberQuestion(3, "Barnets højde",
                "Skriv barnets højde i centimeter:"));
        survey2.addQuestion(new NumberQuestion(3, "Barnets vægt",
                "Skriv barents vægt i gram:"));


        Survey survey3 = new Survey("Efter fødsel til mor",
                "Yderlige information om dig efter fødslen");
        survey3.addStyleProperty("body", "background-color", "#FAD7A0");
        survey3.addQuestion(new DropdownQuestion(2, Question.Type.STRING,
                "Afføring",
                "Hvordan vil du beskrive din afføring efter et gennemsnitligt toiletbesøg?",
                bristolStoolChart));
        survey3.addQuestion(new NumberQuestion(3, "Alkohol",
                "Hvor mange genstande drikker du om ugen?"));
        survey3.addQuestion(new TextQuestion("Kost:",
                "Hvad består din daglige kost af?"
                        + "\n\"Eks: Får du mange fibre? Spiser du sundt?\""));
        survey3.addQuestion(new TextQuestion("Medicin", "Får du medicin?:"));
        survey3.addQuestion(new NumberQuestion("BMI", "Hvad er din BMI?:"));

        survey1.setId(570);
        survey2.setId(571);
        survey3.setId(572);

        NoSqlDatabase.addSurvey(survey1);
        NoSqlDatabase.addSurvey(survey2);
        NoSqlDatabase.addSurvey(survey3);
    }

    /**
     * Get Survey from ID as JSON response.
     *
     * @param id ID of survey to get.
     * @return Return Survey with ID.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{id}/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Survey> getSurveyObject(@PathParam("id") int id) {
        Survey survey = Database.getSurvey(id);
        return ResponseEntity.ok(survey);
    }

    /**
     * Remove question from survey.
     *
     * @param surveyId   Survey to remove from.
     * @param questionId Question to remove.
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/{surveyId}/question/{questionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteQuestionFromSurvey(@PathVariable(value = "surveyId") int surveyId,
                                                   @PathVariable(value = "questionId") int questionId,
                                                   @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                Database.removeQuestionFromSurvey(surveyId, questionId);

                return ResponseEntity.ok().build();
            } catch (SurveyException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new Error("Server Error"));
            }
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Remove question from survey.
     *
     * @param surveyId Survey to remove from.
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            path = "/{surveyId}/question",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addQuestionToSurvey(@PathVariable(value = "surveyId") int surveyId,
                                      @RequestBody JsonNode questionJson,
                                      @CookieValue(value = "token") String token)
            throws SurveyException {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Survey survey = Database.getSurvey(surveyId);

            Question q;

            switch (questionJson.get("input").asText()) {
                case "TEXT":
                    q = new TextQuestion(questionJson.get("title").asText(),
                            questionJson.get("description").asText());
                    break;
                case "NUMBER":
                    q = new NumberQuestion(questionJson.get("title").asText(),
                            questionJson.get("description").asText());
                    break;
                case "DROPDOWN":
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < questionJson.get("values").size(); i++) {
                        list.add(questionJson.get("values").get(i).asText());
                    }
                    Question.Type t;
                    if (questionJson.get("type").equals("STRING")) {
                        t = Question.Type.STRING;
                    } else {
                        t = Question.Type.INT;
                    }

                    q = new DropdownQuestion(t,
                            questionJson.get("title").asText(),
                            questionJson.get("description").asText(),
                            list);
                    break;
                default:
                    throw new SurveyException("Input " + questionJson.get("input").asText() + " wrong type.");
            }

            survey.addQuestion(q);
            Database.updateSurvey(survey);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Remove question from survey.
     *
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity addEmptySurvey(@CookieValue(value = "token") String token) throws SurveyException {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Survey survey = new Survey("", "");
            int id = Database.addSurvey(survey);
            return ResponseEntity.ok(Integer.toString(id));
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Remove question from survey.
     *
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.PUT,
            path = "/{surveyId}",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity updateSurveyMetadata(@PathVariable(value = "surveyId") int surveyId,
                                               @RequestBody JsonNode body,
                                               @CookieValue(value = "token") String token)
            throws SurveyException {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Survey survey = Database.getSurvey(surveyId);
            survey.setTitle(body.get("title").asText());
            survey.setDescription(body.get("description").asText());
            survey.setFrequencyValue(body.get("frequencyValue").asInt());
            survey.setFrequencyType(FrequencyType.valueOf(body.get("frequencyType").asText()));
            Database.updateSurvey(survey);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }
}

