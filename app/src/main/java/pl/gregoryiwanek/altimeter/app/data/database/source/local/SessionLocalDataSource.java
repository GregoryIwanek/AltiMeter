package pl.gregoryiwanek.altimeter.app.data.database.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.gregoryiwanek.altimeter.app.data.Session;
import pl.gregoryiwanek.altimeter.app.data.database.source.SessionDataSource;
import pl.gregoryiwanek.altimeter.app.data.database.source.local.SessionDbContract.SessionEntry;
import pl.gregoryiwanek.altimeter.app.utils.Constants;
import pl.gregoryiwanek.altimeter.app.utils.FormatAndValueConverter;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.gregoryiwanek.altimeter.app.data.database.source.local.SessionDbContract.*;

/**
 * Consists
 */
public class SessionLocalDataSource implements SessionDataSource {

    private static SessionLocalDataSource INSTANCE = null;
    private SessionDbHelper mSessionDbHelper;

    //Private to prevent direct instantiation.
    private SessionLocalDataSource(@NonNull Context context) {
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
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        session.setTitle(adjustStrIfEmpty(session.getTitle()));
        session.setDescription(adjustStrIfEmpty(session.getDescription()));

        String insertOrIgnore = mSessionDbHelper.queryInsertOrIgnore(session.getId());
        db.execSQL(insertOrIgnore);

        updateSessionRow(db, session);

        db.close();

        callback.onNewSessionSaved(session.getId());
    }

    @Override
    public void createRecordsTable(@NonNull Session session) {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();
        int oldVersion = db.getVersion();
        int newVersion = oldVersion + 1;
        SessionDbHelper.setOnUpgrade(session.getId());
        mSessionDbHelper.onUpgrade(db, oldVersion, newVersion);
        db.close();
    }

    @Override
    public void updateSessionData(@NonNull Session session) {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        updateSessionRow(db, session);
        updateRecordsRow(db, session);

        db.close();
    }

    private void updateSessionRow(SQLiteDatabase db, Session session) {
        ContentValues valuesSession = getSessionValues(session);
        String rowSelection = SessionEntry.COLUMN_NAME_ENTRY_ID + "=" + "\"" + session.getId() + "\"";
        updateRowsDb(db, SessionEntry.TABLE_NAME, valuesSession, rowSelection);
    }

    private void updateRecordsRow(SQLiteDatabase db, Session session) {
        ContentValues valuesRecord = getRecordValues(session);
        String tableNameRecords = "\"" + session.getId() +"\"";
        insertToDb(db, tableNameRecords, valuesRecord);
    }

    private ContentValues getRecordValues(Session session) {
        ContentValues vRecord = new ContentValues();
        vRecord.put(RecordsEntry.COLUMN_NAME_LATITUDE, session.getLatNumericStr());
        vRecord.put(RecordsEntry.COLUMN_NAME_LONGITUDE, session.getLongNumericStr());
        vRecord.put(RecordsEntry.COLUMN_NAME_ALTITUDE, session.getCurrentElevation());
        vRecord.put(RecordsEntry.COLUMN_NAME_DATE, session.getCurrentLocation().getTime());
        vRecord.put(RecordsEntry.COLUMN_NAME_ADDRESS, session.getAddress());
        vRecord.put(RecordsEntry.COLUMN_NAME_DISTANCE, session.getDistance());
        return vRecord;
    }

    private ContentValues getSessionValues(Session session) {
        ContentValues vSession = new ContentValues();
        vSession.put(SessionEntry.COLUMN_NAME_TITLE, session.getTitle());
        vSession.put(SessionEntry.COLUMN_NAME_DESCRIPTION, session.getDescription());
        vSession.put(SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE, session.getCurrentElevation());
        vSession.put(SessionEntry.COLUMN_NAME_MAX_HEIGHT, session.getMaxHeight());
        vSession.put(SessionEntry.COLUMN_NAME_MIN_HEIGHT, session.getMinHeight());
        vSession.put(SessionEntry.COLUMN_NAME_CURRENT_ADDRESS, session.getAddress());
        vSession.put(SessionEntry.COLUMN_NAME_CURRENT_DISTANCE, session.getDistance());
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
                SessionEntry.COLUMN_NAME_ENTRY_ID,
                SessionEntry.COLUMN_NAME_TITLE,
                SessionEntry.COLUMN_NAME_DESCRIPTION,
                SessionEntry.COLUMN_NAME_CURRENT_ALTITUDE,
                SessionEntry.COLUMN_NAME_MAX_HEIGHT,
                SessionEntry.COLUMN_NAME_MIN_HEIGHT,
                SessionEntry.COLUMN_NAME_CURRENT_ADDRESS,
                SessionEntry.COLUMN_NAME_CURRENT_DISTANCE
        };

