package group15.mrthermostat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jacob on 3/23/15.
 */
public class RuleListArrayAdapter extends ArrayAdapter<Rule> {

    private final Context context;
    private final List<Rule> values;

    public RuleListArrayAdapter(Context context, List<Rule> values) {
        super(context, R.layout.rule_list_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rule_list_row,null);
            view = new ViewHolder();
            view.conditionField = (TextView) convertView.findViewById(R.id.condition);
            view.settingField = (TextView) convertView.findViewById(R.id.setting);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        // Assign the data
        Rule rule = values.get(position);
        int startCond = rule.getStartCondition();
        int endCond = rule.getEndCondition();
        int setting = rule.getSetting();

        String startCond_s, endCond_s;
        if (startCond <9) {
            startCond_s = "000"+startCond;
        } else if(startCond<99){
            startCond_s = "00"+startCond;
        } else if(startCond<999){
            startCond_s = "0"+startCond;
        } else {
            startCond_s = ""+startCond;
        }

        if (endCond <9) {
            endCond_s = "000"+endCond;
        } else if(endCond<99){
            endCond_s = "00"+endCond;
        } else if(endCond<999){
            endCond_s = "0"+endCond;
        } else {
            endCond_s = ""+endCond;
        }

        String startCond_h, startCond_m, endCond_h, endCond_m;
        startCond_h = startCond_s.substring(0,2);
        startCond_m = startCond_s.substring(2);
        endCond_h = endCond_s.substring(0,2);
        endCond_m = endCond_s.substring(2);


        //String conditionText = String.format("From %d until %d", startCond, endCond);
        String settingText = String.format("set thermostat to %d", setting);

        view.conditionField.setText("From " + startCond_h + ":" + startCond_m
                + " until " + endCond_h + ":" + endCond_m);
        view.settingField.setText(settingText);

        return convertView;
    }

    protected static class ViewHolder {
        protected TextView conditionField;
        protected TextView settingField;
    }
}
