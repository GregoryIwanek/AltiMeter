package pl.grzegorziwanek.altimeter.app.data.location.managers.models;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class NetworkAltitudeModel {
    private static NetworkAltitudeModel networkAltitudeModel;
    private static double mAltitude = 0;
    private static long mMeasureTime;

    public static NetworkAltitudeModel getInstance() {
        if (networkAltitudeModel == null) {
            networkAltitudeModel = new NetworkAltitudeModel();
        }
        return networkAltitudeModel;
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
