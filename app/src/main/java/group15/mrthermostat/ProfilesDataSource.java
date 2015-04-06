package group15.mrthermostat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 2/23/15.
 * Contains functions and fields used for manipulation of the Profile table
 */
public class ProfilesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_ACTIVE,
            MySQLiteHelper.COLUMN_TID};

    public ProfilesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //public Profile createProfile(String name, int active, int tId) {
    public Profile createProfile(String name) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_ACTIVE, 0);
        values.put(MySQLiteHelper.COLUMN_TID, 0);

        long insertId = database.insert(MySQLiteHelper.TABLE_PROFILES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PROFILES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Profile newProfile = cursorToName(cursor);
        cursor.close();
        return newProfile;
    }

    public Profile createProfile(String name, int active) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_ACTIVE, active);
        values.put(MySQLiteHelper.COLUMN_TID, 0);

        long insertId = database.insert(MySQLiteHelper.TABLE_PROFILES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PROFILES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Profile newProfile = cursorToName(cursor);
        cursor.close();
        return newProfile;
    }

    public void updateProfile(Profile profile) {
        long id = profile.getId();
        String name = profile.getName();
        int active = profile.getActive();
        int tId  = profile.getTId();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_ACTIVE, active);
        values.put(MySQLiteHelper.COLUMN_TID, tId);

        database.update(MySQLiteHelper.TABLE_PROFILES, values, MySQLiteHelper.COLUMN_ID + " = " + id, null);

        System.out.println("Profile with id: " + id + ": name updated to " + name);
    }

    public void deleteProfile(Profile profile) {
        long id = profile.getId();
        System.out.println("Profile deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_PROFILES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<Profile>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PROFILES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Profile profile = cursorToName(cursor);
            profiles.add(profile);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return profiles;
    }

    private Profile cursorToName(Cursor cursor) {
        Profile profile = new Profile();
        profile.setId(cursor.getLong(0));
        profile.setName(cursor.getString(1));
        profile.setActive(cursor.getInt(2));
        profile.settId(cursor.getInt(3));
        return profile;
    }
}
