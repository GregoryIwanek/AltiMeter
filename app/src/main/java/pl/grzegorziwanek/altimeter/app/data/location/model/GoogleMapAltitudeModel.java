package pl.grzegorziwanek.altimeter.app.data.location.model;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */

public class GoogleMapAltitudeModel {
    private static GoogleMapAltitudeModel googleMapAltitudeModel;
    private static double mAltitude = 0;
    private static long mMeasureTime;

    public static GoogleMapAltitudeModel getInstance() {
        if (googleMapAltitudeModel == null) {
            googleMapAltitudeModel = new GoogleMapAltitudeModel();
        }
        return googleMapAltitudeModel;
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
