package sw806f18.server.model;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class Researcher extends User {
    public Researcher(String email, String phone, String firstName, String lastName) {
        super(-1, email, firstName, lastName);
        this.phone = phone;
    }

    public Researcher(int id, String email, String phone, String firstName, String lastName) {
        super(id, email, firstName, lastName);
        this.phone = phone;
    }

    public String phone;

    @Override
    public boolean equals(Object obj) {
        Researcher other = (Researcher)obj;

        return getEmail().equals(other.getEmail()) && phone.equals(other.phone)
                && getFirstName().equals(other.getFirstName())
                && getLastName().equals(other.getLastName());
    }
}
