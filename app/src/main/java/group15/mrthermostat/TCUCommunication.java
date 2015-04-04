package group15.mrthermostat;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Created by jacob on 4/1/15.
 * Handles all communication with TCU (POST and GET)
 * also handles the parsing and creation of JSON objects used in communication
 */
public class TCUCommunication {

    private static final String TCU_IP = "192.168.4.1";
    public static CityPreference appPrefs;

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

        appPrefs = new CityPreference(context);
        String tcuSystem = appPrefs.getSystem();
        System.out.println(tcuSystem);
        String tcuFan = appPrefs.getFan();
        System.out.println(tcuFan);

        JSONObject jsonToGo = new JSONObject();

        try{
            jsonToGo.put("system",tcuSystem);
            jsonToGo.put("fan",tcuFan);
        } catch (JSONException e){
            e.printStackTrace();
        }

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

    public static JSONObject getJSON(Context context) {
        try {
            URL url = new URL(String.format(TCU_IP));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(2048);
            String tmp;
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    private void parseJSON(JSONObject json){
        String tempHi, tempLow, tempCurr, wind;

        try {
            String tcu_system = json.getString("system");
            String tcu_fan = json.getString("fan");

            JSONArray profiles = json.getJSONArray("profileList");
            for(int i=0; i<profiles.length(); i++){
                JSONObject profile = profiles.getJSONObject(i);
                String name = profile.getString("name");
                int active = profile.getInt("active");

                JSONArray rules = profile.getJSONArray("profileRulesList");
                for(int j=0; j<rules.length(); j++){
                    JSONObject rule = rules.getJSONObject(j);
                    int setting = rule.getInt("setting");
                    int startCond = rule.getInt("start_condition");
                    int endCond = rule.getInt("end_condition");
                    String type = rule.getString("type");
                    String parentProf = rule.getString("Parent_Name");
                }
            }

            JSONArray sensors = json.getJSONArray("sensorList");
            for(int i=0; i<sensors.length(); i++){
                JSONObject sensor = sensors.getJSONObject(i);
                String name = sensor.getString("name");
                int active = sensor.getInt("active");
                int temp = sensor.getInt("temperature");
            }
        }catch(Exception e){
            Log.e("JSONParsing", "One or more fields not found in the JSON data");
        }
    }
}
