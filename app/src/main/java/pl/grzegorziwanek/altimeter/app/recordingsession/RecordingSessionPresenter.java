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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.grzegorziwanek.altimeter.app.recordingsession.RecordingSessionContract.*;

/**
 * Presenter class of RecordingSession section.
 */
class RecordingSessionPresenter implements Presenter {

    private final SessionRepository mSessionRepository;
    private final View mRecordingSessionView;
    private final LocationUpdateManager mLocationUpdateManager;
    private LocationResponse.FullInfoCallback callbackFullInfo;
    private Session mSession;

    RecordingSessionPresenter(@NonNull SessionRepository sessionSource,
                              @NonNull LocationUpdateManager locationUpdateManager,
                              @NonNull View addNewGraphView) {
        mSessionRepository = checkNotNull(sessionSource);
        mLocationUpdateManager = checkNotNull(locationUpdateManager);
        mRecordingSessionView = checkNotNull(addNewGraphView);
        mRecordingSessionView.setPresenter(this);
    }

    @Override
    public void start() {
        setSessionObject();
        initiateSessionObject();
        setCallbackFullInfo();
    }

    private void setCallbackFullInfo() {
        callbackFullInfo = new LocationResponse.FullInfoCallback() {
            @Override
            public void onFullInfoAcquired(Session session) {
                if (session.getCurrentLocation() != null) {
                    updateView(session);
                } else {
                    updateViewAfterClearedSession();
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
    }

    private void setSessionObject() {
        mSession = mLocationUpdateManager.getSession();
    }

    private void initiateSessionObject() {
        mSessionRepository.createNewSession(mSession, new SessionDataSource.SaveSessionCallback() {
            @Override
            public void onNewSessionSaved(String id) {
            }
        });
    }

    @Override
    public void openMapOfSession() {
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
        updateButtonState(tag);
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

    private void updateViewAfterClearedSession() {
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
        updateButtonState(tag);
        mRecordingSessionView.showRecordingPaused();
        mLocationUpdateManager.stopListenForLocations(false);
    }

    @Override
    public void enableGps() {
        updateManagerState(GpsManager.class, true);
        int tag = R.drawable.ic_gps_open_24dp;
        updateButtonState(tag);
    }

    @Override
    public void disableGps() {
        updateManagerState(GpsManager.class, false);
        int tag = R.drawable.ic_gps_lock_24dp;
        updateButtonState(tag);

    }

    @Override
    public void enableNetwork() {
        updateManagerState(NetworkManager.class, true);
        int tag = R.drawable.ic_network_open_24dp;
        updateButtonState(tag);
    }

    @Override
    public void disableNetwork() {
        updateManagerState(NetworkManager.class, false);
        int tag = R.drawable.ic_network_lock_24dp;
        updateButtonState(tag);
    }

    @Override
    public void enableBarometer() {
        updateManagerState(BarometerManager.class, true);
        int tag = R.drawable.ic_barometer_open_24dp;
        updateButtonState(tag);
    }

    @Override
    public void disableBarometer() {
        updateManagerState(BarometerManager.class, false);
        int tag = R.drawable.ic_barometer_lock_24dp;
        updateButtonState(tag);
    }

    private void updateManagerState(Class<?> manager, boolean isEnabled) {
        mLocationUpdateManager.setManagerState(manager, isEnabled);
    }

    @Override
    public void lockSession() {
        int tag = R.drawable.ic_play_arrow_black_24dp;
        updateButtonState(tag);

        mRecordingSessionView.showSessionLocked();
        mLocationUpdateManager.stopListenForLocations(true);
    }

    @Override
    public void onActivityDestroyedUnsubscribeRx() {
        mLocationUpdateManager.onActivityDestroyed();
    }

    @Override
    public void shareScreenShot(Window window, ContentResolver cr, String[] textViewContent) {
        ScreenShotCatcher catcher = new ScreenShotCatcher();
        Intent intent = catcher.captureAndShare(window, cr, textViewContent);
        mRecordingSessionView.showShareMenu(intent);
    }

    @Override
    public void checkIsSessionEmpty() {
        mLocationUpdateManager.isSessionEmptyObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            mRecordingSessionView.showMapEmpty();
                        } else {
                            mRecordingSessionView.askGenerateMap();
                        }
                    }
                });
    }

    private void updateButtonState(int drawableId) {
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
