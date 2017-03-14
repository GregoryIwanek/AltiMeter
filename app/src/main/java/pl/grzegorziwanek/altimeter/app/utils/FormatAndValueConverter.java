package pl.grzegorziwanek.altimeter.app.utils;

import android.location.Location;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 27.11.2016.
 * Consist upgraded ArrayAdapter<String> class; used to update all wanted text views in view at once (single ArrayAdapter by default upgrade one object);
 * Responsible for comparing value of current data and if corresponding TextViews need to be changed;
 */
public class FormatAndValueConverter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    //TODO -> kick out mUnitsFormat and Symbol and move it to Presenter(?), LocationUpdateManager(?)
    private static String mUnitsFormat;
    private static String mUnitsSymbol;

    /**
     * Set required by user format of units
     * @param unitsFormat units format chosen by user
     */
    public static void setUnitsFormat(String unitsFormat) {
        mUnitsFormat = unitsFormat;
    }

    /** Set geographical coordinates string (this is entry point into this section)
     * @param coordinate geographic coordinate to forge
     * @param isLatitude define if given coordinate is latitude or longitude
     * @return formatted geographical coordinate string
     * example: 51°23'13''N*
     */
    public static String setGeoCoordinateStr(Double coordinate, boolean isLatitude) {
        String str;
        str = convertCoordinateToStr(coordinate);
        str = replaceSpecialSigns(str);

        int indexOfDoubleApostropheInStr;
        indexOfDoubleApostropheInStr = getIndexOfDoubleApostropheInStr(str);
        str = subtractStr(str, indexOfDoubleApostropheInStr);
        str = setGeoDirectionSymbol(str, coordinate, isLatitude);

        return str;
    }

    private static String convertCoordinateToStr(Double coordinate) {
        return Location.convert(coordinate, Location.FORMAT_SECONDS);
    }

    private static String replaceSpecialSigns(String str) {
        str = str.replaceFirst(":", "°");
        str = str.replaceFirst(":", "'");
        str = str.replaceFirst(",", "''");
        return str;
    }

    private static int getIndexOfDoubleApostropheInStr(String str) {
        if (str.contains("''")) {
            return str.indexOf("''");
        } else {
            return str.length() + 1;
        }
    }

    private static String subtractStr(String str, int indexOfSignInStr) {
        if (indexOfSignInStr < str.length() && str.contains("''")) {
            return str.substring(0, indexOfSignInStr+2);
        } else {
            return str;
        }
    }

    private static String setGeoDirectionSymbol(String str, Double coordinate, boolean isLatitude) {
        if (isLatitude) {
            return appendSymbolForLatitude(str, coordinate);
        } else {
            return appendSymbolForLongitude(str, coordinate);
        }
    }

    private static String appendSymbolForLatitude(String str, Double coordinate) {
        if (coordinate < 0) {
            return str + "S";
        } else {
            return str + "N";
        }
    }

    private static String appendSymbolForLongitude(String str, Double coordinate) {
        if (coordinate < 0) {
            return str + "W";
        }
        else {
            return str + "E";
        }
    }

    /** Set new value of the distance.
     *  Gives up distance update if displacement is smaller than 5 meters.
     * @param lastLocation last known location
     * @param currLocation current location
     */
    public static double updateDistanceValue(Location lastLocation, Location currLocation, Double distance) {
        if (lastLocation != null && currLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    currLocation.getLatitude(), currLocation.getLongitude(), results);
            if (results[0] > 5) {
                distance += results[0];
            }
        }
        return distance;
    }

    /** Set distance string
     * @param currDistance value of distance to forge into string
     * @return formatted distance string
     * example: 13.23 mi*
     */
    public static String setDistanceStr(Double currDistance) {
        Double distance;
        distance = formatValueOfDistance(currDistance);

        String distanceStr;
        distanceStr = formatDistanceToStr(distance);

        int indexOfPointInStr;
        indexOfPointInStr = getIndexOfPointInDistance(distanceStr);

        distanceStr = subtractDistanceStr(distanceStr, indexOfPointInStr);
        distanceStr = appendUnitSymbol(distanceStr);

        return distanceStr;
    }

    private static Double formatValueOfDistance(Double distance) {
        switch (mUnitsFormat) {
            case "METERS": mUnitsSymbol = "m";
                break;
            case "KILOMETERS": distance /= 1000; mUnitsSymbol = "km";
                break;
            case "MILES": distance /= 1609.344; mUnitsSymbol = "mi";
                break;
            default: mUnitsSymbol = "m";
                break;
        }

        return distance;
    }

    private static String formatDistanceToStr(Double distance) {
        return DECIMAL_FORMAT.format(distance);
    }

    private static int getIndexOfPointInDistance(String str) {
        if (str.contains(".")) {
            return str.indexOf(".");
        } else {
            return str.length();
        }
    }

    private static String subtractDistanceStr(String str, int pointIndex) {
        if (pointIndex <= str.length()-4 && str.contains(".")) {
            return str.substring(0, pointIndex + 3);
        } else {
            return str;
        }
    }

    private static String appendUnitSymbol(String str) {
        return str + " " + mUnitsSymbol;
    }

    /**
     * Set date string
     * @param millis recorded time in milliseconds
     * @return formatted date string
     * example: 2017.02.14 at 21:48:23
     */
    public static String setDateString(long millis) {
        return new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(millis);
    }

    /** Set min/max elevation string
     * @param altitude value to create string from
     * @return string
     * example: 13.23 m n.p.m.
     */
    public static String setMinMaxString(Double altitude) {
        String str = formatElevation(altitude);
        str = appendMetersAboveSeaLevel(str);
        return str;
    }

    private static String formatElevation(Double altitude) {
        return DECIMAL_FORMAT.format(altitude);
    }

    private static String appendMetersAboveSeaLevel(String str) {
        str = str + " m n.p.m.";
        return str;
    }

    /** Set new value of maximum altitude in a session
     * @param currAltitude new value to compare
     * @param currMaxAltitude old value to compare
     * @return maximum altitude value
     */
    public static Double updateMaxAltitudeValue(Double currAltitude, Double currMaxAltitude) {
        if (currAltitude > currMaxAltitude) {
            return currAltitude;
        } else {
            return currMaxAltitude;
        }
    }

    /** Set new value of minimum altitude in a session
     * @param currAltitude new value to compare
     * @param currMinAltitude old min value to compare
     * @return minimum altitude value
     */
    public static Double updateMinAltitudeValue(Double currAltitude, Double currMinAltitude) {
        if (currAltitude < currMinAltitude) {
            return currAltitude;
        } else {
            return currMinAltitude;
        }
    }

    /**
     * Sets Airport API radius string (int radius in miles, longitude, latitude)
     * ATTENTION: latitude and longitude are reversed; first longitude, then latitude
     * @param latitude of current known location
     * @param longitude of current known location
     * @return radius str, with information about current location
     * and radius in miles (1,61 km) to search for airports in proximity
     */
    public static String setRadialDistanceString(Double latitude, Double longitude) {
        String str;
        str = "100;";
        str += longitude.toString();
        str += ",";
        str += latitude.toString();
        return str;
    }

    /**
     * Sets new, bigger radius value to search for airports in proximity. Is called when there are
     * no airports in radius distance, or airports have no data.
     * @param radialDistStr old string query
     * @return radius str, with information about current location
     * and radius in miles to search for airports in proximity
     */
    public static String increaseRadialDistanceString(String radialDistStr) {
        int indexOfSemicolon = getIndexOfSemicolon(radialDistStr);
        int radiusValue = getRadiusValue(radialDistStr, indexOfSemicolon);
        radialDistStr = setNewRadialDistance(radialDistStr, indexOfSemicolon, radiusValue);
        return radialDistStr;
    }

    private static int getIndexOfSemicolon(String str) {
        if (str.contains(";")) {
            return str.indexOf(";");
        } else {
            return 0;
        }
    }

    private static int getRadiusValue(String str, int semicolonIndex) {
        String start = TextUtils.substring(str, 0, semicolonIndex);
        return Integer.valueOf(start);
    }

    private static String setNewRadialDistance(String radialDist, int semicolonIndex, int oldValue) {
        String strRest = TextUtils.substring(radialDist, semicolonIndex, radialDist.length()-1);
        int newRadius = oldValue + 100;
        return String.valueOf(newRadius) + ";" + strRest;
    }

    /**
     * Gets string of symbols of airports in proximity.
     * @param airportsList list of values of airports in proximity
     * @return returns string with id symbols of airports from list
     * e.g. "EPLB EPWA EPMO "
     */
    public static String getAirportsSymbolString(List<XmlAirportValues> airportsList) {
        String symbolStr = "";
        if (!airportsList.isEmpty()) {
            for (XmlAirportValues values : airportsList) {
                symbolStr += values.getId() + " ";
            }
        }
        return symbolStr;
    }


    // TODO: 14.03.2017 complete below description
    /**
     *
     * @param airportsList
     * @param lat
     * @param lon
     * @return
     */
    public static float fetchAirportPressure(List<XmlAirportValues> airportsList, double lat, double lon) {
        setAirportsDistance(airportsList, lat, lon);
        sortAirportsByDistance(airportsList);
        float pressure = getClosestAirportPressure(airportsList);
        return convertHgPressureToHPa(pressure);
    }

    /**
     * Set airports distance values. Distance is measured as a distance between user's position (or any
     * given position) and airport.
     * @param airportsList list of airports to calculate distance between them and current user position
     * @param lat latitude of user's position
     * @param lon longitude of user's position
     */
    public static void setAirportsDistance(List<XmlAirportValues> airportsList, double lat, double lon) {
        for (XmlAirportValues values : airportsList) {
            float [] results = new float[1];
            Location.distanceBetween(values.getLatitude(), values.getLongitude(),
                    lat, lon, results);
            values.setDistance(results[0]);
        }
    }

    /**
     * Sort list of closest airports by distance to user. Values increments from lowest index to highest.
     * @param airportsList list of airports to sort by distance to user
     */
    private static void sortAirportsByDistance(List<XmlAirportValues> airportsList) {
        Collections.sort(airportsList, new Comparator<XmlAirportValues>() {
            @Override
            public int compare(XmlAirportValues airport1, XmlAirportValues airport2) {
                if (airport1.getDistance() > airport2.getDistance()) {
                    return 1;
                }
                if (airport1.getDistance() < airport2.getDistance()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * Get pressure of closest airport from sorted list. Checks if there is pressure value.
     * @param airportsList list of the airports to fetch pressure from.
     * @return returns pressure of closest airport (goes from lowest index to highest if
     * there is no value of pressure), returns 0 if there is no pressure value at all.
     */
    private static float getClosestAirportPressure(List<XmlAirportValues> airportsList) {
        double pressure = 0;
        for (XmlAirportValues values : airportsList) {
            if (values.getPressureInHg() != 0) {
                pressure = values.getPressureInHg();
                break;
            }
        }
        return (float) pressure;
    }
    /**
     * Converts inch of mercury [inHg] pressure to hectopascal [hPa] value (used in Android)
     * @param hgPressure at the station data is coming from;
     * @return pressure value converted to hectopascals
     */
    private static float convertHgPressureToHPa(float hgPressure) {
        return (float) (hgPressure*Constants.MULTIPLIER_HPA);
    }

    /**
     * Round given numeric value
     * @param value numeric value to round
     * @return rounded value
     * e.g. 160
     */
    public static Double roundValue(Double value) {
        return (double) Math.round(value);
    }

    /**
     *
     * @param messageContent
     * @return
     */
    public static String buildMessage(String[] messageContent) {
        String message = "";
        // add address
        if (isInitiated(messageContent[0])) {
            message = "I'm in " + messageContent[0];
        }
        // add elevation level
        if (isInitiated(messageContent[1])) {
            if (isNotZero(messageContent[1])) {
                message += "\n at " + messageContent[1] + " m.n.p.m.";
            }
        }
        // add distance travelled
        String distance = messageContent[2];
        distance = distance.replaceAll("[^\\d.]", "");
        if (isInitiated(messageContent[2])) {
            if (isNotZero(distance)) {
                message += " after travelling distance of " + messageContent[2] + ".";
            }
        }
        // add app name
        message  += "\nShared with AltiMeter app.";

        return message;
    }

    private static boolean isInitiated(String str) {
        return !str.equals("...");
    }

    private static boolean isNotZero(String str) {
        return Float.valueOf(str) != 0;
    }
}

