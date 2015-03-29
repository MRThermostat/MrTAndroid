package group15.mrthermostat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 3/19/15.
 * Contains functions and fields used for manipulation of the Sensor table
 */
public class SensorsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_TEMPERATURE,
            MySQLiteHelper.COLUMN_ACTIVE, MySQLiteHelper.COLUMN_TID};

    public SensorsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Sensor createSensor(String name, int temp, int active, int tId) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_TEMPERATURE, temp);
        values.put(MySQLiteHelper.COLUMN_ACTIVE, active);
        values.put(MySQLiteHelper.COLUMN_ACTIVE, tId);

        long insertId = database.insert(MySQLiteHelper.TABLE_SENSORS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SENSORS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Sensor newSensor = cursorToName(cursor);
        cursor.close();
        return newSensor;
    }

    public void updateSensor(Sensor sensor) {
        long id = sensor.getId();
        String name = sensor.getName();
        int temp = sensor.getTemp();
        int active = sensor.getActive();
        int tId = sensor.getTId();

        ContentValues args = new ContentValues();
        args.put(MySQLiteHelper.COLUMN_NAME, name);
        args.put(MySQLiteHelper.COLUMN_TEMPERATURE, temp);
        args.put(MySQLiteHelper.COLUMN_ACTIVE, active);
        args.put(MySQLiteHelper.COLUMN_TID, tId);

        database.update(MySQLiteHelper.TABLE_SENSORS, args, MySQLiteHelper.COLUMN_ID + " = " + id, null);

        System.out.println("Profile with id: " + id + ": name updated to " + name + " -and- " +
                "temp updated to " + temp);
    }

    public void deleteSensor(Sensor sensor) {
        long id = sensor.getId();
        System.out.println("Profile deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_SENSORS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Sensor> getAllSensors() {
        List<Sensor> sensors = new ArrayList<Sensor>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SENSORS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sensor sensor = cursorToName(cursor);
            sensors.add(sensor);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return sensors;
    }

    private Sensor cursorToName(Cursor cursor) {
        Sensor sensor = new Sensor();
        sensor.setId(cursor.getLong(0));
        sensor.setName(cursor.getString(1));
        sensor.setTemp(cursor.getInt(2));
        sensor.setActive(cursor.getInt(3));
        sensor.setTId(cursor.getInt(4));
        return sensor;
    }
}