        Cursor c = db.query(
                SessionEntry.TABLE_NAME, projection, null, null, null, null, null
        );

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = getCursorStr(c, SessionEntry.COLUMN_NAME_ENTRY_ID);
                String itemTitle = getCursorStr(c, SessionEntry.COLUMN_NAME_TITLE);
                String itemDescription = getCursorStr(c, SessionEntry.COLUMN_NAME_DESCRIPTION);
                itemTitle = adjustStrIfEmpty(itemTitle);
                itemDescription = adjustStrIfEmpty(itemDescription);
                Session session = new Session(itemTitle, itemDescription, itemId);
                sessions.add(session);
            }
        }
        closeCursor(c);

        db.close();

        callback.onSessionLoaded(sessions);
    }

    private String adjustStrIfEmpty(String str) {
        if (isStringEmpty(str)) {
            return "Have to be set...";
        } else {
            return str;
        }
    }

    private boolean isStringEmpty(String str) {
        return str == null || str.equals("");
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


    //TODO-> merge similar functions into one form update changed details section
    @Override
    public void updateDetailsChanges(@NonNull DetailsSessionCallback callback, Map<String, String> changes) {
        updateDetails(changes);
        callback.onChangesSaved();
    }

    private void updateDetails(Map<String, String> changes) {
        SQLiteDatabase db = mSessionDbHelper.getWritableDatabase();

        updateChangedDetails(db, changes);

        db.close();
    }

    private void updateChangedDetails(SQLiteDatabase db, Map<String, String> changes) {
        ContentValues valuesChanges = getChangedDetailsValues(changes);
        String rowSelection = SessionEntry.COLUMN_NAME_ENTRY_ID + "=" + "\"" + changes.get("id") + "\"";
        updateRowsDb(db, SessionEntry.TABLE_NAME, valuesChanges, rowSelection);
    }

    private ContentValues getChangedDetailsValues(Map<String, String> changes) {
        ContentValues vChanges = new ContentValues();
        vChanges.put(SessionEntry.COLUMN_NAME_TITLE, changes.get("title"));
        vChanges.put(SessionEntry.COLUMN_NAME_DESCRIPTION, changes.get("description"));
        return vChanges;
    }

    @Override
    public void refreshSessions() {
        // Not required because the {@link SessionRepository} handles the logic of refreshing the
        // tasks from all the available data sources. This instance is used as a member of
        // {@link SessionRepository}
    }

    @Override
    public void getMapData(@NonNull String sessionId, @NonNull LoadMapDataCallback callback) {
        List<LatLng> positions = new ArrayList<>();
        if (!sessionId.equals(Constants.DEFAULT_TEXT)) {
            positions = populateMapData(sessionId);
        }
        callback.onMapDataLoaded(positions);
    }

    private List<LatLng> populateMapData(String sessionId) {
        SQLiteDatabase db = mSessionDbHelper.getReadableDatabase();
        List<LatLng> positions  = populateMapDataFromRecords(db, sessionId);
        db.close();
        return positions;
    }

    private List<LatLng> populateMapDataFromRecords(SQLiteDatabase db, String sessionId) {
        String[] projectionRecords = mSessionDbHelper.getProjectionRecordsLatLng();
        String tableName = mSessionDbHelper.setProperName(sessionId);
        Cursor cLatLng = getDetailsCursor(db, tableName, projectionRecords, null);
        return populateMapFromRecords(cLatLng);
    }

    private List<LatLng> populateMapFromRecords(Cursor c) {
        List<LatLng> positions = new ArrayList<>();
        if (isCursorNotEmpty(c)) {
            while (c.moveToNext()) {
                Double lat = Double.valueOf(getCursorStr(c, RecordsEntry.COLUMN_NAME_LATITUDE));
                Double lng = Double.valueOf(getCursorStr(c, RecordsEntry.COLUMN_NAME_LONGITUDE));
                LatLng position = new LatLng(lat, lng);
                positions.add(position);
            }
        }
        closeCursor(c);

        return positions;
    }

    @Override
    public void getDetails(@NonNull String sessionId, DetailsSessionCallback callback, Context context) {
        Bundle args = populateDetails(sessionId);
        saveCurrentIdDrawerMapGeneration(args, context);
        callback.onDetailsLoaded(args);
    }

    private Bundle populateDetails(String sessionId) {
        Bundle args = new Bundle();
        SQLiteDatabase db = mSessionDbHelper.getReadableDatabase();
        args = populateDetailsFromSession(db, args, sessionId);
        args = populateDetailsFromRecords(db, args, sessionId);
        db.close();

        return args;
    }

    private Bundle populateDetailsFromSession(SQLiteDatabase db, Bundle args, String id) {
        String[] projectionSession = mSessionDbHelper.getProjectionsSessions();
        String cursorSelection = SessionEntry.COLUMN_NAME_ENTRY_ID
                + "=" + mSessionDbHelper.setProperName(id);
        String name = SessionEntry.TABLE_NAME;
        Cursor cSession = getDetailsCursor(db, name, projectionSession, cursorSelection);
        args = populateFromSession(cSession, args);
        closeCursor(cSession);

        return args;
    }

    private Bundle populateDetailsFromRecords(SQLiteDatabase db, Bundle args, String id) {
        String[] projectionRecords = mSessionDbHelper.getProjectionsRecordsDate();
        String tableName = mSessionDbHelper.setProperName(id);
        Cursor cRecords = getDetailsCursor(db, tableName, projectionRecords, null);
        args = populateFromRecords(cRecords, args);
        closeCursor(cRecords);

        return args;
    }

    private Cursor getDetailsCursor(SQLiteDatabase db, String tableName, String[] projection, String selection) {
        return db.query(tableName, projection, selection,
                null, null, null, null);
    }

    private Bundle populateFromSession(Cursor c, Bundle args) {
        c.moveToNext();
        String itemId = getCursorStr(c, SessionEntry.COLUMN_NAME_ENTRY_ID);
        String itemTitle = getCursorStr(c, SessionEntry.COLUMN_NAME_TITLE);
        String itemDescription = getCursorStr(c, SessionEntry.COLUMN_NAME_DESCRIPTION);
        String itemMaxHeight = getCursorStr(c, SessionEntry.COLUMN_NAME_MAX_HEIGHT);
        String itemMinHeight = getCursorStr(c, SessionEntry.COLUMN_NAME_MIN_HEIGHT);
        String itemAddress = getCursorStr(c, SessionEntry.COLUMN_NAME_CURRENT_ADDRESS);
        String itemDistance = getCursorStr(c, SessionEntry.COLUMN_NAME_CURRENT_DISTANCE);

        args.putString("title", itemTitle);
        args.putString("description", itemDescription);
        args.putString("id", itemId);
        args.putString("distance", FormatAndValueConverter.setDistanceStr(Double.valueOf(itemDistance)));
        args.putString("maxHeight", itemMaxHeight);
        args.putString("minHeight", itemMinHeight);
        args.putString("address", itemAddress);

        return args;
    }

    private Bundle populateFromRecords(Cursor c, Bundle args) {
        getNumOfPoints(c, args);
        getRecordingTime(c, args);
        return args;
    }

    private Bundle getNumOfPoints(Cursor c, Bundle args) {
        int numb = c.getCount();
        args.putString("numOfPoints", String.valueOf(numb));

        return args;
    }

    private Bundle getRecordingTime(Cursor c, Bundle args) {
        if (isCursorNotEmpty(c)) {
            c.moveToFirst();
            String timeStartStr = getCursorStr(c, RecordsEntry.COLUMN_NAME_DATE);
            long timeStart = convertTimeToNumeric(timeStartStr);
            timeStartStr = FormatAndValueConverter.setDateString(timeStart);
            args.putString("timeStart", timeStartStr);

            c.moveToLast();
            String timeEndStr = getCursorStr(c, RecordsEntry.COLUMN_NAME_DATE);
            long timeEnd = convertTimeToNumeric(timeEndStr);
            timeEndStr = FormatAndValueConverter.setDateString(timeEnd);
            args.putString("timeEnd", timeEndStr);
        } else {
            args.putString("timeStart", "something is wrong");
            args.putString("timeEnd", "something is wrong");
        }

        return args;
    }

    private boolean isCursorNotEmpty(Cursor c) {
        return c!= null && c.getCount()>0;
    }

    private String getCursorStr(Cursor c, String entry) {
        return c.getString(c.getColumnIndexOrThrow(entry));
    }

    private long convertTimeToNumeric(String time) {
        return Long.valueOf(time);
    }

    private void closeCursor(Cursor c) {
        if (c != null) {
            c.close();
        }
    }

    private void saveCurrentIdDrawerMapGeneration(Bundle args, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String id = args.getString("id");
        editor.putString("sessionId", id);
        editor.apply();
    }
}
