package sw806f18.server;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by chrae on 06-03-2018.
 */
@Path("/admin/session")
public class AdminSessionResource {
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    /**
     * Method adding admin
     *
     * @return True if successful
     */
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String addAdmin(@HeaderParam(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME) String username,
                           @HeaderParam(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD) String password,
                           @HeaderParam("phone") String phone) {
        try{
            Connection con = createConnection();
            Statement stmt1 = con.createStatement();
            byte[] salt = getNextSalt();

            String q1 = "INSERT INTO users(username, password, salt)" +
                    "VALUES (" + username + ", " + password + ", " + getByteString(salt) + ")" +
                    "RETURNING id";
            ResultSet rs = stmt1.executeQuery(q1);
            rs.next();
            int id = rs.getInt(1);
            stmt1.close();

            Statement stmt2 = con.createStatement();
            String q2 = "INSERT INTO researcher (id, email, phone)" +
                    "VALUES (" + id + ", " + username + ", " + phone + ")";
            stmt2.executeUpdate(q2);
            stmt2.close();
            closeConnection(con);
        } catch(SQLException e){
            return "Could not create query connection " + e.getMessage();
        } catch(ClassNotFoundException e){
            return "Could not find class " + e.getMessage();
        }
        return "";
    }

    private Connection createConnection() throws SQLException, ClassNotFoundException{
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://192.168.1.123:5432/postgres",
                        "postgres", "power123");
        return c;
    }

    private void closeConnection(Connection c) throws SQLException { c.close(); }

    /**
     * Returns a random salt to be used to hash a password.
     *
     * @return a 16 bytes random salt
     */
    private static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Returns a salted and hashed password using the provided hash.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     *
     * @param password the password to be hashed
     * @param salt     a 16 bytes salt, ideally obtained with the getNextSalt method
     *
     * @return the hashed password with a pinch of salt
     */
    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    private String getByteString(byte[] input){
        String ret = "";
        for(byte b : input){
            ret = ret + b;
        }
        return ret;
    }
}
