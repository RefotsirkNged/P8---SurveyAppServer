package sw806f18.server.database;

import java.sql.SQLException;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import sw806f18.server.exceptions.*;
import sw806f18.server.model.Group;
import sw806f18.server.model.Invite;
import sw806f18.server.model.Participant;
import sw806f18.server.model.Researcher;
import sw806f18.server.model.Survey;
import sw806f18.server.model.User;

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

    public static String getCPRFromKey(String key) throws SQLException, ClassNotFoundException {
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

    public static void linkModuleToGroup(Survey survey, Group group) throws SurveyException {
        RelationalDatabase.setModuleSurveyLink(survey.getId(), group.getId());
    }

    public static void cleanMongoDB() {
        NoSqlDatabase.cleanMongoDB();
    }
}
