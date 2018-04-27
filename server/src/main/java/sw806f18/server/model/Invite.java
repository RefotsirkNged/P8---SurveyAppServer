package sw806f18.server.model;

/**
 * Created by chrae on 22-03-2018.
 */
public class Invite {
    private String cpr;
    private String key;

    public Invite(String cpr, String key) {
        this.cpr = cpr;
        this.key = key;
    }

    public String getCpr() {
        return cpr;
    }

    public String getKey() {
        return key;
    }
}
