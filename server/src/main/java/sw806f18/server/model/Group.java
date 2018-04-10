package sw806f18.server.model;

public class Group {
    private int id;
    private String name;
    private int hub;

    /**
     * Constructor.
     * @param id ID.
     * @param name Name.
     * @param hub Hub no.
     */
    public Group(int id, String name, int hub) {
        this.id = id;
        this.hub = hub;
        this.name = name;
    }

    /**
     * Constructor.
     * @param name Group name.
     * @param hub Hub no.
     */
    public Group(String name, int hub) {
        this(-1, name, hub);
    }

    /**
     * Get JSON object.
     * @return JSON object.
     */
    public String getJsonObject() {
        return "{\"id\": " + id + ","
                + " \"name\": \"" + name + "\","
                + " \"hub\": " + hub + "}";
    }

    /**
     * Get name.
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set hub.
     * @param hub Hub ID.
     */
    public void setHub(int hub) {
        this.hub = hub;
    }

    /**
     * Get ID.
     * @return ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Get hub no.
     * @return Hub.
     */
    public int getHub() {
        return hub;
    }

    /**
     * Set ID.
     * @param id ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Group)) {
            return false;
        }

        Group obj = (Group) other;
        return this.name.equals(obj.name);
    }
}
