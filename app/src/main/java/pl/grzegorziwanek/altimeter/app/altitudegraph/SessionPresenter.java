package pl.grzegorziwanek.altimeter.app.altitudegraph;

import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link SessionFragment}), retrieves the data and updates the
 * UI as required.
 */
public class SessionPresenter implements SessionContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final SessionContract.View mSessionView;
    private boolean mFirstLoad = true;

    public SessionPresenter(@NonNull SessionRepository sessionRepository, @NonNull SessionContract.View sessionView) {
        mSessionRepository = checkNotNull(sessionRepository, "sessionRepository cannot be null");
        mSessionView = checkNotNull(sessionView, "sessionView cannot be null");
        System.out.println("CALLING if msessionView is active AFTER INITIATION: "+mSessionView.isActive());
        mSessionView.setPresenter(this);
    }

    @Override
    public void start() {
//        for (int i=0; i<10; i++) {
//            createTask("SESSION", "SESSION");
//        }
        loadSessions(false);
    }

    private void createTask(String title, String description) {
        Session newSession = new Session(title, description);
        mSessionRepository.saveSession(newSession, null);
        System.out.println("SESSION SAVED FROM PRESENTER");
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadSessions(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadSessions(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link SessionDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadSessions(boolean forceUpdate, final boolean showLoadingUI) {
        System.out.println("CALLING LOADSESSIONS");
        if (showLoadingUI) {
            mSessionView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mSessionRepository.refreshSessions();
        }

        //TODO-> setting espresso here -> to analyse
        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        //EspressoIdlingResource.increment(); // App is busy until further notice

        //getting sessions from database repository
        mSessionRepository.getSessions(new SessionDataSource.LoadSessionsCallback() {
            @Override
            public void onSessionLoaded(List<Session> sessions) {
                System.out.println("CALLING OnSessionLoaded SPresenter" + sessions.size());
                List<Session> sessionsToShow = new ArrayList<Session>();
                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                //TODO-> esspresso, add
//               if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
//                    EspressoIdlingResource.decrement(); // Set app as idle.
//                }

                //TODO-> mCurrentFiltering
                // We filter the sessions based on the requestType
                for (Session session : sessions) {
                    //mCurrentFiltering
                    switch (1) {
                        case 1:
                            sessionsToShow.add(session);
                            break;
                        default:
                            sessionsToShow.add(session);
                            break;
                    }
                }
                System.out.println("CALLING if msessionView is active: "+mSessionView.isActive());
                // The view may not be able to handle UI updates anymore
                if (!mSessionView.isActive()) {
                    System.out.println("CALLING !mSessionView.isActive() SPresenter");
                    return;
                }
                if (showLoadingUI) {
                    mSessionView.setLoadingIndicator(false);
                }

                processSessions(sessionsToShow);
            }

            @Override
            public void onDataNotAvailable() {
                System.out.println("CALLING OnDataNotAvailable SPresenter");
                // The view may not be able to handle UI updates anymore
                if (!mSessionView.isActive()) {
                    return;
                }
                mSessionView.showLoadingSessionError();
            }
        });
    }

    private void processSessions(List<Session> sessions) {
        System.out.println("PROCESS SESSION WILL BE CALLED NOW size()+" + sessions.size());
        if (sessions.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            System.out.println("PROCESSING EMPTY SESSION SPresenter");
            processEmptySessions();
        } else {
            // Show the list of tasks
            System.out.println("PROCESSING FULL SESSION SPresenter");
            mSessionView.showSessions(sessions);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    //TODO-> mCurrentFiltering
    private void showFilterLabel() {
        //mCurrentFiltering
        switch (1) {
            case 1:
                //mSessionView.showActiveFilterLabel();
                break;
            case 2:
                //mSessionView.showCompletedFilterLabel();
                break;
            default:
                //mSessionView.showAllFilterLabel();
                break;
        }
    }

    //TODO-> mCurrentFiltering
    private void processEmptySessions() {
        //mCurrentFiltering
        switch (1) {
            case 1:
                //mSessionView.showNoActiveSessions();
                break;
            case 2:
                //mSessionView.showNoCompletedSessions();
                break;
            default:
                //mSessionView.showNoSessions();
                break;
        }
    }

    @Override
    public void addNewSession() {
        mSessionView.showAddSession();
    }

    @Override
    public void openGraphDetails(@NonNull GraphView requestedGraphs) {

    }

    @Override
    public void completeGraphs(@NonNull GraphView completedGraphs) {

    }

    @Override
    public void activeGraphs(@NonNull GraphView activeGraphs) {

    }

    @Override
    public void clearCompletedGraphs() {

    }

    @Override
    public void setFiltering(SessionFilterType requestType) {

    }

    @Override
    public SessionFilterType getFiltering() {
        return null;
    }
}
