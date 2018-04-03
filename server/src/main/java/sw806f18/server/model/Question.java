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
        STRING,
        INT
    }

    /**
     * Base counstructer.
     */
    public Question() {

    }

    /**
     * Constructor for abstract class Question.
     * @param id The id of a question
     * @param title The title
     * @param description Description
     */
    public Question(int id, Type type, Input input, String title, String description) {
        this.id = id;
        this.input = input;
        this.type = type;
        this.title = title;
        this.description = description;
    }

    public int id;
    public Type type;
    public Input input;
    public String title;
    public String description;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the HTML representation of Question.
     * @return HTML representation of Question
     */
    public abstract String getHTML();

    @Override
    public boolean equals(Object obj) {
        Question other = (Question)obj;
        return this.id == other.id;
    }
}
