package sw806f18.server.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sw806f18.server.Configurations;
import sw806f18.server.TestHelpers;

import static org.junit.Assert.assertTrue;

/**
 * Created by chrae on 05-04-2018.
 */
public class HubTest {
    /**
     * Before.
     *
     * @throws Exception Exception.
     */
    @Before
    public void setUp() throws Exception {
        Configurations.instance = new Configurations("test-config.json");
        TestHelpers.resetDatabase();

        TestHelpers.populateDatabase();
    }

    @After
    public void cleanUp() throws Exception {
        TestHelpers.resetDatabase();
    }

    @Test
    public void buildHub() {
        assertTrue(false);
    }

    @Test
    public void addModule() {
        assertTrue(false);
    }

    @Test
    public void addStyleProperty() {
        assertTrue(false);
    }

    @Test
    public void getHTML() {
        assertTrue(false);
    }
}
