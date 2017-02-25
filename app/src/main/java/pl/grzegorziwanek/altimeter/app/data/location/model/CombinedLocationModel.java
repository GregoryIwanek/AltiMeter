package pl.grzegorziwanek.altimeter.app.data.location.model;

import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;

/**
 * Created by Grzegorz Iwanek on 22.02.2017.
 */

public class CombinedLocationModel {
    private static CombinedLocationModel combinedLocationModel;
    private static double mCombinedAltitude = 0;
    private static long mUpdateTime = 0;

    public static CombinedLocationModel getInstance() {
        if (combinedLocationModel == null) {
            combinedLocationModel = new CombinedLocationModel();
        }
        return combinedLocationModel;
    }

    public static void updateCombinedAltitude() {
        double altitude = 0;
        int count = 0;
        if (GpsManager.isGpsEnabled()) {
            if (GpsAltitudeModel.getAltitude() != 0) {
                altitude += GpsAltitudeModel.getAltitude();
                count++;
            }
        }
        if (NetworkManager.isNetworkEnabled()) {
            if (NetworkAltitudeModel.getAltitude() != 0) {
                altitude += NetworkAltitudeModel.getAltitude();
                count++;
            }
        }
        if (BarometerManager.isBarometerEnabled()) {
            if (BarometerAltitudeModel.getAltitude() != 0) {
                altitude += BarometerAltitudeModel.getAltitude();
                count++;
            }
        }
        if (count != 0) {
            altitude /= count;
            mCombinedAltitude = altitude;
            mUpdateTime = System.currentTimeMillis();
        }
    }

    public static double getCombinedAltitude() {
        return mCombinedAltitude;
    }

    public static void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }
    
    public static long getUpdateTime() {
        return mUpdateTime;
    }
}
