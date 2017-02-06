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
    private CallbackResponse.FullLocationInfoCallback callbackFullInfo;
    private static Session mSession;
    private String mSessionId;

    //TODO-> add ID somewhere, or keep Session instance here (?) or in LocationCollector (?)
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
        mSessionRepository.saveNewSession(mSession, new SessionDataSource.SaveSessionCallback() {
            @Override
            public void onNewSessionSaved(String id) {
                mSessionId = id;
            }
        });
    }

    @Override
    public void startLocationRecording() {
        int tag = R.drawable.ic_pause_black_24dp;
        updateButton(tag);

        callbackFullInfo = new CallbackResponse.FullLocationInfoCallback() {
            @Override
            public void onFullLocationInfoAcquired(Session session) {
                mAddNewGraphView.setAddressTextView(session.getAddress());
                mAddNewGraphView.setElevationTextView(session.getCurrentElevation().toString());
                mAddNewGraphView.setDistanceTextView(session.getDistanceStr());
                mAddNewGraphView.setMinHeightTextView(session.getMinHeightStr());
                mAddNewGraphView.setMaxHeightTextView(session.getMaxHeightStr());
                mAddNewGraphView.setLatTextView(session.getLatitude());
                mAddNewGraphView.setLongTextView(session.getLongitude());
                mAddNewGraphView.drawGraph(session.getLocationList());

                mSessionRepository.updateSessionData(session);
            }
        };

        mLocationCollector.startListenForLocations(callbackFullInfo);
    }

    @Override
    public void stopLocationRecording() {
        int tag = R.drawable.ic_play_arrow_black_24dp;
        updateButton(tag);

        mLocationCollector.stopListenForLocations();
    }

    private void updateButton(int drawableId) {
        mAddNewGraphView.setButtonTag(drawableId);
        mAddNewGraphView.setButtonPicture(drawableId);
    }

    @Override
    public void resetData() {
        mAddNewGraphView.resetGraph();
        mSessionRepository.clearSessionData(mSessionId);
    }
}
