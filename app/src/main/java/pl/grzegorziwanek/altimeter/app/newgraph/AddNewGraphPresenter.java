package pl.grzegorziwanek.altimeter.app.newgraph;

import android.support.annotation.NonNull;

import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.model.location.LocationCollector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 31.01.2017.
 */

public class AddNewGraphPresenter implements AddNewGraphContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final AddNewGraphContract.View mAddNewGraphView;
    private final LocationCollector mLocationCollector;
    private String mSessionId = "da";

    //TODO-> add ID somewhere
    public AddNewGraphPresenter(@NonNull SessionRepository sessionSource,
                                @NonNull AddNewGraphContract.View addNewGraphView,
                                @NonNull LocationCollector locationCollector) {
        mSessionRepository = checkNotNull(sessionSource);
        mLocationCollector = checkNotNull(locationCollector);
        mAddNewGraphView = checkNotNull(addNewGraphView);
        mAddNewGraphView.setPresenter(this);
    }

    @Override
    public void start() {
        initiateSession();
    }

    private void initiateSession() {
        Session session = new Session("", "");
        mSessionRepository.saveSession(session, new SessionDataSource.SaveSessionCallback() {
            @Override
            public void onNewSessionSaved(String id) {
                mSessionId = id;
                System.out.println("SESSIONS ID IS: " + mSessionId);
            }
        });
    }

    @Override
    public void startLocationRecording() {
        int tag = R.drawable.ic_pause_white_18dp;
        updateButton(tag);
        loadGraphDrawing();
    }

    @Override
    public void stopLocationRecording() {
        int tag = R.drawable.ic_play_arrow_white_18dp;
        updateButton(tag);
        //TODO-> stop looking for location listening
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

    public void loadGraphDrawing() {

    }
}
