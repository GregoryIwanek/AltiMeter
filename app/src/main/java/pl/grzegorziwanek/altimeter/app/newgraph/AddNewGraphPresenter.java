package pl.grzegorziwanek.altimeter.app.newgraph;

import android.support.annotation.NonNull;

import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;
import pl.grzegorziwanek.altimeter.app.data.location.LocationUpdateManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 31.01.2017.
 */

public class AddNewGraphPresenter implements AddNewGraphContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final AddNewGraphContract.View mAddNewGraphView;
    private final LocationUpdateManager mLocationUpdateManager;
    private LocationResponse.FullInfoCallback callbackFullInfo;
    private static Session mSession;

    public AddNewGraphPresenter(@NonNull SessionRepository sessionSource,
                                @NonNull LocationUpdateManager locationUpdateManager,
                                @NonNull AddNewGraphContract.View addNewGraphView) {
        mSessionRepository = checkNotNull(sessionSource);
        mLocationUpdateManager = checkNotNull(locationUpdateManager);
        mAddNewGraphView = checkNotNull(addNewGraphView);
        mAddNewGraphView.setPresenter(this);
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
        mAddNewGraphView.showSessionMap(id);
    }

    @Override
    public void callStartLocationRecording() {
        mAddNewGraphView.checkDataSourceOpen();
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
                mAddNewGraphView.setBarometerTextView(barometerAlt);
            }

            @Override
            public void onGpsInfoAcquired(String gpsAlt) {
                mAddNewGraphView.setGpsTextView(gpsAlt);
            }

            @Override
            public void onNetworkInfoAcquired(String networkAlt) {
                mAddNewGraphView.setNetworkTextView(networkAlt);
            }
        };

        mAddNewGraphView.showRecordingData();
        mLocationUpdateManager.startListenForLocations(callbackFullInfo);
    }

    private void updateView(Session session) {
        mAddNewGraphView.setAddressTextView(session.getAddress());
        mAddNewGraphView.setElevationTextView(session.getCurrentElevation().toString());
        mAddNewGraphView.setDistanceTextView(session.getDistanceStr());
        mAddNewGraphView.setMinHeightTextView(session.getMinHeightStr());
        mAddNewGraphView.setMaxHeightTextView(session.getMaxHeightStr());
        mAddNewGraphView.setLatTextView(session.getLatitudeStr());
        mAddNewGraphView.setLongTextView(session.getLongitudeStr());
        mAddNewGraphView.drawGraph(session.getLocationList());

        mSessionRepository.updateSessionData(session);
    }

    private void updateAfterCleared() {
        mAddNewGraphView.setAddressTextView("...");
        mAddNewGraphView.setElevationTextView("...");
        mAddNewGraphView.setDistanceTextView("...");
        mAddNewGraphView.setMinHeightTextView("...");
        mAddNewGraphView.setMaxHeightTextView("...");
        mAddNewGraphView.setLatTextView("...");
        mAddNewGraphView.setLongTextView("...");
    }

    @Override
    public void pauseLocationRecording() {
        int tag = R.drawable.ic_play_arrow_black_24dp;
        updateButton(tag);

        mAddNewGraphView.showRecordingPaused();
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

        mAddNewGraphView.showSessionLocked();
        mLocationUpdateManager.stopListenForLocations(true);
    }

    private void updateButton(int drawableId) {
        mAddNewGraphView.setButtonTagAndPicture(drawableId);
    }

    @Override
    public void resetSessionData() {
        mSessionRepository.clearSessionData(mSession.getId());
        mLocationUpdateManager.clearSessionData();
        mAddNewGraphView.resetGraph();
    }
}
