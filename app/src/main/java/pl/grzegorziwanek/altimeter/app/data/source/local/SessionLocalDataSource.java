package pl.grzegorziwanek.altimeter.app.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.source.SessionDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 27.01.2017.
 */

public class SessionLocalDataSource implements SessionDataSource {

    private static SessionLocalDataSource INSTANCE = null;
    private SessionDbHelper mSessionDbHelper;

    //Private to prevent direct instantiation.
    private SessionLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        System.out.println("will create msessiondbhelper");
        mSessionDbHelper = new SessionDbHelper(context);
        System.out.println("msessiondbhelper created");
    }

    public static SessionLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionLocalDataSource(context);
        }
        return INSTANCE;
    }


    @Override
    public void createSession(@NonNull Session session) {

    }

    @Override
    public void saveSession(@NonNull Session session) {
        checkNotNull(session);
        System.out.println("will call db");
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        System.out.println(db);
        System.out.println("db gotten");

        ContentValues values = new ContentValues();
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID, "12");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_LATITUDE, "1313");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_LONGITUDE, "332");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_ALTITUDE, "043");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_DATE, "21312");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_RADIUS, "543345");
        //CREATE TABLE records (_id TEXT PRIMARY KEY,entryid TEXT,latitude TEXT,longitude TEXT,altitude TEXT,date TEXT,radius TEXT )

        System.out.println("values created");
        db.insert(SessionDbContract.SessionEntry.TABLE_NAME, null, values);
    }

    @Override
    public void getSessions(@NonNull LoadSessionsCallback callback) {

    }

    @Override
    public void refreshSessions() {
        // Not required because the {@link SessionRepository} handles the logic of refreshing the
        // tasks from all the available data sources. This instance is used as a member of
        // {@link SessionRepository}
    }

    //todo-> doesn null null is required? should be different?
    @Override
    public void deleteAllSessions() {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();
        db.delete(SessionDbContract.SessionEntry.TABLE_NAME, null, null);
        db.close();
    }
}
