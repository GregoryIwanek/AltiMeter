package pl.grzegorziwanek.altimeter.app.data.statistics;

import android.content.Context;

import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Grzegorz Iwanek on 19.03.2017.
 */

public class StatisticsManager {
    private static StatisticsResponse mCallback;
    private static StatisticsManager INSTANCE = null;
    private Context mContext;

    private StatisticsManager(Context context) {
        mContext = context;
    }

    public static StatisticsManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new StatisticsManager(context);
        }
        return INSTANCE;
    }

    public void loadStoredStatisticsRx(StatisticsResponse callback) {
        mCallback = callback;
        StatisticsTaskRx taskRx = StatisticsTaskRx.getInstance(mContext);
        taskRx.getStatisticsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, String>>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, String> statisticsMap) {
                        mCallback.onStatisticsLoaded(statisticsMap);
                    }
                });
    }

    public void resetStoredStatisticsRx(StatisticsResponse callback) {
        mCallback = callback;
        StatisticsTaskRx taskRx = StatisticsTaskRx.getInstance(mContext);
        taskRx.getResetObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mCallback.onStatisticsReset(aBoolean);
                    }
                });
    }
}
