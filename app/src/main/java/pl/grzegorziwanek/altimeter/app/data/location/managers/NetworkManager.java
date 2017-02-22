package pl.grzegorziwanek.altimeter.app.data.location.managers;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class NetworkManager {
    private static NetworkManager mNetworkManager;
    private static boolean mNetworkEnabled;
    private static long mMeasureTime;

    public static NetworkManager getInstance() {
        if (mNetworkManager == null) {
            mNetworkManager = new NetworkManager();
        }
        return mNetworkManager;
    }

    public static boolean isNetworkEnabled() {
        return mNetworkEnabled;
    }

    public static void setNetworkEnabled(boolean networkEnabled) {
        mNetworkEnabled = networkEnabled;
    }

    public static long getMeasureTime() {
        return mMeasureTime;
    }

    public static void setMeasureTime(long measureTime) {
        mMeasureTime = measureTime;
    }
}
