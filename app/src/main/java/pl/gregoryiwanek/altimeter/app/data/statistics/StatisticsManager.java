package pl.gregoryiwanek.altimeter.app.data.statistics;

import android.content.Context;

import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Consists manager class dealing with global statistics of the app.
 */
public class StatisticsManager {

    private StatisticsResponse mCallback;
    private final Context mContext;

    public StatisticsManager(Context context) {
        mContext = context;
    }

    public void loadStoredStatisticsRx(StatisticsResponse callback) {
        mCallback = callback;
        StatisticsTaskRx taskRx = new StatisticsTaskRx(mContext);
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
        StatisticsTaskRx taskRx = new StatisticsTaskRx(mContext);
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
