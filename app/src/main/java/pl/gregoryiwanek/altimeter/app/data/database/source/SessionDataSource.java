package pl.gregoryiwanek.altimeter.app.data.database.source;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.gregoryiwanek.altimeter.app.data.Session;

/**
 * Entry point for accessing data.
 */
public interface SessionDataSource {

    interface LoadSessionsCallback {
        void onSessionLoaded(List<Session> sessions);

        void onDataNotAvailable();
    }

    interface LoadMapDataCallback {

        void onMapDataLoaded(List<LatLng> positions);
    }

    interface SaveSessionCallback {

        void onNewSessionSaved(String id);
    }

    interface DeleteSessionCallback {

        void onSessionsDeleted();
    }

    interface DetailsSessionCallback {

        void onDetailsLoaded(Bundle bundle);

        void onChangesSaved();
    }

    void createRecordsTable(@NonNull Session session);

    void createNewSession(@NonNull Session session, @NonNull SaveSessionCallback callback);

    void updateSessionData(@NonNull Session session);

    void getSessions(@NonNull LoadSessionsCallback callback);

    void clearSessionData(@NonNull String sessionId);

    void refreshSessions();

    void getMapData(@NonNull String sessionId, @NonNull LoadMapDataCallback callback);

    void getDetails(@NonNull String sessionId, DetailsSessionCallback callback, Context context);

    void deleteSessions(ArrayList<String> sessionsId, boolean isDeleteAll, @Nullable DeleteSessionCallback callback);

    void setSessionChecked(String sessionId, boolean isCompleted);

    void updateDetailsChanges(@NonNull DetailsSessionCallback callback, Map<String, String> changes);
}

