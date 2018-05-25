package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Error;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public ResponseEntity getSurvey(@PathVariable(value = "id") int id,
                                    @CookieValue(value = "token") String token) {
        if (Database.isParticipant(Authentication.instance.getId(token))) {
            Survey survey = Database.getSurvey(id);

            InputStreamResource inputStreamResource =
                    new InputStreamResource(new ByteArrayInputStream(
                            survey.getHTML().getBytes(StandardCharsets.UTF_8)));
            return ResponseEntity.ok(inputStreamResource);
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

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
        if (Database.isParticipant(Authentication.instance.getId(token))) {
            InputStream stream;
            Survey survey = Database.getSurvey(id);
            boolean hasWarnings = false;

            Map<String, String> formParams = new HashMap<>();
            String[] formMembers = params.split("&");
            for (String member : formMembers) {
                String s = URLDecoder.decode(member, StandardCharsets.UTF_8.toString());
                String[] keyValuePair = s.split("=");
                if (keyValuePair.length != 2) {
                    formParams.put(keyValuePair[0], "");
                } else {
                    formParams.put(keyValuePair[0], keyValuePair[1]);
                }
            }

            for (Question q : survey.getQuestions()) {
                if (formParams.containsKey(q.getHtmlID())) {
                    String value = formParams.get(q.getHtmlID());
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
                    stream = new ByteArrayInputStream(survey.getHTML().getBytes(StandardCharsets.UTF_8));
                }

            }

            InputStreamResource inputStreamResource = new InputStreamResource(stream);
            return ResponseEntity.ok(inputStreamResource);
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
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

    /**
     * Get Survey from ID as JSON response.
     *
     * @param id ID of survey to get.
     * @return Return Survey with ID.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{id}/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Survey> getSurveyObject(@PathVariable(value = "id") int id,
                                                  @CookieValue(value = "token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Survey survey = Database.getSurvey(id);
            return ResponseEntity.ok(survey);
        }
        return ResponseEntity.badRequest().build();
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
     * Add question to survey.
     *
     * @param surveyId Survey to remove from.
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            path = "/{surveyId}/question")
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
                            questionJson.get("description").asText(),
                            questionJson.get("tag").asText());
                    break;
                case "NUMBER":
                    q = new NumberQuestion(questionJson.get("title").asText(),
                            questionJson.get("description").asText(),
                            questionJson.get("tag").asText());
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
                            questionJson.get("tag").asText(),
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
     * Add empty survey.
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
     * Update survey metadata.
     *
     * @return Response.
     */
    @RequestMapping(method = RequestMethod.PUT,
            path = "/{surveyId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSurveyMetadata(@PathVariable(value = "surveyId") int surveyId,
                                               @RequestBody JsonNode body,
                                               @CookieValue(value = "token") String token)
            throws SurveyException {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Survey survey = Database.getSurvey(surveyId);
            survey.setTitle(body.get("title").asText());
            survey.setDescription(body.get("description").asText());
            survey.setFrequencyType(FrequencyType.valueOf(body.get("frequencyType").asText()));
            if (survey.getFrequencyType() == FrequencyType.DATE) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date;
                try {
                    date = formatter.parse(body.get("frequencyValue").asText());
                } catch (ParseException e) {
                    return ResponseEntity.badRequest().body(new Error("Invalid date"));
                }

                survey.setFrequencyValue(date.getTime() / 1000);
            } else {
                survey.setFrequencyValue(body.get("frequencyValue").asInt());
            }
            Database.updateSurvey(survey);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }
}

