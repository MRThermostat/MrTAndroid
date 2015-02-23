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
 */
public class ProfilesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME };

    public ProfilesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Profile createProfile(String name) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
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

    public void deleteProfile(Profile name) {
        long id = name.getId();
        System.out.println("Profile deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_PROFILES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Profile> getAllProfiles() {
        List<Profile> names = new ArrayList<Profile>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PROFILES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Profile name = cursorToName(cursor);
            names.add(name);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return names;
    }

    private Profile cursorToName(Cursor cursor) {
        Profile name = new Profile();
        name.setId(cursor.getLong(0));
        name.setComment(cursor.getString(1));
        return name;
    }
}
