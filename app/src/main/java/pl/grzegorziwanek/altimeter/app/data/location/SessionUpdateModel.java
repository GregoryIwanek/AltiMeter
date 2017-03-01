package pl.grzegorziwanek.altimeter.app.data.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Created by Grzegorz Iwanek on 01.03.2017.
 */

abstract class SessionUpdateModel {

    /**
     *
     * @param location
     * @param context
     */
    static void saveAirportUpdateLocation(Location location, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("measureTime", String.valueOf(location.getTime()));
        editor.putFloat("updateLatitude", (float) location.getLatitude());
        editor.putFloat("updateLongitude", (float) location.getLongitude());
        editor.apply();
    }

    /**
     *
     * @param context
     */
    static void readAirportUpdateLocation(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String str = sharedPref.getString("measureTime", "0");
        long time = Long.valueOf(str);
        float lat = sharedPref.getFloat("updateLatitude", 0);
        float lon = sharedPref.getFloat("updateLongitude", 0);
        BarometerManager.setAirportMeasureTime(time);
        BarometerManager.setUpdateLatitude(lat);
        BarometerManager.setUpdateLongitude(lon);
    }

    /**
     *
     * @param context
     */
    static void saveAirportPressure(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("airportPressure", String.valueOf(BarometerManager.getClosestAirportPressure()));
        editor.apply();
    }

    /**
     *
     * @param context
     */
    static void readAirportPressure(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String pressureStr = sharedPref.getString("airportPressure", "0");
        double pressure = Double.valueOf(pressureStr);
        BarometerManager.setClosestAirportPressure(pressure);
    }

    /**
     *
     * @param context
     */
    static void updateDistanceUnits(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }

    /**
     *
     * @param session
     */
    static void setSessionsHeight(Session session) {
        Double currAltitude = session.getCurrentElevation();
        Double minHeight = session.getMinHeight();
        Double maxHeight = session.getMaxHeight();

        Double newMinHeight =
                FormatAndValueConverter.updateMinAltitudeValue(currAltitude, minHeight);
        Double newMaxHeight =
                FormatAndValueConverter.updateMaxAltitudeValue(currAltitude, maxHeight);
        String newMinStr =
                FormatAndValueConverter.setMinMaxString(newMinHeight);
        String newMaxStr =
                FormatAndValueConverter.setMinMaxString(newMaxHeight);

        session.setMinHeight(newMinHeight);
        session.setMinHeightStr(newMinStr);
        session.setMaxHeight(newMaxHeight);
        session.setMaxHeightStr(newMaxStr);
    }

    /**
     *
     * @param session
     */
    static void setSessionsDistance(Session session) {
        if (session.getLastLocation() != null) {
            Location lastLocation = session.getLastLocation();
            Location currentLocation = session.getCurrentLocation();
            Double currentDistance = session.getDistance();

            Double distance = FormatAndValueConverter.updateDistanceValue(
                    lastLocation, currentLocation, currentDistance);
            session.setDistance(distance);

            String distanceStr =
                    FormatAndValueConverter.setDistanceStr(distance);
            session.setDistanceStr(distanceStr);
        }
    }

    /**
     *
     * @param session
     */
    static void setGeoCoordinateStr(Session session) {
        Location location = session.getCurrentLocation();
        String latitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLatitude(), true);
        String longitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLongitude(), false);
        session.setLatitudeStr(latitudeStr);
        session.setLongitudeStr(longitudeStr);
    }
}
