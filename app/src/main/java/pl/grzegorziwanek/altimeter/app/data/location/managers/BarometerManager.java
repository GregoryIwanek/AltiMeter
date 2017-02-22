package pl.grzegorziwanek.altimeter.app.data.location.managers;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class BarometerManager {
    private static BarometerManager mBarometerManager;
    private static boolean mBarometerEnabled;
    private static long mAirportMeasureTime = 0;
    private static double mUpdateLatitude = 0;
    private static double mUpdateLongitude = 0;

    public static BarometerManager getInstance() {
        if (mBarometerManager == null) {
            mBarometerManager = new BarometerManager();
        }
        return mBarometerManager;
    }

    public static boolean isBarometerEnabled() {
        return mBarometerEnabled;
    }

    public static void setBarometerEnabled(boolean barometerEnabled) {
        mBarometerEnabled = barometerEnabled;
    }

    public static long getAirportMeasureTime() {
        return mAirportMeasureTime;
    }

    public static void setAirportMeasureTime(long airportMeasureTime) {
        mAirportMeasureTime = airportMeasureTime;
    }

    public static double getUpdateLatitude() {
        return mUpdateLatitude;
    }

    public static void setUpdateLatitude(double updateLatitude) {
        mUpdateLatitude = updateLatitude;
    }

    public static double getUpdateLongitude() {
        return mUpdateLongitude;
    }

    public static void setUpdateLongitude(double updateLongitude) {
        mUpdateLongitude = updateLongitude;
    }
}
