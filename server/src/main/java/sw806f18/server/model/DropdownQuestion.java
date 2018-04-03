package sw806f18.server.model;

import java.util.List;

/**
 * Created by augustkorvell on 14/03/2018.
 */

public class DropdownQuestion extends Question {
    private List<String> values;

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Constructor for abstract class Question.
     * @param id Questionaire Id
     * @param type Datatype
     * @param title The title
     * @param description Description
     */
    public DropdownQuestion(int id, Type type, String title, String description, List<String> values) {
        super(id, type, Input.DROPDOWN, title, description);
        this.values = values;
    }

    /**
     * Constructor for abstract class Question.
     *
     * @param type Datatype
     * @param title The title
     * @param description Description
     */
    public DropdownQuestion(Type type, String title, String description, List<String> values) {
        super(-1, type, Input.DROPDOWN, title, description);
        this.values = values;
    }

    /**
     * Empty constructor.
     */
    public DropdownQuestion() {
        super();
    }

    /**
     * Get the html representation of a question.
     * @return HTML representation of Question.
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<h4>" + title + "</h4>");
        builder.append("<p>" + description + "</p>");
        builder.append("<select id='"
                + title.replace(" ","_")
                + "' class='form-control'>");

        for (int i = 0; i < values.size(); i++) {
            builder.append("<option value='"
                    + values.get(i)
                    + "' class='form-control p8dropdown'>"
                    + values.get(i)
                    + "</option>");
        }

        builder.append("</select>");

        return  builder.toString();
    }
}
