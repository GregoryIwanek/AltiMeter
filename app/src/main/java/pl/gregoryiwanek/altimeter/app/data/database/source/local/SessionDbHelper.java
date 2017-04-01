package pl.gregoryiwanek.altimeter.app.data.database.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static pl.gregoryiwanek.altimeter.app.data.database.source.local.SessionDbContract.*;

/**
 * Consists helper class SQLite helper. Works as helper class to perform direct operations
 * on the database values.
 */
class SessionDbHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Graphs.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String UNIQUE = " UNIQUE";
    private static final String COMMA_SEP = ",";

    private static String CREATE_TABLE = "CREATE TABLE ";
    private static String ON_UPGRADE = "";

    SessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static String createSQLSessionsEntries() {
        return CREATE_TABLE + SessionEntry.TABLE_NAME + " (" +
                SessionEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                SessionEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + UNIQUE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE + REAL_TYPE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_MAX_HEIGHT + REAL_TYPE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_MIN_HEIGHT + REAL_TYPE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_CURRENT_ADDRESS + TEXT_TYPE + COMMA_SEP +
                SessionEntry.COLUMN_NAME_CURRENT_DISTANCE + REAL_TYPE +
                " )";
    }

    private static String createSQLRecordEntries(String sessionId) {
        return CREATE_TABLE + "IF NOT EXISTS" + "\"" + sessionId + "\"" + " (" +
                RecordsEntry.COLUMN_NAME_ENTRY_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL," +
                RecordsEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                RecordsEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                RecordsEntry.COLUMN_NAME_ALTITUDE + REAL_TYPE + COMMA_SEP +
                RecordsEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                RecordsEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                RecordsEntry.COLUMN_NAME_DISTANCE + REAL_TYPE +
                " )";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSQLSessionsEntries());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(SessionDbHelper.class.getSimpleName(), "Upgrading database from version " +
                oldVersion + " to " + newVersion + ", which will destroy all old data...");
        db.execSQL(ON_UPGRADE);
        db.setVersion(newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    static void setOnUpgrade(String sessionId) {
        ON_UPGRADE = createSQLRecordEntries(sessionId);
    }

    String queryDeleteTables(String tableName) {
        return "DROP TABLE IF EXISTS " + setProperName(tableName);
    }

    String queryDeleteRows(String tableName) {
        return "delete from "+ SessionEntry.TABLE_NAME + " where "
                + SessionEntry.COLUMN_NAME_ENTRY_ID +"=" + setProperName(tableName);
    }

    String queryClearRecordsTable(String tableName) {
        return "delete from " + setProperName(tableName);
    }

    String queryInsertOrIgnore(String rowValue) {
        return "INSERT OR IGNORE INTO " + SessionEntry.TABLE_NAME
                + " (" + SessionEntry.COLUMN_NAME_ENTRY_ID + ")"
                + " VALUES (" + setProperName(rowValue) +")";
    }

    String setProperName(String name) {
        return "\"" + name + "\"";
    }

    String[] getProjectionsSessions() {
        return new String[] {
                SessionEntry.COLUMN_NAME_ENTRY_ID,
                SessionEntry.COLUMN_NAME_TITLE,
                SessionEntry.COLUMN_NAME_DESCRIPTION,
                SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE,
                SessionEntry.COLUMN_NAME_MAX_HEIGHT,
                SessionEntry.COLUMN_NAME_MIN_HEIGHT,
                SessionEntry.COLUMN_NAME_CURRENT_ADDRESS,
                SessionEntry.COLUMN_NAME_CURRENT_DISTANCE
        };
    }

    String[] getProjectionsRecordsDate() {
        return new String[] {
                RecordsEntry.COLUMN_NAME_DATE
        };
    }

    String[] getProjectionRecordsLatLng() {
        return new String[] {
                RecordsEntry.COLUMN_NAME_LATITUDE,
                RecordsEntry.COLUMN_NAME_LONGITUDE
        };
    }
}
