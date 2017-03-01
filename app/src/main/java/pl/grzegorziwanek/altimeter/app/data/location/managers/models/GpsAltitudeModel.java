package pl.grzegorziwanek.altimeter.app.data.location.managers.models;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class GpsAltitudeModel {
    private static GpsAltitudeModel gpsAltitudeModel;
    private static double mAltitude = 0;
    private static long mMeasureTime;

    public static GpsAltitudeModel getInstance() {
        if (gpsAltitudeModel == null) {
            gpsAltitudeModel = new GpsAltitudeModel();
        }
        return gpsAltitudeModel;
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
