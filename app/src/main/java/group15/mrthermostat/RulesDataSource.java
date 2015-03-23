package group15.mrthermostat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 3/23/15.
 */
public class RulesDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_PROFILE_NAME, MySQLiteHelper.COLUMN_TYPE,
            MySQLiteHelper.COLUMN_START_CONDITION, MySQLiteHelper.COLUMN_END_CONDITION,
            MySQLiteHelper.COLUMN_SETTING};

    public RulesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Rule createRule(String profileName, String type, int startCond, int endCond, int setting) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PROFILE_NAME, profileName);
        values.put(MySQLiteHelper.COLUMN_TYPE, type);
        values.put(MySQLiteHelper.COLUMN_START_CONDITION, startCond);
        values.put(MySQLiteHelper.COLUMN_END_CONDITION, endCond);
        values.put(MySQLiteHelper.COLUMN_SETTING, setting);
        long insertId = database.insert(MySQLiteHelper.TABLE_RULES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_RULES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Rule newRule = cursorToName(cursor);
        cursor.close();
        return newRule;
    }

    public void updateRule(Rule rule) {
        long id = rule.getId();
        String profileName = rule.getProfileName();
        String type  = rule.getType();
        int startCond = rule.getStartCondition();
        int endCond = rule.getEndCondition();
        int setting = rule.getSetting();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PROFILE_NAME, profileName);
        values.put(MySQLiteHelper.COLUMN_TYPE, type);
        values.put(MySQLiteHelper.COLUMN_START_CONDITION, startCond);
        values.put(MySQLiteHelper.COLUMN_END_CONDITION, endCond);
        values.put(MySQLiteHelper.COLUMN_SETTING, setting);

        database.update(MySQLiteHelper.TABLE_RULES, values, MySQLiteHelper.COLUMN_ID + " = " + id, null);

        System.out.println("Profile with id: " + id + "updated");
    }

    public void deleteRule(Rule rule) {
        long id = rule.getId();
        database.delete(MySQLiteHelper.TABLE_RULES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
        System.out.println("Profile deleted with id: " + id);
    }

    public List<Rule> getAllRules() {
        List<Rule> rules = new ArrayList<Rule>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RULES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rule rule = cursorToName(cursor);
            rules.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return rules;
    }

    private Rule cursorToName(Cursor cursor) {
        Rule rule = new Rule();
        rule.setId(cursor.getLong(0));
        rule.setProfileName(cursor.getString(1));
        rule.setType(cursor.getString(2));
        rule.setStartCondition(cursor.getInt(3));
        rule.setEndCondition(cursor.getInt(4));
        rule.setSetting(cursor.getInt(5));
        return rule;
    }
}
