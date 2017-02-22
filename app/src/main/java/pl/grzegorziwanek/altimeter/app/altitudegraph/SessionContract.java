package pl.grzegorziwanek.altimeter.app.altitudegraph;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;
import pl.grzegorziwanek.altimeter.app.data.Session;

/**
 * Created by Grzegorz Iwanek on 18.01.2017. That's it
 */
interface SessionContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showSessions(List<Session> sessions);

        void showEmptySessions(List<Session> sessions);

        void showAddSessionUi();

        void showSessionDetailsUi(String clickedSessionId);

        void showLoadingSessionError();

        boolean isActive();

        void showCheckedSessionsDeleted();

        void showAllSessionsDeleted();

        void onSessionsDeleted();
    }

    interface Presenter extends BasePresenter {

        void loadSessions(boolean forceUpdate);

        void addNewSession();

        void openSessionDetails(String sessionId);

        void deleteCheckedSessions(ArrayList<String> sessionsId);

        void deleteAllSessions(ArrayList<String> sessionsId);

        void setSessionCompleted(String sessionId, boolean isCompleted);
    }
}

