package group15.mrthermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Random;


public class Profiles extends Activity {

    private ProfilesDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        datasource = new ProfilesDataSource(this);
        datasource.open();

        List<Profile> values = datasource.getAllProfiles();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Profile> adapter = new ArrayAdapter<Profile>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Profile> adapter = (ArrayAdapter<Profile>) getListAdapter();
        Profile profile = null;
        switch (view.getId()) {
            case R.id.add:
                String[] comments = new String[] { "Weekend Away", "Day Off", "Work From Home" };
                int nextInt = new Random().nextInt(3);
                // save the new comment to the database
                profile = datasource.createProfile(comments[nextInt]);
                adapter.add(profile);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    profile = (Profile) getListAdapter().getItem(0);
                    datasource.deleteComment(profile);
                    adapter.remove(profile);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    public void openProfileDetailsActivity(View view) {
        Intent intent = new Intent(this, ProfileDetails.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profiles, menu);
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
}