package pl.gregoryiwanek.altimeter.app.data.location.managers.models;

public class GpsAltitudeModel {

    private double mAltitude = 0;
    private boolean mIsInitiated = false;

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(Double altitude) {
        mAltitude = altitude;
        if (altitude != 0) {
            mIsInitiated = true;
        }
    }

    public boolean isInitiated() {
        return mIsInitiated;
    }
}
