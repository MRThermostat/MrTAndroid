package group15.mrthermostat;

/**
 * Created by jacob on 2/23/15.
 */
public class Profile {

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return name;
    }

    public void setComment(String comment) {
        this.name = comment;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}
