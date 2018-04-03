package sw806f18.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

public class TestListener extends RunListener {
    public static HttpServer server;
    public static WebTarget target;

    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
        TestHelpers.populateDatabase();
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        TestHelpers.resetDatabase();
    }

    @Override
    public void testRunStarted(Description description) throws SQLException {
        Configurations.instance = new Configurations("test-config.json");

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://"
                + Configurations.instance.getPostgresIp()
                + ":" + Configurations.instance.getPostgresPort(),
                Configurations.instance.getPostgresUser(),
                Configurations.instance.getPostgresPassword());
            System.out.println(connection.getMetaData().getSchemaTerm());
            connection.createStatement().execute("CREATE DATABASE "
                + Configurations.instance.getPostgresDatabase() + " TEMPLATE devdb");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);

        int i = 0;
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://"
            + Configurations.instance.getPostgresIp() + ":"
            + Configurations.instance.getPostgresPort(),
            Configurations.instance.getPostgresUser(),
            Configurations.instance.getPostgresPassword());
        connection.createStatement().execute("DROP DATABASE " + Configurations.instance.getPostgresDatabase());
        connection.close();

        server.shutdown();
    }
}
