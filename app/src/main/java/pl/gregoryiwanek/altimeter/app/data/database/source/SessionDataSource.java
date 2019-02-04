package pl.gregoryiwanek.altimeter.app.data.database.source;

import android.content.*;
import android.os.*;

import androidx.annotation.*;

import com.google.android.gms.maps.model.*;

import java.util.*;

import pl.gregoryiwanek.altimeter.app.data.sessions.*;

//import pl.gregoryiwanek.altimeter.app.data.Session;

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

		void onExportDataLoaded(List<String[]> args);
	}

	void createRecordsTable(@NonNull Session session);

	void createNewSession(@NonNull Session session, SaveSessionCallback callback);

	void updateSessionData(@NonNull Session session);

	void getSessions(@NonNull LoadSessionsCallback callback);

	void clearSessionData(@NonNull String sessionId);

	void refreshSessions();

	void getMapData(@NonNull String sessionId, @NonNull LoadMapDataCallback callback);

	void getDetails(@NonNull String sessionId, DetailsSessionCallback callback, Context context);

	void deleteSessions(ArrayList<String> sessionsId, boolean isDeleteAll, @Nullable DeleteSessionCallback callback);

	void setSessionChecked(String sessionId, boolean isCompleted);

	void updateDetailsChanges(@NonNull DetailsSessionCallback callback, Map<String, String> changes);

	abstract class AsyncDatabaseTask {
		public abstract void onPreExecuteTriggered();

		public abstract void onPostExecuteTriggered();
	}
}

