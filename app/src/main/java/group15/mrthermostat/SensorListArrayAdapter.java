package group15.mrthermostat;

/**
 * Created by jacob on 3/20/15.
 * adapter to be used with the sensor_list_row layout
 * takes a string and an int and plugs them into a row that will display them side by side
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorListArrayAdapter extends ArrayAdapter<Sensor> {
    private final Context context;
    private final List<Sensor> values;

    public SensorListArrayAdapter(Context context, List<Sensor> values) {
        super(context, R.layout.sensor_list_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sensor_list_row,null);
            view = new ViewHolder();
            view.sensorName = (TextView) convertView.findViewById(R.id.sensorName);
            view.sensorTemp = (TextView) convertView.findViewById(R.id.sensorTemp);
            view.sensorActive = (ImageView) convertView.findViewById(R.id.sensor_active_list);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        // Assign the data
        Sensor sensor = values.get(position);
        view.sensorName.setText(sensor.getName());
        view.sensorTemp.setText("" + sensor.getTemp());

        if (sensor.getActive() == 1) {
            view.sensorActive.setImageResource(R.drawable.btn_check_on_holo_light);
        } else {
            view.sensorActive.setImageResource(R.drawable.btn_check_off_holo_light);
        }

        return convertView;
    }

    protected static class ViewHolder {
        protected TextView sensorName;
        protected TextView sensorTemp;
        protected ImageView sensorActive;
    }
}
