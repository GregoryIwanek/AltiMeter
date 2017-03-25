package pl.grzegorziwanek.altimeter.app.data.location.managers;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class NetworkManager {

    private boolean mNetworkEnabled;
    private long mMeasureTime = 0;

    public boolean isNetworkEnabled() {
        return mNetworkEnabled;
    }

    public void setNetworkEnabled(boolean networkEnabled) {
        mNetworkEnabled = networkEnabled;
    }

    public long getMeasureTime() {
        return mMeasureTime;
    }

    public void setMeasureTime(long measureTime) {
        mMeasureTime = measureTime;
    }

    public void resetData() {
        mNetworkEnabled = false;
        mMeasureTime = 0;
    }
}
