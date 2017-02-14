package pl.grzegorziwanek.altimeter.app.model.database.source;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.grzegorziwanek.altimeter.app.model.Session;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 27.01.2017.
 */

public class SessionRepository implements SessionDataSource {

    private static SessionRepository INSTANCE = null;
    private final SessionDataSource mSessionLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Session> mCachedSessions;
    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean mCacheIsDirty = false;

    //Private to prevent direct instantiation.
    private SessionRepository(@NonNull SessionDataSource sessionLocalDataSource) {
        mSessionLocalDataSource = checkNotNull(sessionLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     * @param sessionLocalDataSource  the device storage data source
     * @return the {@link SessionRepository} instance
     */
    public static SessionRepository getInstance(SessionDataSource sessionLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SessionRepository(sessionLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(SessionDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void createRecordsTable(@NonNull Session session) {

    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadSessionsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getSessions(@NonNull final LoadSessionsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty (corrupted)
        //TODO -> move clear from here
//        if (mCachedSessions != null && !mCacheIsDirty) {
//            callback.onSessionLoaded(new ArrayList<>(mCachedSessions.values()));
//            return;
//        }

        //TODO-> remove mCacheIsDirty from here
        mCacheIsDirty = false;
        if (mCacheIsDirty) {
            // If the cache is dirty (corrupted) we need to fetch new data from the network.
            getSessionsFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network
            mSessionLocalDataSource.getSessions(new LoadSessionsCallback() {
                @Override
                public void onSessionLoaded(List<Session> sessions) {
                    refreshCache(sessions);
                    callback.onSessionLoaded(new ArrayList<>(mCachedSessions.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getSessionsFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void createNewSession(@NonNull final Session session, @NonNull final SaveSessionCallback callback) {
        //TODO-> consider adding remote data source
        //TODO-> change form of architecture here
        checkNotNull(session);
        mSessionLocalDataSource.createNewSession(session, new SaveSessionCallback() {
            @Override
            public void onNewSessionSaved(String id) {
                callback.onNewSessionSaved(id);
                mSessionLocalDataSource.createRecordsTable(session);
            }
        });

        // Do in memory cache update to keep the app UI to date
        // Add to cache
        initiateCache();
        mCachedSessions.put(session.getId(), session);
    }

    @Override
    public void updateSessionData(@NonNull Session session) {
        checkNotNull(session);
        mSessionLocalDataSource.updateSessionData(session);
    }

    @Override
    public void refreshSessions() {
        mCacheIsDirty = true;
    }

    @Override
    public void getMapData(@NonNull String sessionId, @NonNull LoadMapDataCallback callback) {
        mSessionLocalDataSource.getMapData(sessionId, callback);
    }

    @Override
    public void getDetails(@NonNull String sessionId, @NonNull DetailsSessionCallback callback) {
        mSessionLocalDataSource.getDetails(sessionId, callback);
    }

    @Override
    public void clearSessionData(@NonNull String sessionId) {
        mSessionLocalDataSource.clearSessionData(sessionId);
    }

    @Override
    public void deleteSessions(ArrayList<String> sessionsId, boolean isDeleteAll,
                               @NonNull DeleteSessionCallback callback) {
        mSessionLocalDataSource.deleteSessions(sessionsId, isDeleteAll, callback);
        deleteFromCache(sessionsId, isDeleteAll);
        callback.onSessionsDeleted();
    }

    @Override
    public void setSessionChecked(String sessionId, boolean isCompleted) {
        setSessionCompleted(sessionId, isCompleted);
    }

    private void deleteFromCache(ArrayList<String> sessionsId, boolean isDeleteAll) {
        initiateCache();
        if (isDeleteAll) {
            deleteAllCache();
        } else {
            deleteFewCache(sessionsId);
        }
    }

    private void initiateCache() {
        if (mCachedSessions == null) {
            mCachedSessions = new LinkedHashMap<>();
        }
    }

    private void deleteAllCache() {
        mCachedSessions.clear();
    }

    private void deleteFewCache(ArrayList<String> sessionsId) {
        Iterator<Map.Entry<String, Session>> iterator = mCachedSessions.entrySet().iterator();
        for (String sessionId : sessionsId) {
            while (iterator.hasNext()) {
                Map.Entry<String, Session> entry = iterator.next();
                if(entry.getValue().getId().equals(sessionId)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    //TODO-> refactor this to change value of session in database
    //TODO-> add column in Record table (isCompleted)
    private void setSessionCompleted(String sessionId, boolean isCompleted) {
        for (Map.Entry<String, Session> entry : mCachedSessions.entrySet()) {
            if (entry.getValue().getId().equals(sessionId)) {
                mCachedSessions.get(sessionId).setCompleted(isCompleted);
                break;
            }
        }
    }

    //todo -> finish that one
    private void refreshCache(List<Session> sessions) {
        if (mCachedSessions == null) {
            mCachedSessions = new LinkedHashMap<>();
        }

        mCachedSessions.clear();
        for (Session session : sessions) {
            mCachedSessions.put(session.getId(), session);
        }
        mCacheIsDirty = false;
    }

    //todo -> finish that one
    private void refreshLocalDataSource(List<Session> sessions) {
        //mSessionLocalDataSource.deleteSessions();
        for (Session session : sessions) {
            mSessionLocalDataSource.createNewSession(session, null);
        }
    }

    private void getSessionsFromRemoteDataSource(@NonNull final LoadSessionsCallback callback) {
    }
}
