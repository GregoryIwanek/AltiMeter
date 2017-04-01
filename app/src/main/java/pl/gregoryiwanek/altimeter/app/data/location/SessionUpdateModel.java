package pl.gregoryiwanek.altimeter.app.data.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.Session;
import pl.gregoryiwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.gregoryiwanek.altimeter.app.data.location.managers.models.CombinedLocationModel;
import pl.gregoryiwanek.altimeter.app.recordingsession.RecordingSessionFragment;
import pl.gregoryiwanek.altimeter.app.utils.Constants;
import pl.gregoryiwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Consists Session's value and state update model.
 */
class SessionUpdateModel {
    /**
     * Saves location and measure time of device's location when called for update of
     * closest airports in proximity.
     * @param location current location of the device;
     * @param context context, required to use SharedPreferences;
     */
    void saveAirportUpdateLocation(Location location, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("measureTime", String.valueOf(location.getTime()));
        editor.putFloat("updateLatitude", (float) location.getLatitude());
        editor.putFloat("updateLongitude", (float) location.getLongitude());
        editor.apply();
    }

    /**
     * Reads saved device's location at the moment of call for airports in proximity update.
     * @param context context required to read SharedPreferences;
     */
    void readAirportUpdateLocation(Context context, BarometerManager barometerManager) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String str = sharedPref.getString("measureTime", "0");
        long time = Long.valueOf(str);
        float lat = sharedPref.getFloat("updateLatitude", 0);
        float lon = sharedPref.getFloat("updateLongitude", 0);
        barometerManager.setAirportMeasureTime(time);
        barometerManager.setUpdateLatitude(lat);
        barometerManager.setUpdateLongitude(lon);
    }

    /**
     * Saves closest station's value of the pressure on the local sea level.
     * @param context context required to use SharedPreferences;
     */
    void saveAirportPressure(Context context, BarometerManager barometerManager) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("airportPressure", String.valueOf(barometerManager.getClosestAirportPressure()));
        editor.apply();
    }

    /**
     * Reads stored pressure at the local sea level of the closest airport station.
     * @param context context required to use SharedPreferences;
     */
    void readAirportPressure(Context context, BarometerManager barometerManager) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String pressureStr = sharedPref.getString("airportPressure", "0");
        double pressure = Double.valueOf(pressureStr);
        barometerManager.setClosestAirportPressure(pressure);
    }

    /**
     * Updates units symbol chosen by user in settings.
     * @param context context required to use SharedPreferences;
     */
    void updateDistanceUnits(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }

    /**
     * Updates minimum and maximum height (recorded altitude) of the current session.
     * @param session current Session object to update;
     */
    void setSessionsHeight(Session session) {
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
     * Updates value of the distance travelled between last and current location;
     * @param session current Session object to update;
     */
    void setSessionsDistance(Session session) {
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
     * Defines coordinate strings (latitude and longitude) for the current location.
     * Format of the coordinates defined inside {@link FormatAndValueConverter};
     * @param session current Session object to update;
     */
    void setGeoCoordinateStr(Session session) {
        Location location = session.getCurrentLocation();
        String latitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLatitude(), true);
        String longitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLongitude(), false);
        session.setLatitudeStr(latitudeStr);
        session.setLongitudeStr(longitudeStr);
    }

    /**
     * Assigns current elevation value from all chosen sources (GPS, Network, Barometer) {@link CombinedLocationModel}
     * for the current Session;
     * @param session current Session object to update;
     */
    void setCurrentElevation(Session session, CombinedLocationModel combinedLocationModel) {
        double elevation = combinedLocationModel.getCombinedAltitude();
        session.setCurrentElevation(elevation);
        session.getCurrentLocation().setAltitude(elevation);
    }

    /**
     * Sets new current location value for the current Session.
     * Called whenever new correct location object is provided via GPS LocationListener.
     * @param session current Session object to update;
     * @param location location to set as new one;
     */
    void saveSessionsLocation(Session session, Location location) {
        if (session.getCurrentLocation() != null) {
            session.setLastLocation(session.getCurrentLocation());
        }
        session.setCurrLocation(location);
    }

    /**
     * Adds current location object to list of the all location objects recorded during session.
     * @param session current Session object to update;
     */
    void appendLocationToList(Session session) {
        session.appendLocationPoint(session.getCurrentLocation());
    }

    /**
     * Define elevation of the newest location (current) on the list of location, as the average
     * elevation of all combined sources (GPS, Network, Barometer) via {@link CombinedLocationModel}.
     * @param session current Session object to update;
     */
    void setElevationOnList(Session session, CombinedLocationModel combinedLocationModel) {
        double elevation = FormatAndValueConverter.roundValue(combinedLocationModel.getCombinedAltitude());
        session.setElevationOnList(elevation);
    }

    /**
     * Updates values of global, combined statistics of all sessions of the user.
     * Compares values by key stored in String[] array.xml.
     * Called when activity hosting {@link RecordingSessionFragment} is destroyed (onDestroy)
     * and only if recording session is initiated (has saved points).
     * @param context context required to use SharedPreferences (global statistics stored inside SP);
     * @param session current Session object holding data to fetch from;
     */
    void updateGlobalStatistics(Context context, Session session) {
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
            if (!isStatisticValueBigger(maxAltStr, maxAltSession) && maxAltSession != maxAltDefault) {
                maxAltStr = String.valueOf(maxAltSession);
            }
            editor.putString(statisticsNames[3], maxAltStr);

            String minAltStr = preferences.getString(statisticsNames[4], Constants.DEFAULT_TEXT);
            double minAltSession = session.getMinHeight();
            int minAltDefault = -10000;
            if ((isStatisticValueBigger(minAltStr, minAltSession) || minAltStr.equals(Constants.DEFAULT_TEXT))
                    && minAltSession != minAltDefault) {
                minAltStr = String.valueOf(minAltSession);
            }
            editor.putString(statisticsNames[4], minAltStr);

            String longestTimeStr = preferences.getString(statisticsNames[5], Constants.DEFAULT_TEXT);
            long recordingLengthSession = getRecordingLength(session);
            String milliLongestTimeStr = FormatAndValueConverter.formatToZeroDateIfDefaultText(longestTimeStr);
            milliLongestTimeStr = FormatAndValueConverter.getTimeMillisFromStr(milliLongestTimeStr);
            if (!isStatisticValueBigger(milliLongestTimeStr, recordingLengthSession)) {
                longestTimeStr = FormatAndValueConverter.setHoursDateString(recordingLengthSession);
            }
            editor.putString(statisticsNames[5], longestTimeStr);

            editor.apply();
        }
    }

    private boolean isSessionInitiated(Session session) {
        return session.getCurrentLocation() != null || session.getLocationList().size() != 0;
    }

    private boolean isStatisticValueBigger(String statValueStr, double sessionValue) {
        statValueStr = FormatAndValueConverter.formatToZeroIfDefaultText(statValueStr);
        double statValue = Double.valueOf(statValueStr);
        return statValue > sessionValue;
    }

    private String incrementValue(String oldValueStr) {
        oldValueStr = FormatAndValueConverter.formatToZeroIfDefaultText(oldValueStr);
        int oldValue = Integer.valueOf(oldValueStr);
        return String.valueOf(oldValue + 1);
    }

    private String sumCount(String oldValueStr, double sessionValue) {
        oldValueStr = FormatAndValueConverter.formatToZeroIfDefaultText(oldValueStr);
        int oldValue = Integer.valueOf(oldValueStr);
        oldValue += sessionValue;
        return String.valueOf(oldValue);
    }

    private long getRecordingLength(Session session) {
        if (session.getLocationList() != null && session.getLocationList().size() > 0) {
            ArrayList<Location> locations = session.getLocationList();
            return locations.get(locations.size()-1).getTime() - locations.get(0).getTime();
        }
        return 0;
    }
}
