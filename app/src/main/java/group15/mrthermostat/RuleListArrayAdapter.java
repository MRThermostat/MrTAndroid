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

        String conditionText = String.format("From %d until %d", startCond, endCond);
        String settingText = String.format("set thermostat to %d", setting);

        view.conditionField.setText(conditionText);
        view.settingField.setText(settingText);

        return convertView;
    }

    protected static class ViewHolder {
        protected TextView conditionField;
        protected TextView settingField;
    }
}
