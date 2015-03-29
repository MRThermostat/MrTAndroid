package group15.mrthermostat;

/**
 * Created by jacob on 2/23/15.
 * Holds profile specific functions and attributes
 */
public class Profile {

    private long id;
    private String name;
    private int active;
    private int tId;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getActive() { return active;}
    public void setActive(int active) { this.active = active;}

    public int getTId() {return tId;}
    public void settId(int tId) { this.tId = tId;}

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}
