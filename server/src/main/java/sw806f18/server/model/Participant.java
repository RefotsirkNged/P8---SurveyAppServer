package sw806f18.server.model;

public class Participant extends User {
    String cpr;

    public Participant(int id, String email, String cpr){
        super(id, email);
        this.cpr = cpr;
    }

    public Participant(int id, String email) {
        super(id, email);
    }
}
