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
        builder.append("<meta charset='UTF-8' content='width=device-width, initial-scale=1.0'>");

        builder.append("<!-- Latest compiled and minified CSS -->\n" +
                       "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" type=\"text/css\">\n" +
                       "\n" +
                       "<!-- jQuery library -->\n" +
                       "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\" type='text/javascript'></script>\n" +
                       "\n" +
                       "<!-- Popper JS -->\n" +
                       "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" type='text/javascript'></script>\n" +
                       "\n" +
                       "<!-- Latest compiled JavaScript -->\n" +
                       "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" type='text/javascript'></script>");

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




    private   void ABEFEST()  {








    }




}





