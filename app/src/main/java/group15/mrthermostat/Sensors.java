package group15.mrthermostat;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;


import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class Sensors extends ListActivity {

    private SensorsDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        datasource = new SensorsDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Sensor> values = datasource.getAllSensors();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        SensorListArrayAdapter adapter = new SensorListArrayAdapter(this, values);
        setListAdapter(adapter);
    }


    // Will be called via the onClick attribute
    // of the buttons in main.xml
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Sensor> adapter = (ArrayAdapter<Sensor>) getListAdapter();
        Sensor sensor;
        switch (view.getId()) {
            case R.id.addSensor:
                String[] testSensors = new String[] { "Livingroom", "Bedroom", "Hallway" };
                int nextInt = new Random().nextInt(3);
                // save the new comment to the database
                sensor = datasource.createSensor(testSensors[nextInt], nextInt);
                adapter.add(sensor);
                break;
            case R.id.removeSensor:
                if (getListAdapter().getCount() > 0) {
                    sensor = (Sensor) getListAdapter().getItem(0);
                    datasource.deleteSensor(sensor);
                    adapter.remove(sensor);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensors, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
