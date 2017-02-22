package pl.grzegorziwanek.altimeter.app.data.location.managers;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class GpsManager {
    private static GpsManager gpsManager;
    private static boolean mGpsEnabled;
    private static long mMeasureTime;

    public static GpsManager getInstance() {
        if (gpsManager == null) {
            gpsManager = new GpsManager();
        }
        return gpsManager;
    }

    public static void setGpsEnabled(boolean gpsEnabled) {
        mGpsEnabled = gpsEnabled;
    }

    public static boolean isGpsEnabled() {
        return mGpsEnabled;
    }

    public static long getMeasureTime() {
        return mMeasureTime;
    }

    public static void setMeasureTime(long measureTime) {
        mMeasureTime = measureTime;
    }
}


