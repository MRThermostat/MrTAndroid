package group15.mrthermostat;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by jacob on 3/21/15
 * Manages all preferences for the application (name is misleading)
 * City, TCU IP address, and login credentials.
 */
public class CityPreference {
    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Cincinnati, OH");
    }
    void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

    String getIP() { return prefs.getString("tcuIP", "127.0.0.0");}
    void setIP(String tcuIP) { prefs.edit().putString("tcuIP", tcuIP).apply();}

    String getUsername() { return prefs.getString("username", "username");}
    void setUsername(String username) { prefs.edit().putString("username", username).apply();}

    String getPassword() { return prefs.getString("password", "password");}
    void setPassword(String password) { prefs.edit().putString("password", password).apply();}
}
