package sw806f18.server.api;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.AddGroupMemberException;
import sw806f18.server.exceptions.GetAllParticipantsException;
import sw806f18.server.exceptions.GetGroupMemberException;
import sw806f18.server.model.Group;
import sw806f18.server.model.Participant;

import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.model.Invite;

@Path("researcher/participant")
public class ResearcherParticipantResource {

    /**
     * Endpoint for inviting participants.
     * @param token
     * @param cpr
     * @param email
     * @throws CreateInviteException
     */
    @POST
    public void inviteParticipant(@HeaderParam("token") String token,
                                  @HeaderParam("cpr") int cpr,
                                  @HeaderParam("email") String email) throws CreateInviteException {
        String key = Long.toHexString(Double.doubleToLongBits(Math.random()));
        Invite invite = new Invite(Integer.toString(cpr), key);
        try {
            Database.createInvite(invite);
        } catch (CreateInviteException e) {
            e.printStackTrace();
        }

        String from = "sw806f18@gmail.com";
        String username = "sw806f18@gmail.com";
        String password = "p0wer123";
        String host = "smtp.gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        Session emailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(emailSession);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Join MERSY");
            message.setText(key);
            Transport.send(message);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get group members.
     *
     * @param token Token.
     * @param groupId Group ID.
     * @return Group members.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getGroupMembers(@HeaderParam("token") String token,
                                      @HeaderParam("groupID") int groupId) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                List<Participant> participants = Database.getGroupMembers(new Group(groupId, "", 0));
                JsonArrayBuilder builder = Json.createArrayBuilder();
                for (Participant p : participants) {
                    builder.add(Json.createObjectBuilder().add("id", p.getId())
                        .add("cpr", p.getCpr()).add("firstname", p.getFirstName())
                        .add("lastname", p.getLastName()).add("primarygroup", p.getPrimaryGroup()).build());
                }
                return Json.createObjectBuilder().add("members", builder.build()).build();
            } catch (GetGroupMemberException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }

    /**
     * Get All Participants.
     *
     * @param token Token.
     * @return All participants.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public JsonObject getAllParticipants(@HeaderParam("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                List<Participant> participants = Database.getAllParticipants();
                JsonArrayBuilder builder = Json.createArrayBuilder();
                for (Participant p : participants) {
                    builder.add(Json.createObjectBuilder().add("id", p.getId())
                        .add("cpr", p.getCpr()).add("firstname", p.getFirstName())
                        .add("lastname", p.getLastName()).add("primarygroup", p.getPrimaryGroup()).build());
                }
                return Json.createObjectBuilder().add("participants", builder.build()).build();
            } catch (GetAllParticipantsException e) {
                return Json.createObjectBuilder().add("error", e.getMessage()).build();
            }
        }
        return Json.createObjectBuilder().add("error", "Invalid token").build();
    }
}
