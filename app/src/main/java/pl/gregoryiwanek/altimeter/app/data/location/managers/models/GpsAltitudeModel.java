package pl.gregoryiwanek.altimeter.app.data.location.managers.models;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */
public class GpsAltitudeModel {

    private double mAltitude = 0;
    private boolean mIsInitiated = false;
    private long mMeasureTime;

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(Double altitude) {
        mAltitude = altitude;
        if (altitude != 0) {
            mIsInitiated = true;
        }
    }

    public long getMeasureTime() {
        return mMeasureTime;
    }

    public void setMeasureTime(long time) {
        mMeasureTime = time;
    }

    public boolean isInitiated() {
        return mIsInitiated;
    }
}
