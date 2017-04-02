package pl.gregoryiwanek.altimeter.app.data.location.managers;

import java.util.List;

import pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */
public class BarometerManager {
    private boolean mBarometerEnabled;
    private long mAirportMeasureTime = 0;
    private double mUpdateLatitude = 0;
    private double mUpdateLongitude = 0;
    private double mClosestAirportPressure = 0;
    private List<XmlAirportValues> mAirportsList = null;

    public boolean isBarometerEnabled() {
        return mBarometerEnabled;
    }

    public void setBarometerEnabled(boolean barometerEnabled) {
        mBarometerEnabled = barometerEnabled;
    }

    public long getAirportMeasureTime() {
        return mAirportMeasureTime;
    }

    public void setAirportMeasureTime(long airportMeasureTime) {
        mAirportMeasureTime = airportMeasureTime;
    }

    public double getUpdateLatitude() {
        return mUpdateLatitude;
    }

    public void setUpdateLatitude(double updateLatitude) {
        mUpdateLatitude = updateLatitude;
    }

    public double getUpdateLongitude() {
        return mUpdateLongitude;
    }

    public void setUpdateLongitude(double updateLongitude) {
        mUpdateLongitude = updateLongitude;
    }

    public void resetData() {
        mBarometerEnabled = false;
        mAirportMeasureTime = 0;
        mUpdateLatitude = 0;
        mUpdateLongitude = 0;
        resetList();
    }

    public List<XmlAirportValues> getAirportsList() {
        return mAirportsList;
    }

    public void setAirportsList(List<XmlAirportValues> airportsList) {
        mAirportsList = airportsList;
    }

    public void resetList(){
        mAirportsList = null;
    }

    public double getClosestAirportPressure() {
        return mClosestAirportPressure;
    }

    public void setClosestAirportPressure(double closestAirportPressure) {
        mClosestAirportPressure = closestAirportPressure;
    }

}
