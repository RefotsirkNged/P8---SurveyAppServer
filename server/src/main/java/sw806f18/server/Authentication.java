package sw806f18.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class Authentication {
    public static final Authentication instance = new Authentication();

    private Algorithm algorithm;

    private final int TOKEN_EXPIRATION_DAYS = 1;

    private Authentication() {
        try {
            algorithm = Algorithm.HMAC512("power123");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param userid Id for the user that the token will be tied to.
     * @return A JWT token for the user.
     */
    public String getToken(int userid) {
        // Get the expiration time for the token
        Date expiryDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expiryDate);
        calendar.add(Calendar.DATE, TOKEN_EXPIRATION_DAYS);
        expiryDate = calendar.getTime();

        // Create a token
        // Token contains info about creation and expiration time, and the user it is tied to.
        return JWT.create().withIssuedAt(new Date(System.currentTimeMillis())).withExpiresAt(expiryDate).withIssuer("sw806f18").withClaim("userid", userid).sign(algorithm);
    }

    /**
     * Decodes a JWT token
     *
     * @param token A JWT token
     * @return A decoded JWT token
     */
    public DecodedJWT decodeToken(String token) {
        return JWT.require(algorithm).withIssuer("sw806f18").build().verify(token);
    }

    /**
     * Gets the ID of the user that a decoded JWT token belongs to
     *
     * @param token Decoded JWT token.
     * @return ID of the user the token belongs to.
     */
    public int getId(DecodedJWT token) {
        return token.getClaim("userid").asInt();
    }

    /**
     * Gets the ID of the user that a JWT token belongs to
     *
     * @param token JWT token
     * @return ID of the user the token belongs to.
     */
    public int getId(String token) {
        return getId(decodeToken(token));
    }

    /**
     * Checks if a token is expired.
     *
     * @param token A decoded JWT token
     * @return A boolean specifying if the token has expired
     */
    public boolean isTokenExpired(DecodedJWT token) {
        return (token.getExpiresAt().getTime() < new Date().getTime());
    }

    /**
     * Checks if a token is expired
     *
     * @param token A JWT token
     * @return A boolean specifying if the token has expired
     */
    public boolean isTokenExpired(String token) {
        return isTokenExpired(decodeToken(token));
    }
}
