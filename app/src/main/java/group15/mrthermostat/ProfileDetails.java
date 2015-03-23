package group15.mrthermostat;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class ProfileDetails extends ListActivity {

    private ProfilesDataSource profileDatasource;
    private RulesDataSource ruleDatasource;
    Profile currentProfile;
    Boolean profileExists = false;
    Boolean editingProfile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        Intent intent = getIntent();
        String profile_name = intent.getStringExtra(Profiles.PROFILE_NAME);

        EditText nameEdit = (EditText)findViewById(R.id.profileNameEdit);
        nameEdit.setText(profile_name, TextView.BufferType.EDITABLE);

        profileDatasource = new ProfilesDataSource(this);
        try {
            profileDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(profile_name != null && !profile_name.isEmpty()) {
            editingProfile = true;
            List<Profile> allProfs = profileDatasource.getAllProfiles();
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

        List<Rule> values = ruleDatasource.getAllRules();
        RuleListArrayAdapter adapter = new RuleListArrayAdapter(this, values);
        setListAdapter(adapter);

    }

    public void openProfileRulesActivity(View view) {
        Intent intent = new Intent(this, ProfileRules.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Context context = getApplicationContext();
        @SuppressWarnings("unchecked")
        ArrayAdapter<Rule> adapter = (ArrayAdapter<Rule>) getListAdapter();
        Rule rule;

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_delete:
                /*if(editingProfile) {
                    profileDatasource.deleteProfile(currentProfile);
                    Toast.makeText(context, "Profile Deleted", Toast.LENGTH_SHORT).show();
                    openProfilesActivity();
                }*/
                if (getListAdapter().getCount() > 0) {
                    rule = (Rule) getListAdapter().getItem(0);
                    ruleDatasource.deleteRule(rule);
                    adapter.remove(rule);
                }
                break;
            case R.id.action_add:
                int[] testRules = new int[] { 800, 1200, 1400, 1800, 2200, 200, 400, 600};
                int[] testTemps = new int[] {50,55,60,65,70,75,80,85};
                int nextInt = new Random().nextInt(8);
                // save the new comment to the database
                rule = ruleDatasource.createRule("Testing", "Type", testRules[nextInt], testRules[nextInt], testTemps[nextInt]);
                adapter.add(rule);
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
        }
    }

    public void openProfilesActivity() {
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

}
