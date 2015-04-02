package group15.mrthermostat;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jacob on 4/1/15.
 * Handles all communication with TCU (POST and GET)
 * also handles the parsing and creation of JSON objects used in communication
 */
public class TCUCommunication {

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial";

    public static void packJSON(Context context) {

        ProfilesDataSource profileDatasource = new ProfilesDataSource(context);
        try {
            profileDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RulesDataSource ruleDatasource = new RulesDataSource(context);
        try {
            ruleDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SensorsDataSource sensorDatasource = new SensorsDataSource(context);
        try {
            sensorDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Profile> allProfs = profileDatasource.getAllProfiles();
        List<Sensor> allSensors = sensorDatasource.getAllSensors();

        JSONObject jsonToGo = new JSONObject();

        JSONArray profileJSONArray = new JSONArray();

        if (allProfs.size() >0) {
            for (int i = 0; i < allProfs.size(); i++) {
                Profile tempProf = allProfs.get(i);
                JSONObject profJSONObject = new JSONObject();

                try {
                    JSONArray profRulesJSONArray = new JSONArray();
                    List<Rule> profRules = ruleDatasource.getProfileRules(tempProf.getName());

                    if (profRules.size()>0){
                        for (Rule tempRule : profRules){
                            JSONObject ruleJSONObject = new JSONObject();
                            ruleJSONObject.put(MySQLiteHelper.COLUMN_SETTING, tempRule.getSetting());
                            ruleJSONObject.put(MySQLiteHelper.COLUMN_END_CONDITION, tempRule.getEndCondition());
                            ruleJSONObject.put(MySQLiteHelper.COLUMN_START_CONDITION, tempRule.getStartCondition());
                            ruleJSONObject.put(MySQLiteHelper.COLUMN_TYPE, tempRule.getType());
                            ruleJSONObject.put(MySQLiteHelper.COLUMN_PROFILE_NAME, tempRule.getProfileName());
                            profRulesJSONArray.put(ruleJSONObject);
                        }
                    }

                    profJSONObject.put("profileRulesList", profRulesJSONArray);
                    profJSONObject.put(MySQLiteHelper.COLUMN_ACTIVE, tempProf.getActive());
                    profJSONObject.put(MySQLiteHelper.COLUMN_NAME, tempProf.getName());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                profileJSONArray.put(profJSONObject);
            }
        }

        JSONArray sensorJSONArray = new JSONArray();

        if (allSensors.size() >0) {
            for (int i = 0; i < allSensors.size(); i++) {
                Sensor tempSensor = allSensors.get(i);
                JSONObject sensorJSONObject = new JSONObject();

                try {
                    sensorJSONObject.put(MySQLiteHelper.COLUMN_TEMPERATURE, tempSensor.getTemp());
                    sensorJSONObject.put(MySQLiteHelper.COLUMN_ACTIVE, tempSensor.getActive());
                    sensorJSONObject.put(MySQLiteHelper.COLUMN_NAME, tempSensor.getName());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sensorJSONArray.put(sensorJSONObject);
            }
        }

        try {
            jsonToGo.put("profileList", profileJSONArray);
            jsonToGo.put("sensorList", sensorJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonToGoStr = jsonToGo.toString();
        System.out.println(jsonToGoStr);
    }
}
