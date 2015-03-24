package group15.mrthermostat;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;


public class Profiles extends ListActivity {

    private ProfilesDataSource datasource;
    Profile activeProfile;

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

        List<Profile> allProfs = datasource.getAllProfiles();

        for (int i = 0; i < allProfs.size(); i++) {
            activeProfile = allProfs.get(i);
            if (activeProfile.getActive()!=0) break;
        }
        TextView txtActiveProfile = (TextView)findViewById(R.id.active_profile);
        txtActiveProfile.setText(activeProfile.getName());

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Profile> adapter = new ArrayAdapter<Profile>(this,
                android.R.layout.simple_list_item_1, allProfs);
        setListAdapter(adapter);
    }

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    /*public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Profile> adapter = (ArrayAdapter<Profile>) getListAdapter();
        Profile profile;
        switch (view.getId()) {

        }
        adapter.notifyDataSetChanged();
    }
    */


    //public final static String PROFILE_NAME = "group15.mrthermostat.PROFILE";

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, ProfileDetails.class);
        String profileName = String.valueOf(getListView().getItemAtPosition(position));
        intent.putExtra("PROFILE_NAME", profileName);
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

        switch (id) {
            case R.id.Profiles_action_add:
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