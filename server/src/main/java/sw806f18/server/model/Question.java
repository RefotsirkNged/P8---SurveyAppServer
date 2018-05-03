package sw806f18.server.model;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public abstract class Question {
    /**
     * Enum describing the input type of a question.
     */
    public enum Input {
        TEXT,
        NUMBER,
        DROPDOWN
    }

    /**
     * Enum describing the data type of a question.
     */
    public enum Type {
        STRING(0),
        INT(1);

        private final int value;

        private Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Base counstructer.
     */
    public Question() {

    }

    /**
     * Constructor for abstract class Question.
     *
     * @param id          The id of a question
     * @param title       The title
     * @param description Description
     */
    public Question(int id, Type type, Input input, String title, String description, String tag) {
        this.id = id;
        this.input = input;
        this.type = type;
        this.title = title;
        this.description = description;
        this.tag = tag;
    }

    protected int id;
    protected Type type;
    protected Input input;
    protected String title;
    protected String description;
    protected String tag;
    protected String value;
    protected String warning;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHtmlID() {
        return title.replace(" ", "_");
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    /**
     * Get the HTML representation of Question.
     *
     * @return HTML representation of Question
     */
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        if (warning != null) {
            builder.append("<p class='p8warning'>" + warning + "</p>");
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Question other = (Question) obj;
        return this.id == other.id;
    }
}
