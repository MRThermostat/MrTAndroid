package group15.mrthermostat;

/**
 * Created by jacob on 3/23/15.
 */
public class Rule {

    private long id;
    private String profileName, type;
    private int startCondition, endCondition, setting;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getStartCondition() {
        return startCondition;
    }
    public void setStartCondition(int startCondition) {
        this.startCondition = startCondition;
    }

    public int getEndCondition() {
        return endCondition;
    }
    public void setEndCondition(int endCondition) {
        this.endCondition = endCondition;
    }

    public int getSetting() {
        return setting;
    }
    public void setSetting(int setting) {
        this.setting = setting;
    }

}
