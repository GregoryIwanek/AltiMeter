package pl.grzegorziwanek.altimeter.app.model.database.source;

import android.support.annotation.NonNull;

import java.util.ArrayList;
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
    //todo-> change from null to real value
    private final SessionDataSource mSessionRemoteDataSource = null;

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
        if (mCachedSessions != null && !mCacheIsDirty) {
            callback.onSessionLoaded(new ArrayList<>(mCachedSessions.values()));
            return;
        }

        //TODO-> remove mCacheIsDirty from here
        mCacheIsDirty = false;
        System.out.println("is cache dirty?" + mCacheIsDirty);
        if (mCacheIsDirty) {
            // If the cache is dirty (corrupted) we need to fetch new data from the network.
            getSessionsFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network
            mSessionLocalDataSource.getSessions(new LoadSessionsCallback() {
                @Override
                public void onSessionLoaded(List<Session> sessions) {
                    System.out.println("onSessionLoaded callback SessionRepository, sessions size: " + sessions.size());
                    refreshCache(sessions);
                    System.out.println("onSessionLoaded callback SessionRepository, cache size: " + mCachedSessions.size());
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
        //save to database
        System.out.println("SESSION REPO SAVE NEW SESSION: " + session.getId());
        checkNotNull(session);
        mSessionLocalDataSource.createNewSession(session, new SaveSessionCallback() {
            @Override
            public void onNewSessionSaved(String id) {
                callback.onNewSessionSaved(id);
                System.out.println("SESSION REPO ON NEW SESSION SAVED: " + session.getId());
                mSessionLocalDataSource.createRecordsTable(session);
            }
        });

        // Do in memory cache update to keep the app UI to date
        // Add to cache
        if (mCachedSessions == null) {
            mCachedSessions = new LinkedHashMap<>();
        }
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
    public void clearSessionData(@NonNull String sessionId) {
        //TODO -> delete data of one session from database
    }

    @Override
    public void deleteAllSessions() {
        mSessionRemoteDataSource.deleteAllSessions();
        mSessionLocalDataSource.deleteAllSessions();

        if (mCachedSessions == null) {
            mCachedSessions = new LinkedHashMap<>();
        }

        mCachedSessions.clear();
    }

    @Override
    public void deleteSession(@NonNull String sessionId) {
        //TODO -> delete single session from here
    }

    //todo -> finish that one
    private void refreshCache(List<Session> sessions) {
        if (mCachedSessions == null) {
            mCachedSessions = new LinkedHashMap<>();
        }
        mCachedSessions.clear();
        for (Session session : sessions) {
            mCachedSessions.put(session.getId(), session);
            System.out.println("ADD NEW SESSION IN TO CACHE: " + session.getId());
            System.out.println("Session id: " + session.getId());
        }
        mCacheIsDirty = false;
    }

    //todo -> finish that one
    private void refreshLocalDataSource(List<Session> sessions) {
        mSessionLocalDataSource.deleteAllSessions();
        for (Session session : sessions) {
            mSessionLocalDataSource.createNewSession(session, null);
        }
    }

    private void getSessionsFromRemoteDataSource(@NonNull final LoadSessionsCallback callback) {
//        mSessionRemoteDataSource.getSessions(new LoadSessionsCallback() {
//            @Override
//            public void onSessionLoaded(List<Session> sessions) {
//                refreshCache(sessions);
//                refreshLocalDataSource(sessions);
//                callback.onSessionLoaded(new ArrayList<>(mCachedSessions.values()));
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                callback.onDataNotAvailable();
//            }
//        });
    }
}
