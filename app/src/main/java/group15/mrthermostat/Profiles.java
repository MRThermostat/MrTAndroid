package group15.mrthermostat;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class Profiles extends ListActivity {

    private ProfilesDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        datasource = new ProfilesDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        Profile profile;
        switch (view.getId()) {
            /*case R.id.Profiles_action_add:
                String[] comments = new String[] { "Weekend Away", "Day Off", "Work From Home" };
                int nextInt = new Random().nextInt(3);
                // save the new comment to the database
                profile = datasource.createProfile(comments[nextInt]);
                adapter.add(profile);
                break;*/
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    profile = (Profile) getListAdapter().getItem(0);
                    datasource.deleteProfile(profile);
                    adapter.remove(profile);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }



    public final static String PROFILE_NAME = "group15.mrthermostat.PROFILE";

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, ProfileDetails.class);
        String profile = String.valueOf(getListView().getItemAtPosition(position));
        intent.putExtra(PROFILE_NAME, profile);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
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
        Profile profile;
        ArrayAdapter<Profile> adapter = (ArrayAdapter<Profile>) getListAdapter();

        switch (id) {
            case R.id.Profiles_action_add:
                /*String[] comments = new String[]{"Weekend Away", "Day Off", "Work From Home"};
                int nextInt = new Random().nextInt(3);
                // save the new comment to the database
                profile = datasource.createProfile(comments[nextInt]);
                adapter.add(profile);
                break;*/
                Intent intent = new Intent(this, ProfileDetails.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}