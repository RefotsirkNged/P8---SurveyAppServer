package sw806f18.server.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.BSON;
import org.bson.BsonDocument;
import sw806f18.server.Constants;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class Survey {

    /**
     * Empty constructer.
     */
    public Survey() {

    }

    /**
     * Ctor.
     * @param title       The title
     * @param description Description
     */
    public Survey(String title, String description) {
        this.title = title;
        this.description = description;
        questions = new ArrayList<>();
        id = -1;
        frequencyType = "blank";
        frequencyValue = 1;
    }

    /**
     * Ctor.
     *
     * @param id          Id of the survey
     * @param title       The title
     * @param description Description
     */
    public Survey(int id, String title, String description) {
        this.title = title;
        this.description = description;
        questions = new ArrayList<>();
        this.id = id;
        frequencyType = "blank";
        frequencyValue = 1;
    }

    private List<Question> questions;
    private String title;
    private String description;
    private int id;
    private int frequencyValue;
    private String frequencyType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrequencyValue(int value) {
        this.frequencyValue = value;
    }

    public int getFrequencyValue() {
        return frequencyValue;
    }

    public void setFrequencyType(String type) {
        this.frequencyType = type;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Add Question to the survey at index.

     * @param question question to be added
     * @param index    the index at which it should be added
     */
    public void addQuestion(Question question, int index) {
        questions.add(index, question);
    }

    /**
     * Adds (append) a Question to the survey.
     * @param question the question to add
     */
    public void addQuestion(Question question) {
        questions.add(question);
    }

    /**
     * Move a question.
     *
     * @param currrentIndex current position of question
     * @param newIndex      new position of question
     */
    public void moveQuestion(int currrentIndex, int newIndex) {
        Question temp = questions.remove(currrentIndex);

        questions.add(newIndex, temp);


    }

    /**
     * Get html representation of Question.
     *
     * @return Survey as HTML
     */
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<!DOCTYPE html>");
        builder.append("<html>");

        builder.append("<head>");
        builder.append("<title>" + title + "</title>");
        builder.append("<meta charset='UTF-8' content='width=device-width, initial-scale=1.0'>");

        builder.append("<!-- Latest compiled and minified CSS -->\n"
            + "<link rel=\"stylesheet\" "
            + "href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" "
            + "type=\"text/css\">\n"
            + "\n"
            + "<!-- jQuery library -->\n"
            + "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\" "
            + "type='text/javascript'></script>\n"
            + "\n"
            + "<!-- Popper JS -->\n"
            + "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" "
            + "type='text/javascript'></script>\n"
            + "\n"
            + "<!-- Latest compiled JavaScript -->\n"
            + "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" "
            + "type='text/javascript'></script>");

        builder.append("</head>");

        builder.append("<body style='background-color:#D0ECE7;'>");
        builder.append("<div class='content' style='padding:1%;'>");
        builder.append("<h2>" + title + "</h2>");
        builder.append("<br />");
        builder.append("<h4>" + description + "</h4>");
        builder.append("<hr />");
        builder.append("<form action='" + Constants.url + "'>");

        for (int i = 0; i < questions.size(); i++) {
            builder.append(questions.get(i).getHTML());
            builder.append("<br />");
            builder.append("<br />");
        }

        builder.append("</form>");
        builder.append("</div>");
        builder.append("</body>");

        builder.append("</html>");

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Survey other = (Survey) obj;

        if (other.questions.size() != this.questions.size()) {
            return false;
        }

        for (int i = 0; i < this.questions.size(); i++) {
            if (!this.questions.get(i).equals(other.questions.get(i))) {
                return false;
            }
        }

        return this.title.equals(other.title) && this.description.equals(other.description);
    }
}