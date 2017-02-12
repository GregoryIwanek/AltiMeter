package pl.grzegorziwanek.altimeter.app.model.database.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.model.Details;
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

        String insertOrIgnore = mSessionDbHelper.queryInsertOrIgnore(session.getId());
        db.execSQL(insertOrIgnore);
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
        ContentValues vRecord = new ContentValues();
        vRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_LATITUDE, session.getLatitude());
        vRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_LONGITUDE, session.getLongitude());
        vRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_ALTITUDE, session.getCurrentElevation());
        vRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_DATE, session.getCurrentLocation().getTime());
        vRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_ADDRESS, session.getAddress());
        vRecord.put(SessionDbContract.RecordsEntry.COLUMN_NAME_DISTANCE, session.getDistance());
        return vRecord;
    }

    private ContentValues getSessionValues(Session session) {
        ContentValues vSession = new ContentValues();
        vSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE, session.getCurrentElevation());
        vSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_MAX_HEIGHT, session.getMaxHeight());
        vSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_MIN_HEIGHT, session.getMinHeight());
        vSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ADDRESS, session.getAddress());
        vSession.put(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_DISTANCE, session.getDistance());
        return vSession;
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

        callback.onSessionLoaded(sessions);
    }

    @Override
    public void clearSessionData(@NonNull String sessionId) {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();
        db.execSQL(mSessionDbHelper.queryClearRecordsTable(sessionId));
        db.close();
    }

    @Override
    public void deleteSessions(ArrayList<String> sessionsId, boolean isDeleteAll,
                               @Nullable DeleteSessionCallback callback) {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();
        for (String sessionId : sessionsId) {
            db.execSQL(mSessionDbHelper.queryDeleteTables(sessionId));
            db.execSQL(mSessionDbHelper.queryDeleteRows(sessionId));
        }
        db.close();
    }

    @Override
    public void setSessionChecked(String sessionId, boolean isCompleted) {
        //TODO-> add column to table records, set there if session completed, change that after this is called
    }

    @Override
    public void refreshSessions() {
        // Not required because the {@link SessionRepository} handles the logic of refreshing the
        // tasks from all the available data sources. This instance is used as a member of
        // {@link SessionRepository}
    }

    /** //TODO-> get details of clicked object in SessionFragment list and print in new DetailsFragment
     * 1-> getDetails(...)
     * 1.1-> call for new Details object and populate it depending on given parameter (sessionId)
     * 1.2-> call back gotten Details object to SessionPresenter instance
     *
     * 2-> populateDetails(...)
     * 2.1-> get readable database to read from
     * 2.2.1-> get information from table "sessions"
     * 2.2.2-> formulate projection String[] with names of columns and cursor with query
     * 2.2.3-> fetch data from cursor and populate Details member fields
     * 2.2.4-> close cursor, keep database open
     *
     * 2.3.1-> get information from corresponding table "records"
     * 2.3.2-> connect to corresponding table by using "sessionsId"
     * 2.3.3-> formulate projection String[] with names of columns and cursor with query, add clause "WHERE"
     * 2.3.4-> fetch demanded rows form table (first and last row's recording time, number of records)
     * 2.3.5-> fetch data from cursor and populate missing Details member fields
     * 2.3.6-> close cursor, close database
     *
     * 3-> Or use RxJava and stop to fucking around with that super lame Cursor classes??
     */
    @Override
    public void getDetails(@NonNull String sessionId, @NonNull DetailsSessionCallback callback) {

        Details details = populateDetails(sessionId);
        Bundle args = new Bundle();
        callback.onDetailsLoaded(args);
    }

    private Details populateDetails(String sessionId) {
        SQLiteDatabase db = mSessionDbHelper.getReadableDatabase();
        Details details = new Details();

        //TABLE SESSIONS
        String[] projectionSession = {
                SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID,
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE,
                SessionDbContract.SessionEntry.COLUMN_NAME_MAX_HEIGHT,
                SessionDbContract.SessionEntry.COLUMN_NAME_MIN_HEIGHT,
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ADDRESS,
                SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_DISTANCE
        };

        String cursorSelection = SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID
                + "=" + mSessionDbHelper.setProperName(sessionId);
        Cursor c = db.query(
                SessionDbContract.SessionEntry.TABLE_NAME, projectionSession,
                cursorSelection, null, null, null, null
        );

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_ENTRY_ID));
                String itemAlt = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE));
                String itemMaxHeight = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_MAX_HEIGHT));
                String itemMinHeight = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_MIN_HEIGHT));
                String itemAddress = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_ADDRESS));
                String itemDistance = c.getString(c.getColumnIndexOrThrow(SessionDbContract.SessionEntry.COLUMN_NAME_CURRENT_DISTANCE));
                details.setTitle("Title. TODO");
                details.setDescription("Description. TODO");
                details.setUniqueId(itemId);
                details.setDistance(itemDistance);
                details.setMinHeight(itemMinHeight);
                details.setMaxHeight(itemMaxHeight);
                details.setLastAddress(itemAddress);
            }
        }
        if (c != null) {
            c.close();
        }

        //TABLE RECORDS
        String[] projectionRecords = {
                SessionDbContract.RecordsEntry.COLUMN_NAME_DATE
        };

        Cursor cRecords = db.query(
                mSessionDbHelper.setProperName(sessionId), projectionRecords,
                null, null, null, null, null);
        int numb = cRecords.getCount();
        details.setNumOfPoints(numb);

        cRecords.moveToFirst();
        String dateStart = cRecords.getString(cRecords.getColumnIndexOrThrow(SessionDbContract.RecordsEntry.COLUMN_NAME_DATE));
        cRecords.moveToLast();
        String dateEnd = cRecords.getString(cRecords.getColumnIndexOrThrow(SessionDbContract.RecordsEntry.COLUMN_NAME_DATE));
        details.setTimeStart(dateStart);
        details.setTimeEnd(dateEnd);

        return details;
    }

    private Bundle setDetailsBundle(Details details) {
        return new Bundle();
    }
}
