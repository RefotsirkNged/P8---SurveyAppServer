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

    private String mongoIp;
    private String mongoUser;
    private String mongoPassword;
    private String mongoDatabase;
    private int mongoPort;

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

            JsonObject mongo = conf.getJsonObject("mongoDB");
            mongoIp = mongo.getString("ip");
            mongoUser = mongo.getString("user");
            mongoPassword = mongo.getString("password");
            mongoPort = mongo.getInt("port");
            mongoDatabase = mongo.getString("database");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get IP.
     * @return IP.
     */
    public String getPostgresIp() {
        return psqlIp;
    }

    /**
     * Get port.
     * @return Port.
     */
    public int getPostgresPort() {
        return psqlPort;
    }

    /**
     * Get user.
     * @return User.
     */
    public String getPostgresUser() {
        return psqlUsername;
    }

    /**
     * Get password.
     * @return Password.
     */
    public String getPostgresPassword() {
        return psqlPassword;
    }

    /**
     * Get database.
     * @return RelationalDatabase.
     */
    public String getPostgresDatabase() {
        return psqlDatabase;
    }


    /**
     * Get IP.
     * @return IP.
     */
    public String getMongoIp() {
        return mongoIp;
    }

    /**
     * Get port.
     * @return Port.
     */
    public int getMongoPort() {
        return mongoPort;
    }

    /**
     * Get user.
     * @return User.
     */
    public String getMongoUser() {
        return mongoUser;
    }

    /**
     * Get password.
     * @return Password.
     */
    public String getMongoPassword() {
        return mongoPassword;
    }

    /**
     * Get database.
     * @return RelationalDatabase.
     */
    public String getMongoDatabase() {
        return mongoDatabase;
    }
}
