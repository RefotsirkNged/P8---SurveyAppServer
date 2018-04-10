package sw806f18.server.model;

import org.glassfish.grizzly.http.util.TimeStamp;

/**
 * Created by augustkorvell on 10/04/2018.
 */
public class Answer {
    private TimeStamp timeStamp;
    private int userID;
    private Survey survey;

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public int getUserID() {
        return userID;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * Empty constructer.
     */
    public Answer() {
    }

    /**
     * Standard constructor.
     * @param userID Id of the user who has made the answer.
     * @param survey The survey conatining the answered values.
     */
    public Answer(int userID, Survey survey) {
        this.userID = userID;
        this.survey = survey;
        this.timeStamp = new TimeStamp();
    }
}
