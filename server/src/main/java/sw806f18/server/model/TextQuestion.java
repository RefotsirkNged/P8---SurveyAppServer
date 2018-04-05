package sw806f18.server.model;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class TextQuestion extends Question {

    /**
     * Empty constructor.
     */
    public TextQuestion() {
        super();
    }

    /**
     * Constructor for abstract class Question.
     * @param id The id of the question
     * @param title the title
     * @param description Description
     */
    public TextQuestion(int id, String title, String description) {
        super(id, Type.STRING, Input.TEXT, title, description);
    }

    /**
     * Constructor for abstract class Question.
     * @param title The title
     * @param description Description
     */
    public TextQuestion(String title, String description) {
        super(-1, Type.STRING, Input.TEXT, title, description);
    }


    /**
     * Get HTML representation of Question.
     * @return HTML representation of Question
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<h4 class='p8label'>" + title + "</h4>");
        builder.append("<p class='p8label'>" + description + "</p>");
        builder.append(super.getHTML());

        builder.append("<textarea name='" + getHtmlID()
                + "' type='text' rows='10' class='form-control p8text'>"
                + (value == null ? "" : value) + "</textarea>");

        return  builder.toString();
    }
}