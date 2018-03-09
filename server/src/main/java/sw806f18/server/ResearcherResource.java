package sw806f18.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;

/**
 * Created by chrae on 06-03-2018.
 */
@Path("/researcher")
public class ResearcherResource {
    /**
     * Method adding researcher
     *
     * @return String with status of creation
     */
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String addResearcher(@HeaderParam("email") String username,
                                @HeaderParam("password") String password,
                                @HeaderParam("phone") String phone) {
        try{
            Connection con = Database.createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = Security.getNextSalt();

            String q1 = "INSERT INTO users(username, password, salt) " +
                    "VALUES ( '" + username + "' , '" + Security.convertByteArrayToString(Security.hash(password, salt)) + "' , '" + Security.convertByteArrayToString(salt) + "' ) " +
                    "RETURNING id";

            ResultSet rs = stmt1.executeQuery(q1);
            rs.next();
            int id = rs.getInt(1);
            stmt1.close();

            Statement stmt2 = con.createStatement();
            String q2 = "INSERT INTO researcher (id, email, phone)" +
                    "VALUES (" + id + ", '" + username + "', " + phone + ")";
            stmt2.executeUpdate(q2);
            stmt2.close();
            Database.closeConnection(con);
        } catch(SQLException e){
            return "Could not create query connection " + e.getMessage();
        } catch(ClassNotFoundException e){
            return "Could not find class " + e.getMessage();
        }
        return "SUCCESS!";
    }
}
