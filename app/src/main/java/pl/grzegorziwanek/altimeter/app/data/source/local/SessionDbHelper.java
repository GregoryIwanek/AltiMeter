package pl.grzegorziwanek.altimeter.app.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Grzegorz Iwanek on 26.01.2017.
 */

public class SessionDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Graphs.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public SessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println(createSQLEntries());
    }

    private String createSQLEntries() {
        return "CREATE TABLE " + SessionDbContract.SessionEntry.TABLE_NAME + " (" +
                SessionDbContract.SessionEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_ALTITUDE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                SessionDbContract.SessionEntry.COLUMN_NAME_RADIUS + TEXT_TYPE +
                " )";
        //CREATE TABLE task (_id TEXT PRIMARY KEY,entryid TEXT,title TEXT,description TEXT,completed INTEGER )
        //CREATE TABLE records (_id TEXT PRIMARY KEY,entryid TEXT,latitude TEXT,longitude TEXT,altitude TEXT,date TEXT,radius TEXT )
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("ENTRIES CREATED");
        db.execSQL(createSQLEntries());
        System.out.println(this);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(SessionDbHelper.class.getSimpleName(), "Upgrading database from version " +
                oldVersion + " to " + newVersion + ", which will destroy all old data...");
        db.execSQL("DROP TABLE IF EXIST " + SessionDbContract.SessionEntry.TABLE_NAME);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
