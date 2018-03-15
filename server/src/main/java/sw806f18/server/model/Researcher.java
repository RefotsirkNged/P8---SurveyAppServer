package sw806f18.server.model;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class Researcher extends User {
    public Researcher(String email, String phone) {
        super(-1, email);
        this.phone = phone;
    }

    public Researcher(int id, String email, String phone) {
        super(id, email);
        this.phone = phone;
    }

    public String phone;

    @Override
    public boolean equals(Object obj) {
        Researcher other = (Researcher)obj;

        return email.equals(other.email) && phone.equals(other.phone);
    }
}
