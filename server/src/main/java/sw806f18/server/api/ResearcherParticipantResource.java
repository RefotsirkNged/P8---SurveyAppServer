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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
        Invite invite = new Invite(Integer.toString(cpr), token);
        try {
            Database.createInvite(invite);
        } catch (CreateInviteException e) {
            e.printStackTrace();
        }

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

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
