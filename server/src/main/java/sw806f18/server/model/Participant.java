package sw806f18.server.model;

public class Participant extends User {
    private String cpr;

    public Participant(int id, String email, String cpr, String firstName, String lastName) {
        super(id, email, firstName, lastName);
        this.cpr = cpr;
    }

    public Participant(int id, String cpr, String firstName, String lastName) {
        super(id, firstName, lastName);
        this.cpr = cpr;
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

    /**
     * Check for equality without email.
     *
     * @param other Other.
     * @return True if equals.
     */
    public boolean equalsNoMail(Participant other) {
        return cpr.equals(other.cpr)
            && getFirstName().equals(other.getFirstName())
            && getLastName().equals(other.getLastName())
            && getId() == other.getId();
    }
}

