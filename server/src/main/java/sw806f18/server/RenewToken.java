package sw806f18.server;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

@Path("renewtoken")
public class RenewToken {

    /**
     * Renews a JWT token.
     * To use it, the client should at some point before expiration send the token to this resource to get a new token with a later expiration time.
     * If the token is either invalid or already expired, an "error" is returned.
     * @param token A JWT token generated by the system.
     * @return A new JWT token for the same user generated by the system.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject renew(@HeaderParam("token") String token)
    {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT  = Authentication.instance.decodeToken(token);
        }catch (JWTDecodeException e)
        {
            return Json.createObjectBuilder().add("error", "Please login to continue.").build();
        }

        if(Authentication.instance.isTokenExpired(decodedJWT))
        {
            return Json.createObjectBuilder().add("error", "Please login to continue.2").build();
        }
        else {
             String newToken = Authentication.instance.getToken(decodedJWT.getClaim("userid").asInt());
             return Json.createObjectBuilder().add("token", newToken).build();
        }
    }
}
