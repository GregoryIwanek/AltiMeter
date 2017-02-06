package pl.grzegorziwanek.altimeter.app.model.database.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Grzegorz Iwanek on 26.01.2017.
 */

public class SessionDbHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Graphs.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static String DROP_TABLE = "DROP TABLE IF EXIST ";
    private static String CREATE_TABLE = "CREATE TABLE ";
    private static String ON_UPGRADE = "";

    public SessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static String createSQLSessionsEntries() {
        return CREATE_TABLE + SessionDbContract.SessionEntry.TABLE_NAME + " (" +
                SessionDbContract.SessionEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE + REAL_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_MAX_HEIGHT + REAL_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_MIN_HEIGHT + REAL_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ADDRESS + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_DISTANCE + REAL_TYPE +
                " )";
    }

    private static String createSQLRecordEntries(String sessionId) {
        return CREATE_TABLE + "\"" + sessionId + "\"" + " (" +
                SessionDbContract.RecordsEntry.COLUMN_NAME_ENTRY_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL," +
                SessionDbContract.RecordsEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.RecordsEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.RecordsEntry.COLUMN_NAME_ALTITUDE + REAL_TYPE + COMMA_SEP +
                SessionDbContract.RecordsEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.RecordsEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.RecordsEntry.COLUMN_NAME_DISTANCE + REAL_TYPE +
                " )";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(createSQLSessionsEntries());
        System.out.println(createSQLRecordEntries("dadadadadadadadadadada"));
        db.execSQL(createSQLSessionsEntries());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(SessionDbHelper.class.getSimpleName(), "Upgrading database from version " +
                oldVersion + " to " + newVersion + ", which will destroy all old data...");
        System.out.println("onUpgrade is: " + ON_UPGRADE);
        db.execSQL(ON_UPGRADE);
        db.setVersion(newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static String getDropTable() {
        return DROP_TABLE;
    }

    public static void setDropTable(String dropTable) {
        DROP_TABLE = dropTable;
    }

    public static String getCreateTable() {
        return CREATE_TABLE;
    }

    public static void setCreateTable(String sessionId) {
        ON_UPGRADE = createSQLRecordEntries(sessionId);
    }

    public static String getOnUpgrade() {
        return ON_UPGRADE;
    }

    public static void setOnUpgrade(String sessionId) {
        ON_UPGRADE = createSQLRecordEntries(sessionId);
        System.out.println("ON_UPGRADE IS: " + ON_UPGRADE);
    }

    public void setDatabaseVersion(int newVersion) {
        DATABASE_VERSION = newVersion;
    }
}
