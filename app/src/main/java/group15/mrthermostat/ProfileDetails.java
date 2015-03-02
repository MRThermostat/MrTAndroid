package group15.mrthermostat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class ProfileDetails extends Activity {

    private ProfilesDataSource datasource;

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
        switch (view.getId()) {
             case R.id.SaveProfileButton:
                // save the new comment to the database
                String newProfile = profileName.getText().toString();
                datasource.createProfile(newProfile);

                 //Context context = getApplicationContext();
                 //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                 //Intent intent = new Intent(this, Profiles.class);
                 //startActivity(intent);

                break;
        }
    }
}
