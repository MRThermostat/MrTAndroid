package group15.mrthermostat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomePage extends ListActivity implements LocationListener {

    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;
    Profile activeProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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

        //latituteField = (TextView) findViewById(R.id.sensor1);
        //longitudeField = (TextView) findViewById(R.id.sensor2);

        //get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            //latituteField.setText("Location not available");
            //longitudeField.setText("Location not available");
        }

        ProfilesDataSource profilesDatasource = new ProfilesDataSource(this);
        try {
            profilesDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Profile> allProfs = profilesDatasource.getAllProfiles();

        for (int i = 0; i < allProfs.size(); i++) {
            activeProfile = allProfs.get(i);
            if (activeProfile.getActive()!=0) break;
        }
        String profile_name = activeProfile.getName();
        TextView txtActiveProfile = (TextView)findViewById(R.id.currentProfile);
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

        txtSensor1.setText(sensors.get(0).getName()+"\n"+sensors.get(0).getTemp()+"\u00B0F");
        txtSensor2.setText(sensors.get(1).getName()+"\n"+sensors.get(1).getTemp()+"\u00B0F");
        txtSensor3.setText(sensors.get(2).getName()+"\n"+sensors.get(2).getTemp()+"\u00B0F");
        txtSensor4.setText(sensors.get(3).getName()+"\n"+sensors.get(3).getTemp()+"\u00B0F");
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Context context = getApplicationContext();
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));

        //take coordinates and get a locale name
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //if locales are present, apply them to the weatherFetcher
        if (addresses.size() > 0) {
            System.out.println(addresses.get(0).getLocality());
            //changeCity(addresses.get(0).getLocality());
        }
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
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
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
        new CityPreference(this).setCity(city);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }
}
