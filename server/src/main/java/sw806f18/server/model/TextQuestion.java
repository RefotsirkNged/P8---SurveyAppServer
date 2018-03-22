package sw806f18.server.model;

import org.w3c.tidy.Node;
import org.w3c.tidy.Tidy;

import java.util.List;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class TextQuestion extends Question {
    /**
     * Constructor for abstract class Question
     *
     * @param ID
     * @param title
     * @param description
     */
    public TextQuestion(int ID, String title, String description) {
        super(ID, Type.STRING, Input.TEXT, title, description);
    }

    /**
     * Constructor for abstract class Question
     *
     * @param title
     * @param description
     */
    public TextQuestion(String title, String description) {
        super(-1, Type.STRING, Input.TEXT, title, description);
    }


    /**
     * @return HTML representation of Question
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<h4>" + title + "</h4>");
        builder.append("<p>" + description + "</p>");
        builder.append("<input id='" + title.replace(" ","_") + "' type='text' class='form-control'>");

        return  builder.toString();
    }
}
