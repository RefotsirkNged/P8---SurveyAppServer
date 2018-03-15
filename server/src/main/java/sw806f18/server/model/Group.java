package sw806f18.server.model;

public class Group {
    private int id;
    private String name;
    private int hub;

    public Group(int id, String name, int hub){
        this.id=id;
        this.hub=hub;
        this.name=name;
    }

    public Group(String name, int hub){
        this(-1, name, hub);
    }

    public String getJsonObject(){
        return "{\"id\": " + id +"," +
                " \"name\": \"" + name + "\"," +
                " \"hub\": " + hub + "}";
    }

    public String getName(){return name;}
    public int getId(){return id;}
    public int getHub(){return hub;}

    public void setId(int id){this.id = id;}

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Group))
            return false;

        Group obj = (Group) other;
        return this.id == obj.id && this.name.equals(obj.name);
    }
}
