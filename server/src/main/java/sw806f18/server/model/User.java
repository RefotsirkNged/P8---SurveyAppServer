package sw806f18.server.model;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public abstract class User {

    /**
     * Class representing a participant in the system.
     * @param id User ID.
     * @param email User email.
     * @param firstName Users first name.
     * @param lastName Users last name.
     */
    public User(int id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private int id;
    private String email;
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
