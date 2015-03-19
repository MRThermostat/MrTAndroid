package group15.mrthermostat;

/**
 * Created by jacob on 3/19/15.
 * Holds sensor specific functions and attributes
 */
public class Sensor {

    private long id;
    private String name;
    private String temp;

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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}

