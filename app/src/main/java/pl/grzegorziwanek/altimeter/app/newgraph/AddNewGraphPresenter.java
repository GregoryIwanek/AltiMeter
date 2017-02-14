package pl.grzegorziwanek.altimeter.app.newgraph;

import android.support.annotation.NonNull;

import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.model.location.CallbackResponse;
import pl.grzegorziwanek.altimeter.app.model.location.LocationCollector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 31.01.2017.
 */

public class AddNewGraphPresenter implements AddNewGraphContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final AddNewGraphContract.View mAddNewGraphView;
    private final LocationCollector mLocationCollector;
    private CallbackResponse.FullInfoCallback callbackFullInfo;
    private static Session mSession;

    public AddNewGraphPresenter(@NonNull SessionRepository sessionSource,
                                @NonNull LocationCollector locationCollector,
                                @NonNull AddNewGraphContract.View addNewGraphView) {
        mSessionRepository = checkNotNull(sessionSource);
        mLocationCollector = checkNotNull(locationCollector);
        mAddNewGraphView = checkNotNull(addNewGraphView);
        mAddNewGraphView.setPresenter(this);
    }

    @Override
    public void start() {
        setSession();
        initiateSession();
    }

    private void setSession() {
        mSession = mLocationCollector.getSession();
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
    public void startLocationRecording() {
        int tag = R.drawable.ic_pause_black_24dp;
        updateButton(tag);

        callbackFullInfo = new CallbackResponse.FullInfoCallback() {
            @Override
            public void onFullInfoAcquired(Session session) {
                if (session.getCurrentLocation() != null) {
                    updateView(session);
                } else {
                    updateAfterCleared();
                }
            }
        };

        mAddNewGraphView.showRecordingData();
        mLocationCollector.startListenForLocations(callbackFullInfo);
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
        mLocationCollector.stopListenForLocations(false);
    }

    @Override
    public void lockSession() {
        int tag = R.drawable.ic_play_arrow_black_24dp;
        updateButton(tag);

        mAddNewGraphView.showSessionLocked();
        mLocationCollector.stopListenForLocations(true);
    }

    private void updateButton(int drawableId) {
        mAddNewGraphView.setButtonTag(drawableId);
        mAddNewGraphView.setButtonPicture(drawableId);
    }

    @Override
    public void resetSessionData() {
        mSessionRepository.clearSessionData(mSession.getId());
        mLocationCollector.clearSessionData();
        mAddNewGraphView.resetGraph();
    }
}
