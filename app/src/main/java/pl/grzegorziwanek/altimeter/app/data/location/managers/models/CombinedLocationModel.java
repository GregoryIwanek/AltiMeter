package pl.grzegorziwanek.altimeter.app.data.location.managers.models;

import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;
import pl.grzegorziwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Created by Grzegorz Iwanek on 22.02.2017.
 */

public class CombinedLocationModel {

    private double mCombinedAltitude = 0;
    private long mUpdateTime = 0;

    /**
     *
     * @param stateOfProviders if provider is enabled; 0-gps, 1-network, 2-barometer
     * @param modelsAltitude altitudes of providers; 0-gps, 1-network, 2-barometer
     */
    public void updateCombinedAltitude(boolean[] stateOfProviders, double[] modelsAltitude) {
        double altitude = 0;
        int count = 0;
        // gps
        if (stateOfProviders[0]) {
            if (modelsAltitude[0] != 0) {
                altitude += modelsAltitude[0];
                count++;
            }
        }
        // network
        if (stateOfProviders[1]) {
            if (modelsAltitude[1] != 0) {
                altitude += modelsAltitude[1];
                count++;
            }
        }
        // barometer
        if (stateOfProviders[2]) {
            if (modelsAltitude[2] != 0) {
                altitude += modelsAltitude[2];
                count++;
            }
        }
        if (count != 0) {
            altitude /= count;
            mCombinedAltitude = altitude;
            mUpdateTime = System.currentTimeMillis();
        }
    }

    public double getCombinedAltitude() {
        return FormatAndValueConverter.roundValue(mCombinedAltitude);
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }
    
    public long getUpdateTime() {
        return mUpdateTime;
    }
}
