package group15.mrthermostat;

/**
 * Created by jacob on 3/19/15.
 * Holds sensor specific functions and attributes
 */
public class Sensor {

    private long id;
    private String name;
    private int temp;
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

    public int getTemp() {
        return temp;
    }
    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getActive(){return active;}
    public void setActive(int active) {this.active = active;}

    public int getTId(){return tId;}
    public void setTId(int tId) {this.tId = tId;}

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}

