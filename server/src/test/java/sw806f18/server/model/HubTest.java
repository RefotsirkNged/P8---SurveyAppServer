package sw806f18.server.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import sw806f18.server.Configurations;
import sw806f18.server.TestHelpers;
import sw806f18.server.TestRunner;
import sw806f18.server.exceptions.HubException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunner.class)
public class HubTest {
    @Test
    public void buildHub() {
        boolean hasError = false;
        Hub hub = null;
        Hub expected = new Hub();
        expected.addStyleProperty(Hub.Input.CARD, "background", "#FFF");
        expected.addStyleProperty(Hub.Input.CARD, "border", "1px solid #0a0a0a");
        expected.addStyleProperty(Hub.Input.BODY, "background-color", "#000");
        expected.addModule(TestHelpers.survey1);
        expected.addModule(TestHelpers.survey2);

        try {
            hub = Hub.buildHub(TestHelpers.participant1.getId());
        } catch (HubException e) {
            hasError = true;
        }
        assertFalse(hasError);
        assertTrue(expected.equals(hub));
    }

    @Test
    public void addModule() {
        Hub expected = new Hub();
        expected.addStyleProperty(Hub.Input.CARD, "background", "#FFF");
        expected.addStyleProperty(Hub.Input.CARD, "border", "1px solid #0a0a0a");
        expected.addStyleProperty(Hub.Input.BODY, "background-color", "#000");
        expected.addModule(TestHelpers.survey1);
        assertTrue(expected.equals(TestHelpers.hub2));
    }

    @Test
    public void addStyleProperty() {
        Hub expected = new Hub();
        expected.addStyleProperty(Hub.Input.CARD, "background", "#FFF");
        assertTrue(expected.equals(TestHelpers.hub3));
    }

    @Test
    public void getHTML() {
        Tidy tidy = new Tidy();

        boolean hasError = false;

        String html = null;
        try {
            html = Hub.buildHub(TestHelpers.participant1.getId()).getHTML();
        } catch (HubException e) {
            hasError = true;
        }
        assertFalse(hasError);

        InputStream stream = new ByteArrayInputStream(html.getBytes());
        Document htmlDoc = tidy.parseDOM(stream, System.out);

        Assert.assertTrue(tidy.getParseErrors() == 0);
        Assert.assertTrue(tidy.getParseWarnings() == 0);
        Assert.assertTrue(TestHelpers.getHTMLTagData(htmlDoc, "title").equals("MERSY"));
    }
}
