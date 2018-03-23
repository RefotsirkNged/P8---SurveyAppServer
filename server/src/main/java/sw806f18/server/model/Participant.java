package sw806f18.server.model;

public class Participant extends User {
    public String cpr;

    public Participant(int id, String email, String cpr) {
        super(id, email);
        this.cpr = cpr;
    }

    public Participant(int id, String email) {
        super(id, email);
    }


    @Override
    public boolean equals(Object obj) {
        Participant other = (Participant)obj;
        return email.equals(other.email) && cpr.equals(other.cpr);
    }


}

