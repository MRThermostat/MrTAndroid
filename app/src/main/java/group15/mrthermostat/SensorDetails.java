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

    EditText nameEdit;
    TextView tempText;

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

        List<Sensor> allSensors = sensorDatasource.getAllSensors();
        for (int i = 0; i < allSensors.size(); i++) {
            currentSensor = allSensors.get(i);
            if (sensorId == currentSensor.getId()) break;
        }

        nameEdit = (EditText)findViewById(R.id.sensor_name_edit);
        nameEdit.setText(currentSensor.getName(), TextView.BufferType.EDITABLE);

        tempText = (TextView)findViewById(R.id.sensor_temp);
        tempText.setText(currentSensor.getTemp()+"\u00B0F");
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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

        }
    }

    public void openSensorsActivity() {
        Intent intent = new Intent(this, Sensors.class);
        startActivity(intent);
    }
}
