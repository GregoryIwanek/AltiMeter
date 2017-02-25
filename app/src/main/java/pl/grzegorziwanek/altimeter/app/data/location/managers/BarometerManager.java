package pl.grzegorziwanek.altimeter.app.data.location.managers;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.xmlparser.XmlAirportValues;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class BarometerManager {
    private static BarometerManager mBarometerManager;
    private static boolean mBarometerEnabled;
    private static long mAirportMeasureTime = 0;
    private static float mSeaLevelPressure = 0;
    private static double mUpdateLatitude = 0;
    private static double mUpdateLongitude = 0;
    private static double mClosestAirportPressure = 0;
    private static List<XmlAirportValues> mAirportsList = null;

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

    public static float getSeaLevelPressure() {
        return mSeaLevelPressure;
    }

    public static void setSeaLevelPressure(float seaLevelPressure) {
        mSeaLevelPressure = seaLevelPressure;
    }

    public static void resetData() {
        mBarometerEnabled = false;
        mAirportMeasureTime = 0;
        mSeaLevelPressure = 0;
        mUpdateLatitude = 0;
        mUpdateLongitude = 0;
        resetList();
    }

    public static List<XmlAirportValues> getAirportsList() {
        return mAirportsList;
    }

    public static void setAirportsList(List<XmlAirportValues> airportsList) {
        mAirportsList = airportsList;
    }

    public static void resetList(){
        //mAirportsList = null;
    }

    public static double getClosestAirportPressure() {
        return mClosestAirportPressure;
    }

    public static void setClosestAirportPressure(double closestAirportPressure) {
        mClosestAirportPressure = closestAirportPressure;
    }
}
