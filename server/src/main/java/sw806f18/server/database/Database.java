package sw806f18.server.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sw806f18.server.exceptions.AddGroupException;
import sw806f18.server.exceptions.AddGroupMemberException;
import sw806f18.server.exceptions.CprKeyNotFoundException;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.CreateUserException;
import sw806f18.server.exceptions.DeleteGroupException;
import sw806f18.server.exceptions.GetAllParticipantsException;
import sw806f18.server.exceptions.GetGroupMemberException;
import sw806f18.server.exceptions.GetGroupsException;
import sw806f18.server.exceptions.LoginException;
import sw806f18.server.exceptions.*;
import sw806f18.server.exceptions.RemoveParticipantFromGroupException;
import sw806f18.server.model.*;

/**
 * Created by augustkorvell on 22/03/2018.
 */
public class Database {
    /**
     * Get list of all groups from database.
     *
     * @return List of all groups.
     * @throws GetGroupsException Exception.
     */
    public static List<Group> getAllGroups() throws GetGroupsException {
        return RelationalDatabase.getAllGroups();
    }

    /**
     * Adds group to database.
     *
     * @param group group.
     * @return ID of group.
     * @throws AddGroupException Exception.
     */
    public static Group addGroup(Group group) throws AddGroupException {
        return RelationalDatabase.addGroup(group);
    }

    public static boolean isResearcher(int userId) {
        return RelationalDatabase.isResearcher(userId);
    }

    public static boolean isParticipant(int userId) {
        return RelationalDatabase.isParticipant(userId);
    }

    /**
     * Deletes group in database.
     *
     * @param id ID of group to delete.
     * @throws DeleteGroupException Exceptions.
     */
    public static void deleteGroup(int id) throws DeleteGroupException {
        RelationalDatabase.deleteGroup(id);
    }


    /**
     * Attempts login and returns id of researcher if successful.
     *
     * @param email    Email.
     * @param password Password.
     * @return Researcher object.
     * @throws LoginException Exception.
     */
    public static Researcher getResearcher(String email, String password) throws LoginException {
        return RelationalDatabase.getResearcher(email, password);
    }

    /**
     * Adds a new researcher to the database.
     *
     * @param researcher Researcher object.
     * @param password   Password.
     * @throws CreateUserException Exception.
     */
    public static Researcher createResearcher(Researcher researcher, String password) throws CreateUserException {
        return RelationalDatabase.createResearcher(researcher, password);
    }

    public static void createInvite(Invite invite) throws CreateInviteException {
        RelationalDatabase.createInvite(invite);
    }

    public static String getCPRFromKey(String key) throws CprKeyNotFoundException {
        return RelationalDatabase.getCPRFromKey(key);
    }

    public static void clearInviteFromKey(String key) throws CprKeyNotFoundException {
        RelationalDatabase.clearInviteFromKey(key);
    }

    public static Participant getParticipant(String email, String password) throws LoginException {
        return RelationalDatabase.getParticipant(email, password);
    }

    public static Participant createParticipant(Participant participant, String password) throws CreateUserException {
        return RelationalDatabase.createParticipant(participant, password);
    }

    /**
     * Adds Survey to database.
     *
     * @param s Survey to add.
     * @return new survey ID.
     * @throws SurveyException Exception.
     */
    public static int addSurvey(Survey s) throws SurveyException {
        s.setId(RelationalDatabase.addSurvey(s));
        NoSqlDatabase.addSurvey(s);
        return s.getId();
    }

    public static Survey getSurvey(int id) {
        return NoSqlDatabase.getSurvey(id);
    }

    public static List<Survey> getUsersSurveys(User user) throws SurveyException {
        return NoSqlDatabase.getSurveys(RelationalDatabase.getUsersSurveyIDs(user));
    }

    public static List<Survey> getAllModules() throws SurveyException {
        return RelationalDatabase.getAllModules();
    }

    public static List<Participant> getAllParticipants() throws GetAllParticipantsException {
        return RelationalDatabase.getAllParticipants();
    }

    public static void addGroupMember(Group group1, Participant participant1) throws AddGroupMemberException {
        RelationalDatabase.addGroupMember(group1, participant1);
    }

