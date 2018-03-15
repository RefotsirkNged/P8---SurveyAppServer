package sw806f18.server.model;

import sw806f18.server.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by augustkorvell on 14/03/2018.
 */
public class Survey {

    public Survey(String title, String description){
        this.title = title;
        this.description = description;
        questions = new ArrayList<>();
        id = -1;
    }

    public Survey(int id, String title, String description){
        this.title = title;
        this.description = description;
        questions = new ArrayList<>();
        this.id = id;
    }

    public List<Question> questions;
    private String title;
    private String description;
    private int id;

    /**
     * Adds (append) a Question to the survey
     * @param question
     */
    public void addQuestion(Question question){
        questions.add(question);
    }

    /**
     * Add Question to the survey at index
     * @param question
     * @param index
     */
    public void addQuestion(Question question, int index){
        questions.add(index, question);
    }


    /**
     * Move a question
     * @param currrentIndex
     * @param newIndex
     */
    public void moveQuestion(int currrentIndex, int newIndex){
        Question temp = questions.remove(currrentIndex);

        questions.add(newIndex, temp);


    }

    /**
     * @return Survey as HTML
     */
    public String getHTML(){
        StringBuilder builder = new StringBuilder();

        builder.append("<!DOCTYPE html>");
        builder.append("<html>");

        builder.append("<head>");
        builder.append("<title>" + title + "</title>");
        builder.append("</head>");

        builder.append("<body>");
        builder.append("<h1>" + title + "</h1>");
        builder.append("<h2>" + description + "</h2>");

        builder.append("<form action='" + Constants.url + "'>");

        for (int i = 0; i < questions.size(); i++) {
            builder.append(questions.get(i).getHTML());
            builder.append("<br />");
            builder.append("<br />");
        }

        builder.append("</form>");

        builder.append("</body>");

        builder.append("</html>");

        return builder.toString();
    }
}
