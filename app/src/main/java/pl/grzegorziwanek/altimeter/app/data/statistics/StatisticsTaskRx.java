package pl.grzegorziwanek.altimeter.app.data.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import pl.grzegorziwanek.altimeter.app.utils.Constants;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by Grzegorz Iwanek on 19.03.2017.
 */
class StatisticsTaskRx {
    private static StatisticsTaskRx INSTANCE = null;
    private Context mContext;

    private StatisticsTaskRx(Context context) {
        mContext = context;
    }

    public static StatisticsTaskRx getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new StatisticsTaskRx(context);
        }
        return INSTANCE;
    }

    Observable<Map<String,String>> getStatisticsObservable() {
        return Observable.defer(new Func0<Observable<Map<String, String>>>() {
            @Override
            public Observable<Map<String, String>> call() {
                return Observable.just(getStatistics());
            }
        });
    }

    private Map<String,String> getStatistics() {
        SharedPreferences preferences = loadSharedPref();
        return getStatisticsMap(preferences);
    }

    private SharedPreferences loadSharedPref() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private Map<String,String> getStatisticsMap(SharedPreferences preferences) {
        String numSessions = preferences.getString("num_sessions", Constants.DEFAULT_TEXT);
        String numPoints = preferences.getString("num_points", Constants.DEFAULT_TEXT);
        String distance = preferences.getString("distance", Constants.DEFAULT_TEXT);
        String maxAlt = preferences.getString("max_altitude", Constants.DEFAULT_TEXT);
        String minAlt = preferences.getString("min_altitude", Constants.DEFAULT_TEXT);
        String longSession = preferences.getString("long_session", Constants.DEFAULT_TEXT);

        Map<String,String> map = new HashMap<>();
        map.put("num_sessions", numSessions);
        map.put("num_points", numPoints);
        map.put("distance", distance);
        map.put("max_altitude", maxAlt);
        map.put("min_altitude", minAlt);
        map.put("long_session", longSession);

        return map;
    }

    Observable<Boolean> getResetObservable() {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(isResetSuccess());
            }
        });
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
        editor.putString("num_sessions", Constants.DEFAULT_TEXT);
        editor.putString("num_points", Constants.DEFAULT_TEXT);
        editor.putString("distance", Constants.DEFAULT_TEXT);
        editor.putString("max_altitude", Constants.DEFAULT_TEXT);
        editor.putString("min_altitude", Constants.DEFAULT_TEXT);
        editor.putString("long_session", Constants.DEFAULT_TEXT);
        editor.apply();
    }
}
