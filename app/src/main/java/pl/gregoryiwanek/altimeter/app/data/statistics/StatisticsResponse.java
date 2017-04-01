package pl.gregoryiwanek.altimeter.app.data.statistics;

import java.util.Map;

public interface StatisticsResponse {

        void onStatisticsReset(boolean isResetSuccess);

        void onStatisticsLoaded(Map<String,String> statisticsMap);
}
