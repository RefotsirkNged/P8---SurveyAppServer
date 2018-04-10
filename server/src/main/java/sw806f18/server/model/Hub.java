package sw806f18.server.model;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import sw806f18.server.database.Database;
import sw806f18.server.exceptions.GetModulesByUserException;
import sw806f18.server.exceptions.HubException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrae on 05-04-2018.
 */
public class Hub {
    public enum Input{
        CARD,
        HEADER,
        BODY
    }

    /**
     * Build hun from user ID.
     *
     * @param userId User ID.
     * @return Hub.
     * @throws HubException Exception.
     */
    public static Hub buildHub(int userId) throws HubException {
        try {
            Hub hub = Database.getHubByUser(userId);
            hub.setModules(Database.getModulesByUser(userId));
            return hub;
        } catch (GetModulesByUserException e) {
            throw new HubException("Could not load modules. Contact system administrator");
        }
    }

    /**
     * Default constructor.
     */
    public Hub() {
        modules = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param userId User ID.
     * @throws HubException Exception.
     */
    public Hub(int userId) throws HubException {
        try {
            modules = Database.getModulesByUser(userId);
        } catch (GetModulesByUserException e) {
            throw new HubException("Could not load modules. Contact system administrator");
        }
    }

    @BsonIgnore
    private List<Survey> modules;
    private Map<String, List<String>> style = new HashMap<>();
    private int id = -1;

    public void setModules(List<Survey> modules) {
        this.modules = modules;
    }

    public Map<String, List<String>> getStyle() {
        return style;
    }

    public void setStyle(Map<String, List<String>> style) {
        this.style = style;
    }

    public void addModule(Survey module) {
        modules.add(module);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Get modules.
     *
     * @return Modules.
     */
    public List<Survey> getModules() {
        return modules;
    }

    /**
     * Add style property to input type.
     *
     * @param inputType input type to add properties to.
     * @param property  property to add.
     */
    public void addStyleProperty(Input inputType, String property, String value) {
        switch (inputType) {
            case CARD:
                addStyleProperty(".card", property, value);
                break;
            case HEADER:
                addStyleProperty("h2", property, value);
                break;
            case BODY:
                addStyleProperty("body", property, value);
            default:
                break;
        }
    }

    /**
     * Add style properties to html body.
     *
     * @param tag      the tag to add the property to
     * @param property properties to add.
     */
    public void addStyleProperty(String tag, String property, String value) {
        tag = tag.replace(".", "PUNKTUM");
        if (!style.containsKey(tag)) {
            style.put(tag, new ArrayList<>());
        }

        style.get(tag).add(property + ":" + value + ";");
    }

    /**
     * Get html representation of Question.
     *
     * @return Survey as HTML
     */
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        builder.append("<!DOCTYPE html>");
        builder.append("<html>");

        builder.append("<head>");
        builder.append("<title>MERSY</title>");
        builder.append("<meta charset='UTF-8' content='width=device-width, initial-scale=1.0'>");

        builder.append("<!-- Latest compiled and minified CSS -->\n"
                + "<link rel=\"stylesheet\" "
                + "href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" "
                + "type=\"text/css\">\n"
                + "\n"
                + "<!-- jQuery library -->\n"
                + "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\" "
                + "type='text/javascript'></script>\n"
                + "\n"
                + "<!-- Popper JS -->\n"
                + "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" "
                + "type='text/javascript'></script>\n"
                + "\n"
                + "<!-- Latest compiled JavaScript -->\n"
                + "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" "
                + "type='text/javascript'></script>");

        builder.append("<style type='text/css'>");

        for (String tag : style.keySet()) {
            builder.append(tag.replace("PUNKTUM", ".") + " {");
            for (String property : style.get(tag)) {
                builder.append(property);
            }
            builder.append("}");
        }
        builder.append("</style>");
        builder.append("</head>");

        builder.append("<body>");
        builder.append("<div class='content' style='padding:1%;'>");
        builder.append("<h2>Mine moduler</h2>");
        builder.append("<hr />");

        for (Survey m : modules) {
            builder.append(buildCard(m));
        }

        builder.append("</div>");

        builder.append("<script type='text/javascript'>");
        builder.append("function openModule(moduleID){"
                + "window.location.href = \"../../survey/\" + moduleID;"
                + "}");
        builder.append("</script>");

        builder.append("</body>");

        builder.append("</html>");

        return builder.toString();
    }

    private String buildCard(Survey module) {
        return "<div class=\"card\" onclick=\"openModule(" + module.getId() + ")\" style=\"margin-bottom:10px;\">\n"
                + "  <div class=\"card-body\">\n"
                + "    <h5 class=\"card-title\">" + module.getTitle() + "</h5>\n"
                + "    <p class=\"card-text\">" + module.getDescription() + "</p>\n"
                + "  </div>\n"
                + "</div>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Hub hub = (Hub) o;

        if(modules.size() != hub.modules.size()) {
            return false;
        }

        for(int i = 0; i < modules.size(); i++){
            Survey s1 = modules.get(i);
            Survey s2 = hub.modules.get(i);
            if(!(s1.getTitle().equals(s2.getTitle()) && s1.getDescription().equals(s2.getDescription()))){
                return false;
            }
        }

        return style != null ? style.equals(hub.style) : hub.style == null;
    }

    @Override
    public int hashCode() {
        int result = modules != null ? modules.hashCode() : 0;
        result = 31 * result + (style != null ? style.hashCode() : 0);
        return result;
    }
}
