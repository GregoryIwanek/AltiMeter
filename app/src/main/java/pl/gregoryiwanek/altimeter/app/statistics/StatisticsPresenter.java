package pl.gregoryiwanek.altimeter.app.statistics;

import androidx.annotation.NonNull;

import java.util.Map;

import pl.gregoryiwanek.altimeter.app.data.statistics.StatisticsManager;
import pl.gregoryiwanek.altimeter.app.data.statistics.StatisticsResponse;
import pl.gregoryiwanek.altimeter.app.statistics.StatisticsContract.Presenter;
import pl.gregoryiwanek.altimeter.app.statistics.StatisticsContract.View;

/**
 * Consists presenter class of Statistics section.
 * Bridge between {@link View} and {@link StatisticsManager};
 */
class StatisticsPresenter implements Presenter {

    private final View mStatisticsView;
    private StatisticsResponse mCallback;
    private final StatisticsManager mManager;

    StatisticsPresenter(@NonNull View statisticsView, @NonNull StatisticsManager manager) {
        mManager = manager;
        mStatisticsView = statisticsView;
        mStatisticsView.setPresenter(this);
        setCallback();
    }

    @Override
    public void start() {
        mManager.loadStoredStatisticsRx(mCallback);
    }

    private void setCallback() {
        mCallback = new StatisticsResponse() {
            @Override
            public void onStatisticsReset(boolean isResetSuccess) {
                if (isResetSuccess) {
                    start();
                    mStatisticsView.showIsResetSuccess("Statistics reset successful.");
                } else {
                    mStatisticsView.showIsResetSuccess("Statistics reset failed.");
                }
            }

            @Override
            public void onStatisticsLoaded(Map<String, String> statisticsMap) {
                setStatisticsTextViews(statisticsMap);
            }
        };
    }

    @Override
    public void resetStatistics() {
        mManager.resetStoredStatisticsRx(mCallback);
    }

    private void setStatisticsTextViews(Map<String,String> statisticsMap) {
        mStatisticsView.setNumSessionsTextView(statisticsMap.get("num_sessions"));
        mStatisticsView.setNumPointsTextView(statisticsMap.get("num_points"));
        mStatisticsView.setDistanceTextView(statisticsMap.get("distance"));
        mStatisticsView.setMaxAltTextView(statisticsMap.get("max_altitude"));
        mStatisticsView.setMinAltTextView(statisticsMap.get("min_altitude"));
        mStatisticsView.setLongSessionTextView(statisticsMap.get("long_session"));
    }
}