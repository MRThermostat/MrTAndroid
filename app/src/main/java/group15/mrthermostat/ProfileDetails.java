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


public class ProfileDetails extends Activity {

    private ProfilesDataSource datasource;
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

        datasource = new ProfilesDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(profile_name != null && !profile_name.isEmpty()) {
            editingProfile = true;
            List<Profile> allProfs = datasource.getAllProfiles();
            for (int i = 0; i < allProfs.size(); i++) {
                currentProfile = allProfs.get(i);
                if (profile_name.equals(currentProfile.toString())) break;
            }
        }


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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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

                 List<Profile> allProfs = datasource.getAllProfiles();
                 for (int i = 0; i < allProfs.size(); i++) {
                     if (newProfile.equals(allProfs.get(i).toString())) {
                         profileExists = true;
                         break;
                     }
                 }

                 if (profileExists) {
                     Toast.makeText(context, "Error: Profile name already in use", Toast.LENGTH_SHORT).show();
                     profileExists = false;
                 } else if (editingProfile) {
                     currentProfile.setName(newProfile);
                     datasource.updateProfile(currentProfile);
                     Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show();
                     openProfilesActivity(view);
                 } else {
                     datasource.createProfile(newProfile);
                     Toast.makeText(context, "New Profile Created", Toast.LENGTH_SHORT).show();
                     openProfilesActivity(view);
                 }

                 //Intent intent = new Intent(this, Profiles.class);
                 //startActivity(intent);

                 break;
        }
    }

    public void openProfilesActivity(View view) {
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

}
