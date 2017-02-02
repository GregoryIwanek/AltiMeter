package pl.grzegorziwanek.altimeter.app.model.database.source;

import android.support.annotation.NonNull;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.model.Session;

/**
 * Entry point for accessing graphs data.
 */
///**
// * Main entry point for accessing tasks data.
// * <p>
// * For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to other
// * methods to inform the user of network/database errors or successful operations.
// * For example, when a new task is created, it's synchronously stored in cache but usually every
// * operation on database or network should be executed in a different thread.
// */
public interface SessionDataSource {

    interface LoadSessionsCallback {
        //TODO-> change graph view to correct type of data
        void onSessionLoaded(List<Session> sessions);

        void onDataNotAvailable();
    }

    interface GetSessionCallback {
        //TODO-> change graph view to correct type of data
        void onSessionLoaded(Session session);

        void onDataNotAvailable();
    }

    interface SaveSessionCallback {

        void onNewSessionSaved(String id);
    }

    void createSession(@NonNull Session session);

    void saveSession(@NonNull Session session, @NonNull SaveSessionCallback callback);

    void getSessions(@NonNull LoadSessionsCallback callback);
//
//    void getSession(@NonNull String graphId, @NonNull GetSessionCallback callback);
//
//    void saveSession(@NonNull Session session);
//
//    void completeSession(@NonNull Session session);
//
//    void completeSession(@NonNull String sessionId);
//
//    void activateSession(@NonNull Session session);
//
//    void activateSession(@NonNull String sessionId);
//
//    void clearCompletedSessions();
//
    void clearSessionData(@NonNull String sessionId);

    void refreshSessions();

    void deleteAllSessions();

    void deleteSession(@NonNull String sessionId);
}

