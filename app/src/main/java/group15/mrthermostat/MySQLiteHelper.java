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
    private static final int DATABASE_VERSION = 6;

    public static final String TABLE_PROFILES = "profiles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ACTIVE = "active";

    public static final String TABLE_SENSORS = "sensors";
    public static final String COLUMN_TEMPERATURE = "temperature";

    public static final String TABLE_RULES = "rules";
    public static final String COLUMN_PROFILE_NAME = "Profile_Name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_START_CONDITION = "start_condition";
    public static final String COLUMN_END_CONDITION = "end_condition";
    public static final String COLUMN_SETTING = "setting";

    private static final String TABLE_PROFILES_CREATE = "create table " + TABLE_PROFILES
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_ACTIVE + " integer);";

    private static final String TABLE_SENSORS_CREATE = "create table " + TABLE_SENSORS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_TEMPERATURE + " integer);";

    private static final String TABLE_RULES_CREATE = "create table " + TABLE_RULES
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PROFILE_NAME + " text not null, "
            + COLUMN_TYPE + " text not null, "
            + COLUMN_START_CONDITION + " integer, "
            + COLUMN_END_CONDITION + " integer, "
            + COLUMN_SETTING + " integer);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_PROFILES_CREATE);
        database.execSQL(TABLE_SENSORS_CREATE);
        database.execSQL(TABLE_RULES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULES);
        onCreate(db);
    }
}
