package group15.mrthermostat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HomePage extends ListActivity implements LocationListener {

    //private TextView cordsField;
    //private TextView longitudeField;
    //private LocationManager locationManager;
    //private String provider;

    TCUCommunication tcuComms;

    ProfilesDataSource profilesDatasource;
    RulesDataSource ruleDatasource;
    SensorsDataSource sensorDatasource;
    Profile activeProfile;

    CityPreference appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Context context = getApplicationContext();

        //JSONObject jsonToGo = tcuComms.packJSON(context);
        //sendJSON(jsonToGo);
        //clearDatabase();

        appPrefs = new CityPreference(context);

        //Set date and time
        String currentDateAndTime = DateFormat.getDateTimeInstance().format(new Date());
        TextView dateText = (TextView) findViewById(R.id.homePage_Date);
        dateText.setText(currentDateAndTime);

        //connect the homepage to the weather fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.weatherbox, new weather())
                    .commit();
        }

        //cordsField = (TextView) findViewById(R.id.activeSensorsTitle);
        //longitudeField = (TextView) findViewById(R.id.sensor2);

        //get the location manager
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use default
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);
        //Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        //if (location != null) {
        //    System.out.println("Provider " + provider + " has been selected.");
        //    onLocationChanged(location);
        //} else {
        //    cordsField.setText("Location not available");
        //    //longitudeField.setText("Location not available");
        //}

        profilesDatasource = new ProfilesDataSource(this);
        try {
            profilesDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ruleDatasource = new RulesDataSource(this);
        try {
            ruleDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sensorDatasource = new SensorsDataSource(this);
        try {
            sensorDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //parseJSON(jsonToGo);


        List<Profile> allProfs = profilesDatasource.getAllProfiles();

        TextView txtActiveProfile = (TextView)findViewById(R.id.currentProfile);

        if (allProfs.size() > 0) {
            for (int i = 0; i < allProfs.size(); i++) {
                activeProfile = allProfs.get(i);
                if (activeProfile.getActive()!=0) break;
            }

            String profile_name = activeProfile.getName();
            txtActiveProfile = (TextView)findViewById(R.id.currentProfile);
            txtActiveProfile.setText(activeProfile.getName() + ":");

            List<Rule> rules = ruleDatasource.getProfileRules(profile_name);
            RuleListArrayAdapter adapter = new RuleListArrayAdapter(this, rules);
            setListAdapter(adapter);
        } else {
            txtActiveProfile.setText("No Currently Active Profile");
        }

        TextView sysHeat = (TextView)findViewById(R.id.tcu_system_heat);
        TextView sysCool = (TextView)findViewById(R.id.tcu_system_cool);
        TextView sysBlow = (TextView)findViewById(R.id.tcu_system_blower);

        TextView fanOn = (TextView)findViewById(R.id.tcu_fan_on);
        TextView fanOff = (TextView)findViewById(R.id.tcu_fan_off);
        TextView fanAuto = (TextView)findViewById(R.id.tcu_fan_auto);

        if (appPrefs.getSystem().equals("heat")) {
            sysHeat.setBackground(getResources().getDrawable(R.drawable.underline_selection));
            sysCool.setBackgroundColor(getResources().getColor(android.R.color.white));
            sysBlow.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else if (appPrefs.getSystem().equals("cool")) {
            sysCool.setBackground(getResources().getDrawable(R.drawable.underline_selection));
            sysHeat.setBackgroundColor(getResources().getColor(android.R.color.white));
            sysBlow.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            sysBlow.setBackground(getResources().getDrawable(R.drawable.underline_selection));
            sysHeat.setBackgroundColor(getResources().getColor(android.R.color.white));
            sysCool.setBackgroundColor(getResources().getColor(android.R.color.white));
        }

        if (appPrefs.getFan().equals("on")) {
            fanOn.setBackground(getResources().getDrawable(R.drawable.underline_selection));
            fanOff.setBackgroundColor(getResources().getColor(android.R.color.white));
            fanAuto.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else if (appPrefs.getFan().equals("off")) {
            fanOff.setBackground(getResources().getDrawable(R.drawable.underline_selection));
            fanOn.setBackgroundColor(getResources().getColor(android.R.color.white));
            fanAuto.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            fanAuto.setBackground(getResources().getDrawable(R.drawable.underline_selection));
            fanOff.setBackgroundColor(getResources().getColor(android.R.color.white));
            fanOn.setBackgroundColor(getResources().getColor(android.R.color.white));
        }

        List<Sensor> sensors = sensorDatasource.getAllSensors();
        List<Sensor> activeSensors = new ArrayList<Sensor>();

        float activeSum=0;
        int activeCount=0;

        for(int i=0; i<sensors.size(); i++){
            Sensor tempSensor = sensors.get(i);
            if (tempSensor.getActive() == 1){
                activeSensors.add(tempSensor);
                activeCount++;
                activeSum += tempSensor.getTemp();
            }
        }
        float activeAvg = activeSum/activeCount/10;

        TextView txtSensor1, txtSensor2, txtSensor3, txtSensor4, txtActAvg;
        txtSensor1 = (TextView)findViewById(R.id.sensor1);
        txtSensor2 = (TextView)findViewById(R.id.sensor2);
        txtSensor3 = (TextView)findViewById(R.id.sensor3);
        txtSensor4 = (TextView)findViewById(R.id.sensor4);
        txtActAvg = (TextView)findViewById(R.id.active_average);
        float tempDecimal;

        txtActAvg.setText("Current Active Average: "+activeAvg+"\u00B0F");

        if (activeSensors.size() > 3) {
            tempDecimal = activeSensors.get(0).getTemp()/((float)10);
            txtSensor1.setText(activeSensors.get(0).getName() + "\n" + tempDecimal + "\u00B0F");
            tempDecimal = activeSensors.get(1).getTemp()/((float)10);
            txtSensor2.setText(activeSensors.get(1).getName() + "\n" + tempDecimal + "\u00B0F");
            tempDecimal = activeSensors.get(2).getTemp()/((float)10);
            txtSensor3.setText(activeSensors.get(2).getName() + "\n" + tempDecimal + "\u00B0F");
            tempDecimal = activeSensors.get(3).getTemp()/((float)10);
            txtSensor4.setText(activeSensors.get(3).getName() + "\n" + tempDecimal + "\u00B0F");
        } else if (activeSensors.size() == 3) {
            tempDecimal = activeSensors.get(0).getTemp()/((float)10);
            txtSensor1.setText(activeSensors.get(0).getName() + "\n" + tempDecimal + "\u00B0F");
            tempDecimal = activeSensors.get(1).getTemp()/((float)10);
            txtSensor2.setText(activeSensors.get(1).getName() + "\n" + tempDecimal + "\u00B0F");
            tempDecimal = activeSensors.get(2).getTemp()/((float)10);
            txtSensor3.setText(activeSensors.get(2).getName() + "\n" + tempDecimal + "\u00B0F");
        } else if (activeSensors.size() == 2) {
            tempDecimal = activeSensors.get(0).getTemp()/((float)10);
            txtSensor2.setText(activeSensors.get(0).getName() + "\n" + tempDecimal + "\u00B0F");
            tempDecimal = activeSensors.get(1).getTemp()/((float)10);
            txtSensor3.setText(activeSensors.get(1).getName() + "\n" + tempDecimal + "\u00B0F");
        } else if (activeSensors.size() == 1) {
            tempDecimal = activeSensors.get(0).getTemp()/((float)10);
            txtSensor2.setText(activeSensors.get(0).getName() + "\n" + tempDecimal + "\u00B0F");
        } else {
            TextView sensorTitleText =(TextView)findViewById(R.id.activeSensorsTitle);
            sensorTitleText.setText("No sensors active");
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        //locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Context context = getApplicationContext();
        //float lat = (float) (location.getLatitude());
        //float lng = (float) (location.getLongitude());
        //cordsField.setText(String.valueOf(lat)+ " " + String.valueOf(lng));
        //longitudeField.setText(String.valueOf(lng));

        //take coordinates and get a locale name
        //Geocoder gcd = new Geocoder(context, Locale.getDefault());
        //List<Address> addresses = null;
        //try {
        //    addresses = gcd.getFromLocation(lat, lng, 1);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //if locales are present, apply them to the weatherFetcher
        //if (addresses.size() > 0) {
        //   String cordCity = addresses.get(0).getLocality();
        //    System.out.println(cordCity);
        //    //changeCity(addresses.get(0).getLocality());
        //}
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    public void openProfilesActivity(View view) {
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

    public void openSensorsActivity(View view) {
        Intent intent = new Intent(this, Sensors.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.change_city) {
            showCityInputDialog();
        }

        if (id == R.id.change_ip) {
            showIPInputDialog();
        }

        if (id == R.id.user_login) {
            showUsernameInputDialog();
        }

        if (id == R.id.send_data) {
            transferData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCityInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(appPrefs.getCity());
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city){
        weather wf = (weather)getFragmentManager()
                .findFragmentById(R.id.weatherbox);
        wf.changeCity(city);
        appPrefs.setCity(city);
    }

    private void showIPInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set TCU IP");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(appPrefs.getIP());
        builder.setView(input);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeIP(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeIP(String tcuIP){
        System.out.println("Old:" + appPrefs.getIP());
        System.out.println("New: " + tcuIP);
        appPrefs.setIP(tcuIP);
    }

    private void showUsernameInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Username");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(appPrefs.getUsername());
        builder.setView(input);
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeUsername(input.getText().toString());
                showPasswordInputDialog();
            }
        });
        builder.show();
    }

    public void changeUsername(String username){
        System.out.println("Old:" + appPrefs.getUsername());
        System.out.println("New: " + username);
        appPrefs.setUsername(username);
    }

    private void showPasswordInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(appPrefs.getPassword());
        input.setTransformationMethod(new PasswordTransformationMethod());
        builder.setView(input);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changePassword(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changePassword(String password){
        System.out.println("Old:" + appPrefs.getPassword());
        System.out.println("New: " + password);
        appPrefs.setPassword(password);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

    public void onClick (View view) {
        TextView sysHeat = (TextView)findViewById(R.id.tcu_system_heat);
        TextView sysCool = (TextView)findViewById(R.id.tcu_system_cool);
        TextView sysBlow = (TextView)findViewById(R.id.tcu_system_blower);

        TextView fanOn = (TextView)findViewById(R.id.tcu_fan_on);
        TextView fanOff = (TextView)findViewById(R.id.tcu_fan_off);
        TextView fanAuto = (TextView)findViewById(R.id.tcu_fan_auto);

        switch (view.getId()) {
            case R.id.weatherbox:
                showCityInputDialog();
                break;

            case R.id.tcu_system_heat:
                sysHeat.setBackground(getResources().getDrawable(R.drawable.underline_selection));
                sysCool.setBackgroundColor(getResources().getColor(android.R.color.white));
                sysBlow.setBackgroundColor(getResources().getColor(android.R.color.white));
                appPrefs.setSystem("heat");
                break;

            case R.id.tcu_system_cool:
                sysCool.setBackground(getResources().getDrawable(R.drawable.underline_selection));
                sysHeat.setBackgroundColor(getResources().getColor(android.R.color.white));
                sysBlow.setBackgroundColor(getResources().getColor(android.R.color.white));
                appPrefs.setSystem("cool");
                break;

            case R.id.tcu_system_blower:
                sysBlow.setBackground(getResources().getDrawable(R.drawable.underline_selection));
                sysHeat.setBackgroundColor(getResources().getColor(android.R.color.white));
                sysCool.setBackgroundColor(getResources().getColor(android.R.color.white));
                appPrefs.setSystem("blow");
                break;

            case R.id.tcu_fan_on:
                fanOn.setBackground(getResources().getDrawable(R.drawable.underline_selection));
                fanOff.setBackgroundColor(getResources().getColor(android.R.color.white));
                fanAuto.setBackgroundColor(getResources().getColor(android.R.color.white));
                appPrefs.setFan("on");
                break;

            case R.id.tcu_fan_off:
                fanOff.setBackground(getResources().getDrawable(R.drawable.underline_selection));
                fanOn.setBackgroundColor(getResources().getColor(android.R.color.white));
                fanAuto.setBackgroundColor(getResources().getColor(android.R.color.white));
                appPrefs.setFan("off");
                break;

            case R.id.tcu_fan_auto:
                fanAuto.setBackground(getResources().getDrawable(R.drawable.underline_selection));
                fanOff.setBackgroundColor(getResources().getColor(android.R.color.white));
                fanOn.setBackgroundColor(getResources().getColor(android.R.color.white));
                appPrefs.setFan("auto");
                break;

        }
    }

    public void clearDatabase(){
        Context context = getApplicationContext();
        MySQLiteHelper myDB = new MySQLiteHelper(context);
        myDB.clearDB();
    }

    public void parseJSON(JSONObject json) {
        System.out.println("In Parser");

        try {
            String tcu_system = json.getString("system");
            String tcu_fan = json.getString("fan");

            appPrefs.setSystem(tcu_system);
            appPrefs.setFan(tcu_fan);

            System.out.println("System: " + tcu_system + "  Fan: " + tcu_fan);

            JSONArray profiles = json.getJSONArray("profileList");
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                String name = profile.getString("name");
                int active = profile.getInt("active");

                profilesDatasource.createProfile(name, active);

                System.out.println("Profile " + i + " - Profile Name: " + name + "  Active: " + active);

                JSONArray rules = profile.getJSONArray("profileRulesList");
                for (int j = 0; j < rules.length(); j++) {
                    JSONObject rule = rules.getJSONObject(j);
                    int setting = rule.getInt("setting");
                    int startCond = rule.getInt("start_condition");
                    int endCond = rule.getInt("end_condition");
                    String type = rule.getString("type");
                    String parentProf = rule.getString("Profile_Name");

                    ruleDatasource.createRule(parentProf,type,startCond,endCond,setting);

                    System.out.println("Rule " + j + " - Type: " + type + "  End Condition: " + endCond
                            + "  Start Condition: " + startCond + "  Setting: " + setting);
                }
            }

            JSONArray sensors = json.getJSONArray("sensorList");
            for (int i = 0; i < sensors.length(); i++) {
                JSONObject sensor = sensors.getJSONObject(i);
                String name = sensor.getString("name");
                int active = sensor.getInt("active");
                int temp = sensor.getInt("temperature");

                sensorDatasource.createSensor(name,temp,active,0);

                System.out.println("Sensor " + i + " - Sensor Name: " + name + "  Active: " + active
                        + "  Temperature: " + temp);
            }
        } catch (Exception e) {
            Log.e("JSONParsing", "One or more fields not found in the JSON data");
        }
    }

    public void sendJSON (final JSONObject json) {

        new Thread(){
            public void run() {
                System.out.println("In sendJSON");
                HttpClient httpClient = new DefaultHttpClient();
                System.out.println("Sending: "+json.toString());
                HttpPost httpPostRqst = new HttpPost("http://192.168.4.1");
                //HttpPost httpPostRqst = new HttpPost("http://posttestserver.com/post.php");
                //HttpPost httpPostRqst = new HttpPost("http://postcatcher.in/catchers/55225ab9187ce60300001056");

                System.out.println("-----------2------------");

                try {
                    httpPostRqst.setEntity(new StringEntity(json.toString(), "UTF-8"));
                    httpPostRqst.setHeader("Content-Type", "application/json");
                    //httpPostRqst.setHeader("Accept", "application/json");
                    //jsonString.setContentType("application/json;charset=UTF-8");
                    //jsonString.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                    //httpPostRqst.setEntity(jsonString);
                } catch (UnsupportedEncodingException e) {
                    // log exception
                    e.printStackTrace();
                }
                System.out.println("-----------3------------");


                try {
                    HttpResponse httpResponse = httpClient.execute(httpPostRqst);
                    Log.d("HTTPResponse:", httpResponse.getStatusLine().toString());
                    Log.d("HTTPResponse:", httpResponse.getEntity().toString());
                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("-----------4------------");
            }
        }.start();
    }

    public void transferData(){
        Context context = getApplicationContext();
        JSONObject jsonToGo = tcuComms.packJSON(context);
        sendJSON(jsonToGo);
    }
}
