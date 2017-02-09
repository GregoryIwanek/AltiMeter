package pl.grzegorziwanek.altimeter.app.model.database.source;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.model.Session;

/**
 * Entry point for accessing data.
 */
public interface SessionDataSource {

    interface LoadSessionsCallback {
        //TODO-> change graph view to correct type of data
        void onSessionLoaded(List<Session> sessions);

        void onDataNotAvailable();
    }

    interface SaveSessionCallback {

        void onNewSessionSaved(String id);
    }

    void createRecordsTable(@NonNull Session session);

    void createNewSession(@NonNull Session session, @NonNull SaveSessionCallback callback);

    void updateSessionData(@NonNull Session session);

    void getSessions(@NonNull LoadSessionsCallback callback);

    void clearSessionData(@NonNull String sessionId);

    void refreshSessions();

    void deleteSessions(ArrayList<String> sessionsId, boolean isDeleteAll);
}

