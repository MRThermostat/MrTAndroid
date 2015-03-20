package group15.mrthermostat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jacob on 2/23/15.
 * Manages the SQLite RDB for the application
 * All tables and columns are defined here
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dataFromTCU.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PROFILES = "profiles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String TABLE_SENSORS = "sensors";
    public static final String COLUMN_TEMPERATURE = "temperature";

    private static final String TABLE_PROFILES_CREATE = "create table " + TABLE_PROFILES
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null);";

    private static final String TABLE_SENSORS_CREATE = "create table " + TABLE_SENSORS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_TEMPERATURE + " integer);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_PROFILES_CREATE);
        database.execSQL(TABLE_SENSORS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        onCreate(db);
    }
}
