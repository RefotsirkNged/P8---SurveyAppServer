package sw806f18.server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;

/**
 * Created by chrae on 09-03-2018.
 */
public class Configurations {
    public static Configurations instance = new Configurations("config.json");

    private String psqlIP;
    private String psqlUsername;
    private String psqlPassword;
    private int psqlPort;
    public Configurations(String path){
        try {
            InputStream fis = new FileInputStream(path);
            JsonReader reader = Json.createReader(fis);
            JsonObject conf = reader.readObject();
            reader.close();

            JsonObject psql = conf.getJsonObject("postgreSQL");
            psqlIP = psql.getString("ip");
            psqlUsername = psql.getString("user");
            psqlPassword = psql.getString("password");
            psqlPort = psql.getInt("port");
        } catch (IOException e) {
            e.printStackTrace();
            //throw new IOException("Configuration file not found");
        }
    }

    public String postgresIp(){return psqlIP;}
    public int postgresPort(){return psqlPort;}
    public String postgresUser(){return psqlUsername;}
    public String postgresPassword(){return psqlPassword;}
}
