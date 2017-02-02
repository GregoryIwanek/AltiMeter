package pl.grzegorziwanek.altimeter.app.model.database.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionDataSource;

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
    public void saveSession(@NonNull Session session,@NonNull SaveSessionCallback callback) {
        checkNotNull(session);
        System.out.println("will call db");
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        System.out.println(db);
        System.out.println("db gotten");

        ContentValues values = new ContentValues();
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID, session.getId());
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_LATITUDE, "1313");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_LONGITUDE, "332");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_ALTITUDE, "043");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_DATE, "21312");
        values.put(SessionDbContract.SessionEntry.COLUMN_NAME_RADIUS, "543345");
        //CREATE TABLE records (_id TEXT PRIMARY KEY,entryid TEXT,latitude TEXT,longitude TEXT,altitude TEXT,date TEXT,radius TEXT )

        System.out.println("values created");
        db.insert(SessionDbContract.SessionEntry.TABLE_NAME, null, values);

        callback.onNewSessionSaved(session.getId());
    }

    public void updateSessionData() {
        //TODO -> make that to update columns and refactor saveSession to just save id of new session into database
    }

    @Override
    public void getSessions(@NonNull LoadSessionsCallback callback) {
        System.out.println("GET SESSIONS LOCAL CALLED");
        List<Session> sessions = new ArrayList<>();
        SQLiteDatabase db = mSessionDbHelper.getReadableDatabase();

        String[] projection = {
                SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID,
                SessionDbContract.SessionEntry.COLUMN_NAME_LATITUDE,
                SessionDbContract.SessionEntry.COLUMN_NAME_LONGITUDE,
                SessionDbContract.SessionEntry.COLUMN_NAME_ALTITUDE,
                SessionDbContract.SessionEntry.COLUMN_NAME_DATE,
                SessionDbContract.SessionEntry.COLUMN_NAME_RADIUS
        };

        Cursor c = db.query(
                SessionDbContract.SessionEntry.TABLE_NAME, projection, null, null, null, null, null
        );

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID));
                String itemLatitude = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_LATITUDE));
                String itemLongitue = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_LONGITUDE));
//                String itemAltitude = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_ALTITUDE));
//                String itemDate = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_DATE));
//                String itemRadius = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_RADIUS));
                Session session = new Session(itemLatitude, itemLongitue, itemId);
                sessions.add(session);
                System.out.println("SESSION OBJECT ADDED TO LIST IN LOOP SessionLocalDataSource");
            }
        }
        if (c != null) {
            c.close();
        }

        System.out.println("GET VALUES CALLED close db");
        db.close();

        if (sessions.isEmpty()) {
            // This will be called if the table is new or just empty...
            callback.onDataNotAvailable();
        } else {
            callback.onSessionLoaded(sessions);
        }
    }

    @Override
    public void clearSessionData(@NonNull String sessionId) {

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

    @Override
    public void deleteSession(@NonNull String sessionId) {

    }
}
