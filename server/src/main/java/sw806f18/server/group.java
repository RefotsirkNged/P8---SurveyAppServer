package sw806f18.server;

public class group {
    public int id;
    public String name;
    public int hub;

    public  group(int id, String name, int hub){
        this.id=id;
        this.hub=hub;
        this.name=name;
    }

    public String getJsonObject(){
        return id +" : \"" + name + "\" : " + hub;
    }
}
