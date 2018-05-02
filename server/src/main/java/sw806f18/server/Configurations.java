package sw806f18.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    private int primaryGroup;

    /**
     * Constructor.
     * @param path Path to configuration file.
     */
    public Configurations(String path) {
        try {
            InputStream fis = new FileInputStream(path);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonNodes = mapper.readValue(fis, ObjectNode.class);

            JsonNode psql = jsonNodes.get("postgreSQL");
            psqlIp = psql.findValue("ip").asText();
            psqlUsername = psql.findValue("user").asText();
            psqlPassword = psql.findValue("password").asText();
            psqlPort = psql.findValue("port").asInt();
            psqlDatabase = psql.findValue("database").asText();

            JsonNode mongo = jsonNodes.get("mongoDB");
            mongoIp = mongo.findValue("ip").asText();
            mongoUser = mongo.findValue("user").asText();
            mongoPassword = mongo.findValue("password").asText();
            mongoPort = mongo.findValue("port").asInt();
            mongoDatabase = mongo.findValue("database").asText();

            primaryGroup = jsonNodes.get("primarygroup").asInt();
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

    public int getPrimaryGroup() {
        return primaryGroup;
    }

}