    public static void removeParticipantFromGroup(Group group, Participant participant)
            throws RemoveParticipantFromGroupException {
        RelationalDatabase.removeParticipantFromGroup(group, participant);
    }

    public static List<Participant> getGroupMembers(Group group1) throws GetGroupMemberException {
        return RelationalDatabase.getGroupMembers(group1);
    }

    /**
     * Link a module(survey) to a group using the IDs.
     *
     * @param surveyID
     * @param groupID
     * @throws P8Exception
     * @throws SQLException
     */
    public static void linkModuleToGroup(int surveyID, int groupID) throws P8Exception {
        RelationalDatabase.setModuleLink(surveyID, groupID);
    }

    public static List<Integer> getModuleLinks(Survey survey) throws SurveyException,
            SQLException, ClassNotFoundException {
        return RelationalDatabase.getModuleLinks(survey.getId());
    }

    public static void cleanMongoDB() {
        NoSqlDatabase.cleanMongoDB();
    }

    public static void addAnswer(Answer answer) throws AnswerException {
        NoSqlDatabase.addAnswer(answer);
        RelationalDatabase.addAnswer(answer);
    }

    /**
     * Get modules by user.
     *
     * @param userId User ID.
     * @return List of modules metadata.
     */
    public static List<Survey> getModulesByUser(int userId) throws GetModulesByUserException {
        return RelationalDatabase.getModulesByUser(userId);
    }

    /**
     * Add a hub to the database.
     *
     * @param hub Hub.
     * @return Hub.
     * @throws HubException Exception.
     */
    public static Hub addHub(Hub hub) throws HubException {
        hub.setId(RelationalDatabase.addHub());
        NoSqlDatabase.addHub(hub);
        return hub;
    }

    /**
     * Get Hub by User.
     *
     * @param userID User ID.
     * @return Hub.
     * @throws HubException Exception.
     */
    public static Hub getHubByUser(int userID) throws HubException {
        int hubID = RelationalDatabase.getHubIdByUser(userID);
        Hub hub = NoSqlDatabase.getHub(hubID);
        if (hub != null) {
            return hub;
        } else {
            return new Hub(userID);
        }
    }

    public static List<Survey> getGroupLinks(int groupId) throws SurveyException {
        return RelationalDatabase.getGroupLinks(groupId);
    }

    public static void removeGroupLink(int groupId, int moduleId) throws SurveyException {
        RelationalDatabase.removeGroupLink(groupId, moduleId);
    }

    public static Answer getNewestAnswer(int userId) {
        return NoSqlDatabase.getNewestAnswer(userId);
    }

    /**
     * Removes a question from a survey.
     *
     * @param surveyId   The ID of the survey from which the question should be removed.
     * @param questionId The ID of the question to be removed.
     */
    public static void removeQuestionFromSurvey(int surveyId, int questionId) throws SurveyException {
        RelationalDatabase.removeQuestionFromSurvey(questionId);
        try {
            NoSqlDatabase.removeQuestionFromSurvey(surveyId, questionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSurvey(Survey survey) throws SurveyException {
        RelationalDatabase.updateSurvey(survey);
        NoSqlDatabase.updateQuestionFromSurvey(survey);
    }

    public static List<String> getAllTags() throws P8Exception {
        return RelationalDatabase.getAllTags();
    }

    public static List<String> getIntTags() throws P8Exception {
        return RelationalDatabase.getIntTags();
    }

    public static List<String> getStringTags() throws P8Exception {
        return RelationalDatabase.getStringTags();
    }

    /**
     * Query surveys and answers.
     *
     * @param query Query.
     * @return Surveys ans persons.
     */
    public static Map<Tuple<String, Integer>, List<Tuple<String, Integer>>> querySurveys(List<QueryRow> query) {
        List<Tuple<String, Integer>> surveys = RelationalDatabase.queryTags(query);
        Map<Tuple<String, Integer>, List<Tuple<String, Integer>>> res = new HashMap<>();
        for (Tuple<String, Integer> survey : surveys) {
            List<Tuple<String, Integer>> persons = NoSqlDatabase.getPersonsFromSurvey(survey);
            res.put(survey, persons);
        }

        return res;
    }
}