package sw806f18.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw806f18.server.Authentication;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.CreateInviteException;
import sw806f18.server.exceptions.GetAllParticipantsException;
import sw806f18.server.exceptions.GetGroupMemberException;
import sw806f18.server.model.Group;
import sw806f18.server.model.Invite;
import sw806f18.server.model.JsonBuilder;
import sw806f18.server.model.Participant;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(path = "researcher/participant")
public class ResearcherParticipantResource {

    /**
     * Endpoint for inviting participants.
     *
     * @param token
     * @param cpr
     * @param email
     * @throws CreateInviteException
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity inviteParticipant(@CookieValue("token") String token,
                                            @RequestHeader("cpr") int cpr,
                                            @RequestHeader("email") String email) throws CreateInviteException {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            Invite invite = new Invite(Integer.toString(cpr), token);
            try {
                Database.createInvite(invite);
            } catch (CreateInviteException e) {
                e.printStackTrace();
                return ResponseEntity.ok(JsonBuilder.buildError(e.getMessage()));
            }

            // TODO: Maybe not have credentials in source files
            String to = "sw806f18@gmail.com";
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
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject("invite");
                message.setText("123");
                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
                return ResponseEntity.ok(JsonBuilder.buildError(e.getMessage()));
            }

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.ok(JsonBuilder.buildError("Invalid token"));
        }
    }

    /**
     * Get group members.
     *
     * @param token   Token.
     * @param groupId Group ID.
     * @return Group members.
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getGroupMembers(@CookieValue("token") String token,
                                          @RequestHeader("groupID") int groupId) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                List<Participant> participants = Database.getGroupMembers(new Group(groupId, "", 0));

                ObjectMapper mapper = new ObjectMapper();

                ArrayNode arrayNode = mapper.createArrayNode();

                for (Participant p : participants) {
                    ObjectNode node = mapper.createObjectNode();
                    node.put("id", p.getId());
                    node.put("cpr", p.getCpr());
                    node.put("firstname", p.getFirstName());
                    node.put("lastname", p.getLastName());
                    node.put("primarygroup", p.getPrimaryGroup());
                    arrayNode.add(node);
                }

                JsonNode members = mapper.createObjectNode().set("members", arrayNode);
                return ResponseEntity.ok(members);
            } catch (GetGroupMemberException e) {
                return ResponseEntity.badRequest().body(new Error(e.getMessage()));
            }
        }
        return ResponseEntity.badRequest().body(new Error("Invalid token"));
    }

    /**
     * Get All Participants.
     *
     * @param token Token.
     * @return All participants.
     */
    @RequestMapping(path = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllParticipants(@CookieValue("token") String token) {
        if (Database.isResearcher(Authentication.instance.getId(token))) {
            try {
                List<Participant> participants = Database.getAllParticipants();
                ObjectMapper mapper = new ObjectMapper();
                ArrayNode arrayNode = mapper.createArrayNode();

                for (Participant p : participants) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("id", p.getId());
                    objectNode.put("cpr", p.getCpr());
                    objectNode.put("firstname", p.getFirstName());
                    objectNode.put("lastname", p.getLastName());
                    objectNode.put("primarygroup", p.getPrimaryGroup());

                    arrayNode.add(objectNode);
                }

                ObjectNode participantsNode = mapper.createObjectNode();
                participantsNode.put("participants", arrayNode);

                return ResponseEntity.ok(participantsNode.toString());
            } catch (GetAllParticipantsException e) {
                return ResponseEntity.ok(JsonBuilder.buildError(e.getMessage()));
            }
        }
        return ResponseEntity.ok(JsonBuilder.buildError("Invalid token"));
    }
}
