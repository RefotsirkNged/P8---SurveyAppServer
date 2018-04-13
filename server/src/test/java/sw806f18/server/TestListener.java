package sw806f18.server;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.postgresql.util.PSQLException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestListener extends RunListener {
    public static HttpServer server;
    public static WebTarget target;

    private static boolean firstRun = true;

    private Connection tempConnection = null;

    @Override
    public void testStarted(Description description) throws Exception {
        TestHelpers.resetDatabase();
        TestHelpers.populateDatabase();
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        if (!firstRun) {
            return;
        }

        firstRun = false;

        Configurations.instance = new Configurations("test-config.json");

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        Logger grizzlyLogger = Logger.getLogger("org.glassfish.grizzly");
        grizzlyLogger.setLevel(Level.SEVERE);

        if (Configurations.instance.getPostgresDatabase().equals("devdb")
            || Configurations.instance.getPostgresDatabase().equals("postgres")) {
            throw new SQLException("Please use another database name for testing!!!");
        }

        if (Configurations.instance.getMongoDatabase().equals("data")) {
            throw new MongoException("Please use another database name for testing!!!");
        }

        try {
            dropRelationelDatabase();
            dropNonrelationalDatabase();
        } catch (PSQLException e) {
            e.printStackTrace();
            return;
            // The database is already dropped!
        }
        createDatabase();

        try {
            server.shutdown();
        } catch (Exception e) {
            System.out.println("Server could not shut down. " + e.getMessage());
        }

        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://"
                + Configurations.instance.getPostgresIp() + ":"
                + Configurations.instance.getPostgresPort(),
            Configurations.instance.getPostgresUser(),
            Configurations.instance.getPostgresPassword());
    }

    private void dropRelationelDatabase() throws SQLException {
        Connection connection = getConnection();
        String statement = "SELECT pg_terminate_backend(pg_stat_activity.pid) "
                + "FROM pg_stat_activity "
                + "WHERE pg_stat_activity.datname = '" + Configurations.instance.getPostgresDatabase() + "' "
                + "  AND pid <> pg_backend_pid();"
                + "DROP DATABASE " + Configurations.instance.getPostgresDatabase();
        connection.createStatement().execute(statement);
        connection.close();
    }

    private void dropNonrelationalDatabase() {
        openNoSqlConnection();
        database.drop();
        closeNoSqlConnection();
    }

    private void createDatabase() throws SQLException {
        Connection connection = getConnection();
        String statement = "SELECT pg_terminate_backend(pg_stat_activity.pid) "
                + "FROM pg_stat_activity "
                + "WHERE pg_stat_activity.datname = 'devdb' "
                + "  AND pid <> pg_backend_pid();"
                + "CREATE DATABASE " + Configurations.instance.getPostgresDatabase() + " TEMPLATE devdb";
        connection.createStatement().execute(statement);

        connection.close();
    }

    private static MongoClient client;
    private static MongoDatabase database;

    private static void openNoSqlConnection() {
        MongoClientURI uri = new MongoClientURI("mongodb://" + Configurations.instance.getMongoUser() + ":"
            + Configurations.instance.getMongoPassword() + "@" + Configurations.instance.getMongoIp()
            + ":" + Configurations.instance.getMongoPort() + "/?authSource=admin");
        client = new MongoClient(uri);
        database = client.getDatabase(Configurations.instance.getMongoDatabase());
    }


    private static void closeNoSqlConnection() {
        client.close();
    }
}
