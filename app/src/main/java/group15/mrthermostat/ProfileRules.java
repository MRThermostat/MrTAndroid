package group15.mrthermostat;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;


public class ProfileRules extends Activity implements NumberPicker.OnValueChangeListener{

    private TextView txtRuleOwner;
    private TextView txtTimeStart;
    private TextView txtTimeEnd;
    private TextView txtSetting;

    String ruleOwner;
    int startCond, endCond, setting;

    private int startHour;
    private int startMinutes;
    private int endHour;
    private int endMinutes;

    private RulesDataSource rulesDatasource;
    Rule currentRule;
    Boolean editingRule = false;

    private TimePickerDialog.OnTimeSetListener onTimeSetListener_start = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // update the current variables (hour and minutes)
            startHour = hourOfDay;
            startMinutes = minute;
            // update txtTime with the selected time
            updateStartTimeUI();
        }
    };

    private TimePickerDialog.OnTimeSetListener onTimeSetListener_end = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // update the current variables (hour and minutes)
            endHour = hourOfDay;
            endMinutes = minute;
            // update txtTime with the selected time
            updateEndTimeUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_rules);

        rulesDatasource = new RulesDataSource(this);
        try {
            rulesDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Spinner spinner = (Spinner) findViewById(R.id.ruleTypeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rule_type_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // retrieve views
        txtRuleOwner = (TextView) findViewById(R.id.rule_owner);
        txtTimeStart = (TextView) findViewById(R.id.rule_start_time_view);
        txtTimeEnd = (TextView) findViewById(R.id.rule_end_time_view);
        txtSetting = (TextView) findViewById(R.id.rule_setting_view);

        // initialize the current date
        //Calendar calendar = Calendar.getInstance();
        //this.startHour = calendar.get(Calendar.HOUR_OF_DAY);
        //this.startMinutes = calendar.get(Calendar.MINUTE);
        // set the current date and time on TextViews
        //updateStartTimeUI();

        //this.endHour = this.startHour + 1;
        //this.endMinutes = this.startMinutes + 1;
        //updateEndTimeUI();

        Intent intent = getIntent();
        ruleOwner = intent.getStringExtra(ProfileDetails.RULE_OWNER);
        Long rule_id = intent.getLongExtra(ProfileDetails.RULE_ID, 9999L);

        if(rule_id != 9999) {
            editingRule = true;
            List<Rule> allRules = rulesDatasource.getAllRules();
            for (int i = 0; i < allRules.size(); i++) {
                currentRule = allRules.get(i);
                if (rule_id == currentRule.getId()) break;
            }

            ruleOwner = currentRule.getProfileName();

            startCond = currentRule.getStartCondition();
            endCond = currentRule.getEndCondition();
            setting = currentRule.getSetting();

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

            txtTimeStart.setText("START:  "+ startCond_h+":"+startCond_m);
            txtTimeEnd.setText("END:      "+ endCond_h+":"+endCond_m);
            txtSetting.setText("Temperature: "+setting);
        }

        txtRuleOwner.setText(ruleOwner);

        //TextView nameEdit = (TextView)findViewById(R.id.test_text);
        //nameEdit.setText("" + rule_id);
    }

    @Override
    protected void onResume() {
        try {
            rulesDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        rulesDatasource.close();
        super.onPause();
    }

    private void updateStartTimeUI() {
        String hour = (startHour > 9) ? ""+startHour: "0"+startHour ;
        String minutes = (startMinutes > 9) ?""+startMinutes : "0"+startMinutes;
        txtTimeStart.setText("START:  "+ hour+":"+minutes);

        String txtTimeMilitary = hour + minutes;
        startCond = Integer.parseInt(txtTimeMilitary);
    }

    private void updateEndTimeUI() {
        String hour = (endHour > 9) ? ""+endHour: "0"+endHour ;
        String minutes = (endMinutes > 9) ?""+endMinutes : "0"+endMinutes;
        txtTimeEnd.setText("END:      "+ hour+":"+minutes);

        String txtTimeMilitary = hour + minutes;
        endCond = Integer.parseInt(txtTimeMilitary);
    }

    private TimePickerDialog showTimePickerDialog(int initialHour, int initialMinutes, boolean is24Hour, TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog dialog = new TimePickerDialog(this, listener, initialHour, initialMinutes, is24Hour);
        dialog.show();
        return dialog;
    }

    public void showTimePickerDialog(View view) {

        switch (view.getId()) {
            case R.id.rule_start_time_view:
                showTimePickerDialog(startHour, startMinutes, true, onTimeSetListener_start);
                break;

            case R.id.rule_end_time_view:
                showTimePickerDialog(endHour, endMinutes, true, onTimeSetListener_end);
                break;

            //case R.id.rule_end_time_view:
                //showTimePickerDialog(startHour, startMinutes, true, onTimeSetListener_end);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("value is", "" + newVal);

    }

    public void showNumberPickerDialog(View view)
    {

        final Dialog d = new Dialog(ProfileRules.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.number_picker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(99);
        np.setMinValue(45);
        np.setValue(70);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setting = np.getValue();
                txtSetting.setText("Temperature: " + setting);
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_rules, menu);
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
                if(editingRule) {
                    rulesDatasource.deleteRule(currentRule);
                    Toast.makeText(context, "Rule Deleted", Toast.LENGTH_SHORT).show();
                    openProfileDetailsActivity();
                }
                break;
            case android.R.id.home:
                openProfileDetailsActivity();
                Log.d("CAD", "home button called for: "+ruleOwner);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //public final static String PROFILE_NAME = "group15.mrthermostat.PROFILE";
    public void openProfileDetailsActivity(){
        Intent intent = new Intent(this, ProfileDetails.class);
        intent.putExtra("PROFILE_NAME", ruleOwner);
        startActivity(intent);
    }

    public void onClick(View view) {
        Context context = getApplicationContext();
        switch (view.getId()) {
            case R.id.SaveProfileButton:
                // save the new comment to the database
                if(editingRule) {
                    currentRule.setStartCondition(startCond);
                    currentRule.setEndCondition(endCond);
                    currentRule.setSetting(setting);

                    rulesDatasource.updateRule(currentRule);

                    Toast.makeText(context, "Rule Updated", Toast.LENGTH_SHORT).show();
                    openProfileDetailsActivity();
                } else {
                    rulesDatasource.createRule(ruleOwner,"Time",startCond,endCond,setting);
                    Toast.makeText(context, "Rule Updated", Toast.LENGTH_SHORT).show();
                    openProfileDetailsActivity();
                }

                /*
                String newProfile = profileName.getText().toString();

                List<Profile> allProfs = profileDatasource.getAllProfiles();
                for (int i = 0; i < allProfs.size(); i++) {
                    if (newProfile.equals(allProfs.get(i).toString())) {
                        profileExists = true;
                        break;
                    }
                }

                if (newProfile.isEmpty()){
                    Toast.makeText(context, "Error: No blank profile names allowed", Toast.LENGTH_SHORT).show();
                } else {
                    if (profileExists) {
                        Toast.makeText(context, "Error: Profile name already in use", Toast.LENGTH_SHORT).show();
                        profileExists = false;
                    } else if (editingProfile) {
                        currentProfile.setName(newProfile);
                        profileDatasource.updateProfile(currentProfile);

                        List<Rule> rules = ruleDatasource.getProfileRules(profile_name);
                        for (int i = 0; i < rules.size(); i++) {
                            Rule tempRule = rules.get(i);
                            tempRule.setProfileName(newProfile);
                            ruleDatasource.updateRule(tempRule);
                        }

                        Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show();
                        openProfilesActivity();
                    } else {
                        profileDatasource.createProfile(newProfile);
                        Toast.makeText(context, "New Profile Created", Toast.LENGTH_SHORT).show();
                        openProfilesActivity();
                    }
                }

                //Intent intent = new Intent(this, Profiles.class);
                //startActivity(intent);

                break;
                */
        }
    }
}
