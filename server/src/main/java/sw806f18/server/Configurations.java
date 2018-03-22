package sw806f18.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Created by chrae on 09-03-2018.
 */
public class Configurations {
    public static Configurations instance;

    private String psqlIp;
    private String psqlUsername;
    private String psqlPassword;
    private String psqlDatabase;
    private int psqlPort;

    /**
     * Constructor.
     * @param path Path to configuration file.
     */
    public Configurations(String path) {
        try {
            InputStream fis = new FileInputStream(path);
            JsonReader reader = Json.createReader(fis);
            JsonObject conf = reader.readObject();
            reader.close();

            JsonObject psql = conf.getJsonObject("postgreSQL");
            psqlIp = psql.getString("ip");
            psqlUsername = psql.getString("user");
            psqlPassword = psql.getString("password");
            psqlPort = psql.getInt("port");
            psqlDatabase = psql.getString("database");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get IP.
     * @return IP.
     */
    public String postgresIp() {
        return psqlIp;
    }

    /**
     * Get port.
     * @return Port.
     */
    public int postgresPort() {
        return psqlPort;
    }

    /**
     * Get user.
     * @return User.
     */
    public String postgresUser() {
        return psqlUsername;
    }

    /**
     * Get password.
     * @return Password.
     */
    public String postgresPassword() {
        return psqlPassword;
    }

    /**
     * Get database.
     * @return RelationalDatabase.
     */
    public String postgresDatabase() {
        return psqlDatabase;
    }
}
