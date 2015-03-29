package group15.mrthermostat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;


public class SensorDetails extends Activity {
    long sensorId;
    SensorsDataSource sensorDatasource;
    Sensor currentSensor;
    List<Sensor> allSensors;

    EditText nameEdit;
    TextView tempText;
    TextView activeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        Intent intent = getIntent();
        sensorId = intent.getLongExtra("SENSOR_ID", 9999L);

        sensorDatasource = new SensorsDataSource(this);
        try {
            sensorDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        allSensors = sensorDatasource.getAllSensors();
        for (int i = 0; i < allSensors.size(); i++) {
            currentSensor = allSensors.get(i);
            if (sensorId == currentSensor.getId()) break;
        }

        nameEdit = (EditText)findViewById(R.id.sensor_name_edit);
        nameEdit.setText(currentSensor.getName(), TextView.BufferType.EDITABLE);

        tempText = (TextView)findViewById(R.id.sensor_temp);
        tempText.setText(currentSensor.getTemp()+"\u00B0F");

        activeText = (TextView)findViewById(R.id.sensor_active);

        if (currentSensor.getActive() == 1){
            activeText.setText("Active");
        } else {
            activeText.setText("Inactive");
        }

    }

    @Override
    protected void onResume() {
        try {
            sensorDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorDatasource.close();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Context context = getApplicationContext();

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_delete:
                sensorDatasource.deleteSensor(currentSensor);
                Toast.makeText(context, "Profile Deleted", Toast.LENGTH_SHORT).show();
                openSensorsActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Context context = getApplicationContext();
        switch (view.getId()) {
            case R.id.save_sensor_button:
                // save the new comment to the database
                currentSensor.setName(nameEdit.getText().toString());
                //currentSensor.setActive(active);

                sensorDatasource.updateSensor(currentSensor);

                Toast.makeText(context, "Rule Updated", Toast.LENGTH_SHORT).show();
                openSensorsActivity();

                break;

            case R.id.set_sensor_active_button:
                for (int i = 0; i < allSensors.size(); i++) {
                    Sensor tempSensor = allSensors.get(i);
                    tempSensor.setActive(0);
                    sensorDatasource.updateSensor(tempSensor);
                }
                currentSensor.setActive(1);
                sensorDatasource.updateSensor(currentSensor);
                Toast.makeText(context, "Sensor set to Active", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    public void openSensorsActivity() {
        Intent intent = new Intent(this, Sensors.class);
        startActivity(intent);
    }
}
