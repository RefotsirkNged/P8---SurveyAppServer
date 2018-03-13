package sw806f18.server.model;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public abstract class User {

    public User(int id, String email){
        this.id = id;
        this.email = email;
    }

    public int id;
    public String email;
}
