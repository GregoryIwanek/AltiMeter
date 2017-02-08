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
        mSessionDbHelper = new SessionDbHelper(context);
    }

    public static SessionLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void createNewSession(@NonNull Session session, @NonNull SaveSessionCallback callback) {
        checkNotNull(session);
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        String query =
                "INSERT OR IGNORE INTO " + SessionDbContract.SessionEntry.TABLE_NAME
                        + " (" + SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID + ")"
                        + " VALUES (" + "\""+session.getId() +"\")";
        db.execSQL(query);
        System.out.println(query);
        db.close();

        callback.onNewSessionSaved(session.getId());
    }

    @Override
    public void createRecordsTable(@NonNull Session session) {
        checkNotNull(session);
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();
        int oldVersion = db.getVersion();
        int newVersion = oldVersion + 1;
        SessionDbHelper.setOnUpgrade(session.getId());
        mSessionDbHelper.onUpgrade(db, oldVersion, newVersion);
        db.close();
    }

    @Override
    public void updateSessionData(@NonNull Session session) {
        checkNotNull(session);
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        ContentValues valuesSession = getSessionValues(session);
        ContentValues valuesRecord = getRecordValues(session);

        String rowSelection = SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID + "=" + "\"" + session.getId() + "\"";
        updateRowsDb(db, SessionDbContract.SessionEntry.TABLE_NAME, valuesSession, rowSelection);

        String tableNameRecords = "\"" + session.getId() +"\"";
        insertToDb(db, tableNameRecords, valuesRecord);
        db.close();
    }

    private ContentValues getRecordValues(Session session) {
        ContentValues valuesRecord = new ContentValues();
        valuesRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_LATITUDE, session.getLatitude());
        valuesRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_LONGITUDE, session.getLongitude());
        valuesRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_ALTITUDE, session.getCurrentElevation());
        valuesRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_DATE, session.getCurrentLocation().getTime());
        valuesRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_ADDRESS, session.getAddress());
        valuesRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_DISTANCE, session.getDistance());
        return valuesRecord;
    }

    private ContentValues getSessionValues(Session session) {
        ContentValues valuesSession = new ContentValues();
        valuesSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE, session.getCurrentElevation());
        valuesSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_MAX_HEIGHT, session.getMaxHeight());
        valuesSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_MIN_HEIGHT, session.getMinHeight());
        valuesSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ADDRESS, session.getAddress());
        valuesSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_DISTANCE, session.getDistance());
        return valuesSession;
    }

    private void insertToDb(SQLiteDatabase db, String tableName, ContentValues values) {
        db.insert(tableName, null, values);
    }

    private void updateRowsDb(SQLiteDatabase db, String tableName, ContentValues values, String where) {
        db.update(tableName, values, where, null);
    }

    @Override
    public void getSessions(@NonNull LoadSessionsCallback callback) {
        List<Session> sessions = new ArrayList<>();
        SQLiteDatabase db = mSessionDbHelper.getReadableDatabase();

        String[] projection = {
                SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID,
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE,
                SessionDbContract.SessionEntry.COLUMN_NAME_MAX_HEIGHT,
                SessionDbContract.SessionEntry.COLUMN_NAME_MIN_HEIGHT,
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ADDRESS,
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_DISTANCE
        };

        Cursor c = db.query(
                SessionDbContract.SessionEntry.TABLE_NAME, projection, null, null, null, null, null
        );

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID));
//                String itemLatitude = c.getString(c.getColumnIndexOrThrow(SessionDbContract.RecordsEntry.COLUMN_NAME_LATITUDE));
//                String itemLongitue = c.getString(c.getColumnIndexOrThrow(SessionDbContract.RecordsEntry.COLUMN_NAME_LONGITUDE));
//                String itemAltitude = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_ALTITUDE));
//                String itemDate = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_DATE));
//                String itemRadius = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_RADIUS));
                Session session = new Session("DADA", "WRWA", itemId);
                sessions.add(session);
            }
        }
        if (c != null) {
            c.close();
        }

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
    public void deleteCheckedSessions(ArrayList<String> sessionsId) {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();
        for (String sessionId : sessionsId) {
            db.execSQL("DROP TABLE IF EXISTS " + setProperName(sessionId));
            db.execSQL("delete from "+SessionDbContract.SessionEntry.TABLE_NAME + " where "
            + SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID +"=" + setProperName(sessionId));
        }
        db.close();
    }

    private String setProperName(String id) {
        return "\"" + id + "\"";
    }

    @Override
    public void refreshSessions() {
        // Not required because the {@link SessionRepository} handles the logic of refreshing the
        // tasks from all the available data sources. This instance is used as a member of
        // {@link SessionRepository}
    }

    //todo-> does null null is required? should be different?
    //todo -> refactor code here-> deletion of all other tables with records
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
