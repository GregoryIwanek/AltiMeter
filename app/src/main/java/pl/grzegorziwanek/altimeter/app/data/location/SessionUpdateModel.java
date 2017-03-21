package pl.grzegorziwanek.altimeter.app.data.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.models.CombinedLocationModel;
import pl.grzegorziwanek.altimeter.app.utils.Constants;
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
        double currAltitude = session.getCurrentElevation();
        double minHeight = session.getMinHeight();
        double maxHeight = session.getMaxHeight();

        double newMinHeight =
                FormatAndValueConverter.updateMinAltitudeValue(currAltitude, minHeight);
        double newMaxHeight =
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
            double currentDistance = session.getDistance();

            double distance = FormatAndValueConverter.updateDistanceValue(
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

    /**
     *
     * @param session
     */
    static void setCurrentElevation(Session session) {
        double elevation = CombinedLocationModel.getCombinedAltitude();
        session.setCurrentElevation(elevation);
        session.getCurrentLocation().setAltitude(elevation);
    }


    /**
     *
     * @param session
     * @param location
     */
    static void saveSessionsLocation(Session session, Location location) {
        if (session.getCurrentLocation() != null) {
            session.setLastLocation(session.getCurrentLocation());
        }
        session.setCurrLocation(location);
    }

    /**
     *
     * @param session
     */
    static void appendLocationToList(Session session) {
        session.appendLocationPoint(session.getCurrentLocation());
    }

    /**
     *
     * @param session
     */
    static void setElevationOnList(Session session) {
        double elevation = FormatAndValueConverter.roundValue(CombinedLocationModel.getCombinedAltitude());
        session.setElevationOnList(elevation);
    }

    static void updateGlobalStatistics(Context context, Session session) {
        if (isSessionInitiated(session)) {
            // statistics array inside values folder
            // 0 - num_sessions; 1 - num_points; 2 - distance; 3 - max_altitude; 4 - min_altitude; 5 - long_session;
            String[] statisticsNames = context.getResources().getStringArray(R.array.statistics_names);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();

            String numSessionsStr = preferences.getString(statisticsNames[0], Constants.DEFAULT_TEXT);
            numSessionsStr = incrementValue(numSessionsStr);
            editor.putString(statisticsNames[0], numSessionsStr);

            String numPointsStr = preferences.getString(statisticsNames[1], Constants.DEFAULT_TEXT);
            double numPointsSession = session.getLocationList().size();
            numPointsStr = sumCount(numPointsStr, numPointsSession);
            editor.putString(statisticsNames[1], numPointsStr);

            String distanceStr = preferences.getString(statisticsNames[2], Constants.DEFAULT_TEXT);
            double distanceSession = session.getDistance();
            distanceStr = sumCount(distanceStr, distanceSession);
            editor.putString(statisticsNames[2], distanceStr);

            String maxAltStr = preferences.getString(statisticsNames[3], Constants.DEFAULT_TEXT);
            double maxAltSession = session.getMaxHeight();
            int maxAltDefault = 10000;
            if (isStatisticValueBigger(maxAltStr, maxAltSession) && maxAltSession != maxAltDefault) {
                maxAltStr = String.valueOf(maxAltSession);
            }
            editor.putString(statisticsNames[3], maxAltStr);

            String minAltStr = preferences.getString(statisticsNames[4], Constants.DEFAULT_TEXT);
            double minAltSession = session.getMinHeight();
            int minAltDefault = -10000;
            if (!isStatisticValueBigger(minAltStr, minAltSession) && minAltSession != minAltDefault) {
                minAltStr = String.valueOf(minAltSession);
            }
            editor.putString(statisticsNames[4], minAltStr);

            String longestTimeStr = preferences.getString(statisticsNames[5], Constants.DEFAULT_TEXT);
            long recordingLengthSession = getRecordingLength(session);
            System.out.println("recording length session: " + recordingLengthSession);
            String milliLongestTimeStr = FormatAndValueConverter.formatToZeroDateIfDefaultText(longestTimeStr);
            milliLongestTimeStr = FormatAndValueConverter.getTimeMillisFromStr(milliLongestTimeStr);
            if (!isStatisticValueBigger(milliLongestTimeStr, recordingLengthSession)) {
                System.out.println("mili is: " + milliLongestTimeStr);
                longestTimeStr = FormatAndValueConverter.setHoursDateString(recordingLengthSession);
            }
            editor.putString(statisticsNames[5], longestTimeStr);

            editor.apply();
        }
    }

    private static boolean isSessionInitiated(Session session) {
        return session.getCurrentLocation() != null || session.getLocationList().size() != 0;
    }

    // TODO: 21.03.2017 change name for longest time
    private static boolean isStatisticValueBigger(String statValueStr, double sessionValue) {
        statValueStr = FormatAndValueConverter.formatToZeroIfDefaultText(statValueStr);
        double statValue = Double.valueOf(statValueStr);
        System.out.println("session value is: " + sessionValue);
        System.out.println("stat value is: " + statValue);
        return statValue > sessionValue;
    }

    private static String incrementValue(String oldValueStr) {
        oldValueStr = FormatAndValueConverter.formatToZeroIfDefaultText(oldValueStr);
        int oldValue = Integer.valueOf(oldValueStr);
        return String.valueOf(oldValue + 1);
    }

    private static String sumCount(String oldValueStr, double sessionValue) {
        oldValueStr = FormatAndValueConverter.formatToZeroIfDefaultText(oldValueStr);
        int oldValue = Integer.valueOf(oldValueStr);
        oldValue += sessionValue;
        return String.valueOf(oldValue);
    }

    private static long getRecordingLength(Session session) {
        if (session.getLocationList() != null) {
            ArrayList<Location> locations = session.getLocationList();
            return locations.get(locations.size()-1).getTime() - locations.get(0).getTime();
        }
        return 0;
    }
}
