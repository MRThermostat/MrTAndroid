package group15.mrthermostat;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;


public class ProfileDetails extends ListActivity {

    private ProfilesDataSource profileDatasource;
    private RulesDataSource ruleDatasource;
    Profile currentProfile;
    Boolean profileExists = false;
    Boolean editingProfile = false;
    String profile_name;
    List<Profile> allProfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        Intent intent = getIntent();
        profile_name = intent.getStringExtra("PROFILE_NAME");
        Log.d("CAD", "String EXTRA is: " + profile_name);

        EditText nameEdit = (EditText)findViewById(R.id.profileNameEdit);
        nameEdit.setText(profile_name, TextView.BufferType.EDITABLE);

        profileDatasource = new ProfilesDataSource(this);
        try {
            profileDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        allProfs = profileDatasource.getAllProfiles();

        if(profile_name != null && !profile_name.isEmpty()) {
            editingProfile = true;
            for (int i = 0; i < allProfs.size(); i++) {
                currentProfile = allProfs.get(i);
                if (profile_name.equals(currentProfile.toString())) break;
            }
        }

        ruleDatasource = new RulesDataSource(this);
        try {
            ruleDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Rule> rules = ruleDatasource.getProfileRules(profile_name);
        RuleListArrayAdapter adapter = new RuleListArrayAdapter(this, rules);
        setListAdapter(adapter);

    }

    @Override
    protected void onResume() {
        try {
            profileDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ruleDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        profileDatasource.close();
        ruleDatasource.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_details, menu);
        return true;
    }

    public final static String RULE_OWNER = "group15.mrthermostat.RuleOwner";

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
                if(editingProfile) {
                    profileDatasource.deleteProfile(currentProfile);
                    Toast.makeText(context, "Profile Deleted", Toast.LENGTH_SHORT).show();
                    openProfilesActivity();

                    List<Rule> rules = ruleDatasource.getProfileRules(profile_name);
                    for (int i = 0; i < rules.size(); i++) {
                        Rule tempRule = rules.get(i);
                        ruleDatasource.deleteRule(tempRule);
                    }
                }
                /*if (getListAdapter().getCount() > 0) {
                    rule = (Rule) getListAdapter().getItem(0);
                    ruleDatasource.deleteRule(rule);
                    adapter.remove(rule);
                }*/
                break;
            case R.id.action_add:
                /*int[] testRules = new int[] { 800, 1200, 1400, 1800, 2200, 200, 400, 600};
                int[] testTemps = new int[] {50,55,60,65,70,75,80,85};
                int nextInt = new Random().nextInt(8);
                int nextInt2 = new Random().nextInt(8);
                String profName = currentProfile.getName();

                rule = ruleDatasource.createRule(profName, "Time", testRules[nextInt], testRules[nextInt2], testTemps[nextInt]);
                adapter.add(rule);*/


                if(editingProfile) {
                    String profName = currentProfile.getName();

                    Intent intent = new Intent(this, ProfileRules.class);
                    intent.putExtra(RULE_OWNER, profName);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Rules can only be added to existing Profiles", Toast.LENGTH_SHORT).show();
                }

                // save the new comment to the database
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    public void onClick(View view) {
        EditText profileName = (EditText)findViewById(R.id.profileNameEdit);
        Context context = getApplicationContext();
        switch (view.getId()) {
             case R.id.SaveProfileButton:
                 // save the new comment to the database

                 String newProfile = profileName.getText().toString();

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
            case R.id.set_profile_active_button:
                if(editingProfile){
                    for (int i = 0; i < allProfs.size(); i++) {
                        Profile tempProfile = allProfs.get(i);
                        tempProfile.setActive(0);
                        profileDatasource.updateProfile(tempProfile);
                    }
                    currentProfile.setActive(1);
                    profileDatasource.updateProfile(currentProfile);
                    Toast.makeText(context, "Profile set to Active", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }

    public final static String RULE_ID = "group15.mrthermostat.Rule";

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, ProfileRules.class);

        Rule rule = (Rule) l.getItemAtPosition(position);
        long ruleID = rule.getId();
        intent.putExtra(RULE_ID, ruleID);


        startActivity(intent);
    }

    public void openProfilesActivity() {
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

    /*public void openProfileRulesActivity() {
        Intent intent = new Intent(this, ProfileRules.class);
        startActivity(intent);
    }*/

}
