package pl.grzegorziwanek.altimeter.app.data.location.model;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class BarometerAltitudeModel {
    private static BarometerAltitudeModel barometerAltitudeModel;
    private static double mAltitude = 0;
    private static long mMeasureTime;

    public static BarometerAltitudeModel getInstance() {
        if (barometerAltitudeModel == null) {
            barometerAltitudeModel = new BarometerAltitudeModel();
        }
        return barometerAltitudeModel;
    }

    public static double getAltitude() {
        return mAltitude;
    }

    public static void setAltitude(Double altitude) {
        mAltitude = altitude;
    }

    public static long getMeasureTime() {
        return mMeasureTime;
    }

    public static void setMeasureTime(long time) {
        mMeasureTime = time;
    }
}
