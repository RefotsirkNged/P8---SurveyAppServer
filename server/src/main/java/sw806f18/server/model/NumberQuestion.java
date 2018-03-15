package sw806f18.server.model;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class NumberQuestion extends Question {
    /**
     * Constructor for abstract class Question
     *
     * @param ID
     * @param title
     * @param description
     */
    public NumberQuestion(int ID, String title, String description) {
        super(ID, Type.INT, Input.NUMBER, title, description);
    }

    /**
     * Constructor for abstract class Question
     *
     * @param ID
     * @param title
     * @param description
     */
    public NumberQuestion(String title, String description) {
        super(-1, Type.INT, Input.NUMBER, title, description);
    }

    /**
     * @return HTML representation of Question
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<h3>" + title + "</h3>");
        builder.append("<p>" + description + "</p>");
        builder.append("<input id='" + title.replace(" ","_")+ "' type='number'>");

        return  builder.toString();
    }
}
