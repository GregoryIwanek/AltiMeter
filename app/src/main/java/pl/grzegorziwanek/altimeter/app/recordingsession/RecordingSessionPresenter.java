package pl.grzegorziwanek.altimeter.app.recordingsession;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Window;

import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;
import pl.grzegorziwanek.altimeter.app.data.location.LocationUpdateManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;
import pl.grzegorziwanek.altimeter.app.utils.ScreenShotCatcher;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.grzegorziwanek.altimeter.app.recordingsession.RecordingSessionContract.*;

/**
 * Created by Grzegorz Iwanek on 31.01.2017.
 */
class RecordingSessionPresenter implements Presenter {

    private final SessionRepository mSessionRepository;
    private final View mRecordingSessionView;
    private final LocationUpdateManager mLocationUpdateManager;
    private LocationResponse.FullInfoCallback callbackFullInfo;
    private Session mSession;

    public RecordingSessionPresenter(@NonNull SessionRepository sessionSource,
                                     @NonNull LocationUpdateManager locationUpdateManager,
                                     @NonNull View addNewGraphView) {
        mSessionRepository = checkNotNull(sessionSource);
        mLocationUpdateManager = checkNotNull(locationUpdateManager);
        mRecordingSessionView = checkNotNull(addNewGraphView);
        mRecordingSessionView.setPresenter(this);
    }

    @Override
    public void start() {
        setSession();
        initiateSession();
    }

    private void setSession() {
        mSession = mLocationUpdateManager.getSession();
    }

    private void initiateSession() {
        mSessionRepository.createNewSession(mSession, new SessionDataSource.SaveSessionCallback() {
            @Override
            public void onNewSessionSaved(String id) {
            }
        });
    }

    @Override
    public void openSessionMap() {
        String id = mSession.getId();
        mRecordingSessionView.showSessionMap(id);
    }

    @Override
    public void callStartLocationRecording() {
        mRecordingSessionView.checkDataSourceOpen();
    }

    @Override
    public void startLocationRecording() {
        int tag = R.drawable.ic_pause_black_24dp;
        updateButton(tag);

        callbackFullInfo = new LocationResponse.FullInfoCallback() {
            @Override
            public void onFullInfoAcquired(Session session) {
                if (session.getCurrentLocation() != null) {
                    updateView(session);
                } else {
                    updateAfterCleared();
                }
            }

            //TODO-> merge this into one system of location management
            @Override
            public void onBarometerInfoAcquired(String barometerAlt) {
                mRecordingSessionView.setBarometerTextView(barometerAlt);
            }

            @Override
            public void onGpsInfoAcquired(String gpsAlt) {
                mRecordingSessionView.setGpsTextView(gpsAlt);
            }

            @Override
            public void onNetworkInfoAcquired(String networkAlt) {
                mRecordingSessionView.setNetworkTextView(networkAlt);
            }
        };

        mRecordingSessionView.showRecordingData();
        mLocationUpdateManager.startListenForLocations(callbackFullInfo);
    }

    private void updateView(Session session) {
        mRecordingSessionView.setAddressTextView(session.getAddress());
        mRecordingSessionView.setElevationTextView(session.getCurrentElevation().toString());
        mRecordingSessionView.setDistanceTextView(session.getDistanceStr());
        mRecordingSessionView.setMinHeightTextView(session.getMinHeightStr());
        mRecordingSessionView.setMaxHeightTextView(session.getMaxHeightStr());
        mRecordingSessionView.setLatTextView(session.getLatitudeStr());
        mRecordingSessionView.setLongTextView(session.getLongitudeStr());
        mRecordingSessionView.drawGraph(session.getGraphList());
        mSessionRepository.updateSessionData(session);
    }

    private void updateAfterCleared() {
        mRecordingSessionView.setAddressTextView("...");
        mRecordingSessionView.setElevationTextView("...");
        mRecordingSessionView.setDistanceTextView("...");
        mRecordingSessionView.setMinHeightTextView("...");
        mRecordingSessionView.setMaxHeightTextView("...");
        mRecordingSessionView.setLatTextView("...");
        mRecordingSessionView.setLongTextView("...");
    }

    @Override
    public void pauseLocationRecording() {
        int tag = R.drawable.ic_play_arrow_black_24dp;
        updateButton(tag);
        mRecordingSessionView.showRecordingPaused();
        mLocationUpdateManager.stopListenForLocations(false);
    }

    @Override
    public void enableGps() {
        updateManager(GpsManager.class, true);
        int tag = R.drawable.ic_gps_open_24dp;
        updateButton(tag);
    }

    @Override
    public void disableGps() {
        updateManager(GpsManager.class, false);
        int tag = R.drawable.ic_gps_lock_24dp;
        updateButton(tag);

    }

    @Override
    public void enableNetwork() {
        updateManager(NetworkManager.class, true);
        int tag = R.drawable.ic_network_open_24dp;
        updateButton(tag);
    }

    @Override
    public void disableNetwork() {
        updateManager(NetworkManager.class, false);
        int tag = R.drawable.ic_network_lock_24dp;
        updateButton(tag);
    }

    @Override
    public void enableBarometer() {
        updateManager(BarometerManager.class, true);
        int tag = R.drawable.ic_barometer_open_24dp;
        updateButton(tag);
    }

    @Override
    public void disableBarometer() {
        updateManager(BarometerManager.class, false);
        int tag = R.drawable.ic_barometer_lock_24dp;
        updateButton(tag);
    }

    private void updateManager(Class<?> manager, boolean isEnabled) {
        mLocationUpdateManager.setManagerState(manager, isEnabled);
    }

    @Override
    public void lockSession() {
        int tag = R.drawable.ic_play_arrow_black_24dp;
        updateButton(tag);

        mRecordingSessionView.showSessionLocked();
        mLocationUpdateManager.stopListenForLocations(true);
    }

    @Override
    public void activityDestroyedUnsubscribeRx() {
        mLocationUpdateManager.onActivityDestroyed();
    }

    @Override
    public void shareScreenShot(Window window, ContentResolver cr, String[] textViewContent) {
        Intent intent = ScreenShotCatcher.captureAndShare(window, cr, textViewContent);
        mRecordingSessionView.showShareMenu(intent);
    }

    private void updateButton(int drawableId) {
        mRecordingSessionView.setButtonTagAndPicture(drawableId);
    }

    @Override
    public void resetSessionData() {
        pauseLocationRecording();
        disableServices();
        mSessionRepository.clearSessionData(mSession.getId());
        mLocationUpdateManager.resetAllData();
        mRecordingSessionView.resetGraph();
    }

    private void disableServices() {
        disableGps();
        disableNetwork();
        disableBarometer();
    }
}
