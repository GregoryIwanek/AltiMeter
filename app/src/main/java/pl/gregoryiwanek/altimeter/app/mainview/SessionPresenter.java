package pl.gregoryiwanek.altimeter.app.mainview;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pl.gregoryiwanek.altimeter.app.data.Session;
import pl.gregoryiwanek.altimeter.app.data.database.source.SessionDataSource;
import pl.gregoryiwanek.altimeter.app.data.database.source.SessionRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link SessionFragment}), retrieves the data and updates the
 * UI as required.
 */
class SessionPresenter implements SessionContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final SessionContract.View mSessionView;
    private boolean mFirstLoad = true;
    private SessionDataSource.DeleteSessionCallback callbackDelete;

    SessionPresenter(@NonNull SessionRepository sessionRepository, @NonNull SessionContract.View sessionView) {
        mSessionRepository = checkNotNull(sessionRepository, "sessionRepository cannot be null");
        mSessionView = checkNotNull(sessionView, "sessionView cannot be null");
        mSessionView.setPresenter(this);
        setCallbacks();
    }

    @Override
    public void start() {
        loadSessions(false);
    }

    private void createTask(String title, String description) {
        Session newSession = new Session(title, description);
        mSessionRepository.createNewSession(newSession, null);
    }

    @Override
    public void loadSessions(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadSessions(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void setCallbacks() {
        callbackDelete = new SessionDataSource.DeleteSessionCallback() {
            @Override
            public void onSessionsDeleted() {
                mSessionView.onSessionsDeleted();
            }
        };
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link SessionDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadSessions(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mSessionView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mSessionRepository.refreshSessions();
        }

        //getting sessions from database repository
        mSessionRepository.getSessions(new SessionDataSource.LoadSessionsCallback() {
            @Override
            public void onSessionLoaded(List<Session> sessions) {
                if (!mSessionView.isActive()) {
                    return;
                }

                if (showLoadingUI) {
                    mSessionView.setLoadingIndicator(false);
                }

                processSessions(sessions);
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mSessionView.isActive()) {
                    return;
                }
                mSessionView.showLoadingSessionError();
            }
        });
    }

    private void processSessions(List<Session> sessions) {
        if (sessions.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            mSessionView.showEmptySessions(sessions);
        } else {
            // Show the list of tasks
            mSessionView.showSessions(sessions);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    //TODO-> mCurrentFiltering
    private void showFilterLabel() {

    }

    @Override
    public void addNewSession() {
        mSessionView.showAddSessionUi();
    }

    @Override
    public void openSessionDetails(String sessionId) {
        mSessionView.showSessionDetailsUi(sessionId);
    }

    @Override
    public void deleteCheckedSessions(ArrayList<String> sessionsId) {
        mSessionRepository.deleteSessions(sessionsId, false, callbackDelete);
        mSessionView.showCheckedSessionsDeleted();
        loadSessions(false);
    }

    @Override
    public void deleteAllSessions(ArrayList<String> sessionsId) {
        mSessionRepository.deleteSessions(sessionsId, true, callbackDelete);
        mSessionView.showAllSessionsDeleted();
        loadSessions(false);
    }

    @Override
    public void setSessionCompleted(String sessionId, boolean isCompleted) {
        mSessionRepository.setSessionChecked(sessionId, isCompleted);
    }
}
