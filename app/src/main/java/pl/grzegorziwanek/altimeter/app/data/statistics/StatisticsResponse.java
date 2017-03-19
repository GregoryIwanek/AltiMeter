package pl.grzegorziwanek.altimeter.app.data.statistics;

import java.util.Map;

/**
 * Created by Grzegorz Iwanek on 19.03.2017.
 */

public interface StatisticsResponse {

        void onStatisticsReset(boolean isResetSuccess);

        void onStatisticsLoaded(Map<String,String> statisticsMap);
}
