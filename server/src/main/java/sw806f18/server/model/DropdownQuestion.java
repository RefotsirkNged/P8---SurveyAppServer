package sw806f18.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class DropdownQuestion extends Question {
    public List<String> values;

    /**
     * Constructor for abstract class Question
     *
     * @param ID
     * @param type
     * @param title
     * @param description
     */
    public DropdownQuestion(int ID, Type type, String title, String description, List<String> values) {
        super(ID, type, Input.DROPDOWN, title, description);
        this.values = values;
    }

    /**
     * Constructor for abstract class Question
     *
     * @param type
     * @param title
     * @param description
     */
    public DropdownQuestion(Type type, String title, String description, List<String> values) {
        super(-1, type, Input.DROPDOWN, title, description);
        this.values = values;
    }

    /**
     * @return HTML representation of Question
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<h3>" + title + "</h3>");
        builder.append("<p>" + description + "</p>");
        builder.append("<select id='" + title.replace(" ","_") + "'>");

        for (int i = 0; i < values.size(); i++) {
            builder.append("<option value='" + values.get(i) + "'>" + values.get(i) + "</option>");
        }

        builder.append("</select>");

        return  builder.toString();
    }
}
