package sw806f18.server.model;

import sw806f18.server.Configurations;

import java.util.Date;

public class Participant extends User {
    private String cpr;
    private Date birthday;
    private int primaryGroup = Configurations.instance.getPrimaryGroup();

    /**
     * Constructor.
     * @param id Id.
     * @param email Email.
     * @param cpr CPR.
     * @param firstName First name.
     * @param lastName Last name.
     * @param primaryGroup Primary hub group.
     */
    public Participant(int id, String email, String cpr, String firstName, String lastName, int primaryGroup) {
        super(id, email, firstName, lastName);
        this.cpr = cpr;
        this.birthday = new Date();
    }

    /**
     * Constructor.
     * @param id Id.
     * @param email Email.
     * @param cpr CPR.
     * @param firstName First name.
     * @param lastName Last name.
     */
    public Participant(int id, String email, String cpr, String firstName, String lastName) {
        super(id, email, firstName, lastName);
        this.cpr = cpr;
        this.birthday = new Date();
    }

    /**
     * Constructor.
     * @param id Id.
     * @param cpr CPR.
     * @param firstName First name.
     * @param lastName Last name.
     * @param primaryGroup Primary hub group.
     */
    public Participant(int id, String cpr, String firstName, String lastName, int primaryGroup) {
        super(id, firstName, lastName);
        this.cpr = cpr;
        this.birthday = new Date();
        this.primaryGroup = primaryGroup;
    }

    /**
     * Constructor.
     * @param id Id.
     * @param cpr CPR.
     * @param firstName First name.
     * @param lastName Last name.
     */
    public Participant(int id, String cpr, String firstName, String lastName) {
        super(id, firstName, lastName);
        this.cpr = cpr;
        this.birthday = new Date();
    }

    public int getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(int primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public String getCpr() {
        return cpr;
    }

    public Date getBirthday() {
        return birthday;
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

