package pl.grzegorziwanek.altimeter.app.model.database.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    interface DeleteSessionCallback {

        void onSessionsDeleted();
    }

    void createRecordsTable(@NonNull Session session);

    void createNewSession(@NonNull Session session, @NonNull SaveSessionCallback callback);

    void updateSessionData(@NonNull Session session);

    void getSessions(@NonNull LoadSessionsCallback callback);

    void clearSessionData(@NonNull String sessionId);

    void refreshSessions();

    void deleteSessions(ArrayList<String> sessionsId, boolean isDeleteAll, @Nullable DeleteSessionCallback callback);

    void setSessionChecked(String sessionId, boolean isCompleted);
}

