package pl.grzegorziwanek.altimeter.app.altitudegraph;

import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;
import pl.grzegorziwanek.altimeter.app.model.Session;

/**
 * Created by Grzegorz Iwanek on 18.01.2017.
 */
public interface SessionContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showSessions(List<Session> sessions);

        void showAddSession();

        void showGraphDetailsUi(String graphId);

        void showGraphMarkedComplete();

        void showGraphMarkedActive();

        void showCompletedGraphsCleared();

        void showLoadingSessionError();

        void showNoSessions();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveSessions();

        void showNoCompletedSessions();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadSessions(boolean forceUpdate);

        void addNewSession();

        void openGraphDetails(@NonNull GraphView requestedGraphs);

        void completeGraphs(@NonNull GraphView completedGraphs);

        void activeGraphs(@NonNull GraphView activeGraphs);

        void clearCompletedGraphs();

        void setFiltering(SessionFilterType requestType);

        SessionFilterType getFiltering();
    }
}

