package pl.gregoryiwanek.altimeter.app.utils.formatconventer;

import android.location.Location;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
import pl.gregoryiwanek.altimeter.app.utils.Constants;

/**
 * Consists static methods used to format and convert input values to required shape.
 * Class itself doesn't save or store any variables, except units format (which can be
 * changed by user's preferences);
 * Methods follow scheme:
 * input values -> format/convert/recalculate input values -> return new correct value;
 */
public class FormatAndValueConverter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

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
        if (str.contains(":")) {
            str = str.replaceFirst(":", "°");
        }
        if (str.contains(":")) {
            str = str.replaceFirst(":", "'");
        }
        if (str.contains(",")) {
            str = str.replaceFirst(",", "''");
        }
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

    /**
     * Sets new value of the distance.
     * Drops distance update if displacement is smaller than 5 meters (value won't be updated);
     * @param lastLocation last known location;
     * @param currLocation current location;
     * @param distance value of the current;
     * @return returns updated, numeric distance value;
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
     * Set date string in "yyyy.MM.dd 'at' HH:mm:ss" format.
     * @param millis recorded time in milliseconds;
     * @return formatted date string
     * example: 2017.02.14 at 21:48:23
     */
    public static String setDateString(long millis) {
        return new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(millis);
    }

    /**
     * Converts input time in milliseconds into string in date "HH:mm:ss" format.
     * @param millis time value in milliseconds to convert to date "HH:mm:ss" format;
     * @return returns string in "HH:mm:ss" format;
     */
    public static String setHoursDateString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(millis);
    }

    /**
     * Converts string in date format (yyyy-MM-dd HH:mm:ss or HH:mm:ss) into milliseconds format.
     * Input string has to be in correct "yyyy-MM-dd HH:mm:ss or HH:mm:ss" format to work;
     * @param time input time string in "yyyy-MM-dd HH:mm:ss or HH:mm:ss" format;
     * @return returns string with time in milliseconds
     */
    public static String getTimeMillisFromStr(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        time = checkIfCorrectDateFormat(time);
        String result = "0";
        try {
            Date date = sdf.parse("1970-01-01 " + time);
            result = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String checkIfCorrectDateFormat(String timeStr) {
        switch (timeStr) {
            case "0":
                timeStr = "00:00:00";
                break;
            case "00:00:00.000":
                timeStr = "00:00:00";
                break;
            case "00:00:00.":
                timeStr = "00:00:00";
                break;
        }
        return timeStr;
    }

    /** Set min/max elevation string
     * @param altitude value to create string from
     * @return string
     * example: altitude=13, return 13 m n.p.m.
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

    /**
     * Fetches pressure value of the closest airport station to the user.
     * @param airportsList list of {@link XmlAirportValues} objects
     * @param lat current latitude value of device's location
     * @param lon current longitude value of device's location
     * @return returns numeric pressure value of the closest {@link XmlAirportValues} object to the
     * device's location;
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
    private static void setAirportsDistance(List<XmlAirportValues> airportsList, double lat, double lon) {
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
        return (float) (hgPressure* Constants.MULTIPLIER_HPA);
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
     * Rounds input numeric value to half.
     * @param value numeric value to round to half
     * @return rounded to half numeric value e.g. 10.5, 11.0, 0.5;
     */
    public static Double roundValueToHalf(Double value) {
        return (double) Math.round(value*2)/2;
    }

    /**
     * Builds share message. Depending on input content (full or partial), it will be returned
     * full share message, partial share message or basic default message.
     * @param messageContent array of string values with message content [0-address, 1-elevation level,
     *                       2-distance travelled];
     * @return returns full or partial string message;
     */
    public static String buildMessage(String[] messageContent) {
        if (messageContent != null) {
            return buildFullMessage(messageContent);
        } else {
            return buildDefaultMessage();
        }
    }

    private static String buildFullMessage(String[] messageContent) {
        if (messageContent != null) {
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
        return "";
    }

    private static String buildDefaultMessage() {
        return "Shared with AltiMeter app.";
    }

    private static boolean isInitiated(String str) {
        return !str.equals("...");
    }

    private static boolean isNotZero(String str) {
        return Float.valueOf(str) != 0;
    }

    /**
     * Formats given string, if required, to char value zero.
     * Input string can be in any format, and method looks if it equals default text value "...";
     * @param str string to check if is equal to default text ("...");
     * @return string in a date format "00:00:00"
     */
    public static String formatToZeroIfDefaultText(String str) {
        if (str.equals("...")) {
            str = "0";
        }
        return str;
    }

    /**
     * Formats given string, if required, to correct date format HH:mm:SS;
     * Input string is expected to be in format HH:mm:SS or equal to default text value "...";
     * @param str string to check if is equal to default text ("...")
     * @return string in a date format "00:00:00"
     */
    public static String formatToZeroDateIfDefaultText(String str) {
        if (str.equals("...")) {
            str = "00:00:00";
        }
        return str;
    }
}

