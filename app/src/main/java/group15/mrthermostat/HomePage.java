package group15.mrthermostat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class HomePage extends ListActivity implements LocationListener {

    //private TextView cordsField;
    //private TextView longitudeField;
    //private LocationManager locationManager;
    //private String provider;
    Profile activeProfile;

    CityPreference appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        appPrefs = new CityPreference(this);

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

        ProfilesDataSource profilesDatasource = new ProfilesDataSource(this);
        try {
            profilesDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

            RulesDataSource ruleDatasource = new RulesDataSource(this);
            try {
                ruleDatasource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Rule> rules = ruleDatasource.getProfileRules(profile_name);
            RuleListArrayAdapter adapter = new RuleListArrayAdapter(this, rules);
            setListAdapter(adapter);
        } else {
            txtActiveProfile.setText("No Currently Active Profile");
        }


        SensorsDataSource datasource = new SensorsDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Sensor> sensors = datasource.getAllSensors();

        TextView txtSensor1, txtSensor2, txtSensor3, txtSensor4;
        txtSensor1 = (TextView)findViewById(R.id.sensor1);
        txtSensor2 = (TextView)findViewById(R.id.sensor2);
        txtSensor3 = (TextView)findViewById(R.id.sensor3);
        txtSensor4 = (TextView)findViewById(R.id.sensor4);

        if (sensors.size() > 3) {
            txtSensor1.setText(sensors.get(0).getName() + "\n" + sensors.get(0).getTemp() + "\u00B0F");
            txtSensor2.setText(sensors.get(1).getName() + "\n" + sensors.get(1).getTemp() + "\u00B0F");
            txtSensor3.setText(sensors.get(2).getName() + "\n" + sensors.get(2).getTemp() + "\u00B0F");
            txtSensor4.setText(sensors.get(3).getName() + "\n" + sensors.get(3).getTemp() + "\u00B0F");
        } else if (sensors.size() == 3) {
            txtSensor1.setText(sensors.get(0).getName() + "\n" + sensors.get(0).getTemp() + "\u00B0F");
            txtSensor2.setText(sensors.get(1).getName() + "\n" + sensors.get(1).getTemp() + "\u00B0F");
            txtSensor3.setText(sensors.get(2).getName() + "\n" + sensors.get(2).getTemp() + "\u00B0F");
        } else if (sensors.size() == 2) {
            txtSensor2.setText(sensors.get(0).getName() + "\n" + sensors.get(0).getTemp() + "\u00B0F");
            txtSensor3.setText(sensors.get(1).getName() + "\n" + sensors.get(1).getTemp() + "\u00B0F");
        } else if (sensors.size() == 1) {
            txtSensor2.setText(sensors.get(0).getName() + "\n" + sensors.get(0).getTemp() + "\u00B0F");
        } else {
            txtSensor1.setText("No");
            txtSensor2.setText("sensors");
            txtSensor3.setText("connected");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.change_city) {
            showCityInputDialog();
        }

        if (id == R.id.change_ip) {
            showIPInputDialog();
        }

        if (id == R.id.user_login) {
            showUsernameInputDialog();
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
}
