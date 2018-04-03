package sw806f18.server.model;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class NumberQuestion extends Question {
    /**
     * Constructor for abstract class Question.
     * @param id The id of the question
     * @param title The title
     * @param description Description
     */
    public NumberQuestion(int id, String title, String description) {
        super(id, Type.INT, Input.NUMBER, title, description);
    }

    /**
     * Constructor for abstract class Question.
     * @param title The title
     * @param description Description
     */
    public NumberQuestion(String title, String description) {
        super(-1, Type.INT, Input.NUMBER, title, description);
    }

    /**
     * Empty constructor.
     */
    public NumberQuestion() {
        super();
    }

    /**
     * Get the HTML representation of Question.
     * @return HTML representation of Question
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<h4>" + title + "</h4>");
        builder.append("<p>" + description + "</p>");
        builder.append("<input id='"
                + title.replace(" ","_")
                + "' type='number' class='form-control'>");

        return  builder.toString();
    }
}
