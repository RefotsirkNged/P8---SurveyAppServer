package sw806f18.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.postgresql.util.PSQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestListener extends RunListener {
    private static boolean firstRun = true;
    private ConfigurableApplicationContext context;

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

        if (Configurations.instance.getPostgresDatabase().equals("devdb")
            || Configurations.instance.getPostgresDatabase().equals("postgres")) {
            throw new SQLException("Please use another database name for testing!!!");
        }

        if (Configurations.instance.getMongoDatabase().equals("data")) {
            throw new MongoException("Please use another database name for testing!!!");
        }

        dropRelationelDatabase();
        dropNonrelationalDatabase();

        createDatabase();


        try {
            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        context = SpringApplication.run(Main.class);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://"
                + Configurations.instance.getPostgresIp() + ":"
                + Configurations.instance.getPostgresPort(),
            Configurations.instance.getPostgresUser(),
            Configurations.instance.getPostgresPassword());
    }

    private void dropRelationelDatabase() {
        Connection connection = null;
        Statement statement = null;
        try {

            connection = getConnection();
            String query = "SELECT pg_terminate_backend(pg_stat_activity.pid) "
                + "FROM pg_stat_activity "
                + "WHERE pg_stat_activity.datname = '" + Configurations.instance.getPostgresDatabase() + "' "
                + "  AND pid <> pg_backend_pid();"
                + "DROP DATABASE " + Configurations.instance.getPostgresDatabase();
            statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            TestHelpers.closeConnection(connection);
            TestHelpers.closeStatement(statement);
        }
    }

    private void dropNonrelationalDatabase() {
        openNoSqlConnection();
        database.drop();
        closeNoSqlConnection();
    }

    private void createDatabase() throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConnection();
            String query = "SELECT pg_terminate_backend(pg_stat_activity.pid) "
                + "FROM pg_stat_activity "
                + "WHERE pg_stat_activity.datname = 'devdb' "
                + "  AND pid <> pg_backend_pid();"
                + "CREATE DATABASE " + Configurations.instance.getPostgresDatabase() + " TEMPLATE devdb";
            statement = connection.createStatement();
            statement.execute(query);
        } finally {
            TestHelpers.closeConnection(connection);
            TestHelpers.closeStatement(statement);
        }
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
