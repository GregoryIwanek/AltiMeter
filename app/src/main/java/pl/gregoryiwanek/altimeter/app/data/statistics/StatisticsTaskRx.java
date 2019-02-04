package pl.gregoryiwanek.altimeter.app.data.statistics;

import android.content.*;
import android.preference.*;

import java.util.*;

import pl.gregoryiwanek.altimeter.app.*;
import pl.gregoryiwanek.altimeter.app.utils.*;
import rx.Observable;

/**
 * Consists JavaRx task dealing with fetching global statistics of an app.
 */
class StatisticsTaskRx {

    private final Context mContext;

    StatisticsTaskRx(Context context) {
        mContext = context;
    }

    // load global statistics, get observable Map with statistics
    Observable<Map<String,String>> getStatisticsObservable() {
        return Observable.defer(() -> Observable.just(getStatistics()));
    }

    private Map<String,String> getStatistics() {
        SharedPreferences preferences = loadSharedPref();
        return getStatisticsMap(preferences);
    }

    private SharedPreferences loadSharedPref() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private Map<String,String> getStatisticsMap(SharedPreferences preferences) {
        Map<String,String> map = new HashMap<>();
        String[] statisticsNames = mContext.getResources().getStringArray(R.array.statistics_names);
        for (String name : statisticsNames) {
            String statistic = preferences.getString(name, Constants.DEFAULT_TEXT);
            map.put(name, statistic);
        }
        return map;
    }

    // reset statistics, get boolean observable if succeed or not
    Observable<Boolean> getResetObservable() {
        return Observable.defer(() -> Observable.just(isResetSuccess()));
    }

    private Boolean isResetSuccess() {
        try {
            SharedPreferences preferences = loadSharedPref();
            resetStatistics(preferences);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void resetStatistics(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        String[] statisticsNames = mContext.getResources().getStringArray(R.array.statistics_names);
        for (String name: statisticsNames) {
            editor.putString(name, Constants.DEFAULT_TEXT);
        }
        editor.apply();
    }
}
