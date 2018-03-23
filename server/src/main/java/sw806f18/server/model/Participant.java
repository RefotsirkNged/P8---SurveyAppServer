package sw806f18.server.model;

public class Participant extends User {
    private String cpr;

    public Participant(int id, String email, String cpr, String firstName, String lastName) {
        super(id, email, firstName, lastName);
        this.cpr = cpr;
    }

    public Participant(int id, String email, String firstName, String lastName) {
        super(id, email, firstName, lastName);
    }

    public String getCpr() {
        return cpr;
    }

    @Override
    public boolean equals(Object obj) {
        Participant other = (Participant) obj;
        return getEmail().equals(other.getEmail()) && cpr.equals(other.cpr)
            && getFirstName().equals(other.getFirstName())
            && getLastName().equals(other.getLastName());
    }
}

