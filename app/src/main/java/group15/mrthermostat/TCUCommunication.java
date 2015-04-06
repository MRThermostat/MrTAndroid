package group15.mrthermostat;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
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
    private static final int TCU_PORT = 80;
    public static CityPreference appPrefs;

    public static JSONObject packJSON(Context context) {

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
        return jsonToGo;
    }

    public static JSONObject getJSON(Context context) {
        try {
            URL url = new URL(TCU_IP);
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

    public void sendJSON (JSONObject json) {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPostRqst = new HttpPost(TCU_IP);

        try {
            StringEntity jsonString = new StringEntity(json.toString());
            jsonString.setContentType("application/json;charset=UTF-8");
            jsonString.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            httpPostRqst.setEntity(jsonString);
            HttpResponse httpResponse = httpClient.execute(httpPostRqst);
        } catch (Exception e){
            Log.e("HttpPost", "Your stuff's broken.");
        }
    }

    /*
    public void parseJSON(JSONObject json){
        System.out.println("In Parser");

        try {
            String tcu_system = json.getString("system");
            String tcu_fan = json.getString("fan");

            System.out.println("System: "+tcu_system + "  Fan: "+tcu_fan);

            JSONArray profiles = json.getJSONArray("profileList");
            for(int i=0; i<profiles.length(); i++){
                JSONObject profile = profiles.getJSONObject(i);
                String name = profile.getString("name");
                int active = profile.getInt("active");

                System.out.println("Profile "+i + " - Profile Name: "+name + "  Active: "+active);

                JSONArray rules = profile.getJSONArray("profileRulesList");
                for(int j=0; j<rules.length(); j++){
                    JSONObject rule = rules.getJSONObject(j);
                    int setting = rule.getInt("setting");
                    int startCond = rule.getInt("start_condition");
                    int endCond = rule.getInt("end_condition");
                    String type = rule.getString("type");
                    String parentProf = rule.getString("Profile_Name");

                    System.out.println("Rule "+j + " - Type: "+type + "  End Condition: "+endCond
                            + "  Start Condition: "+startCond + "  Setting: "+setting);
                }
            }

            JSONArray sensors = json.getJSONArray("sensorList");
            for(int i=0; i<sensors.length(); i++){
                JSONObject sensor = sensors.getJSONObject(i);
                String name = sensor.getString("name");
                int active = sensor.getInt("active");
                int temp = sensor.getInt("temperature");

                System.out.println("Sensor "+i + " - Sensor Name: "+name + "  Active: "+active
                        + "  Temperature: "+temp);
            }
        }catch(Exception e){
            Log.e("JSONParsing", "One or more fields not found in the JSON data");
        }
    }
    */
}
