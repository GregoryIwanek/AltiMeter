package pl.grzegorziwanek.altimeter.app.model.location.services;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * Created by Grzegorz Iwanek on 27.11.2016.
 * Consist upgraded ArrayAdapter<String> class; used to update all wanted text views in view at once (single ArrayAdapter by default upgrade one object);
 * Responsible for comparing value of current data and if corresponding TextViews need to be changed;
 */
public class FormatAndValueConverter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    //TODO -> kick out mUnitsFormat and Symbol and move it to Presenter(?), LocationCollector(?)
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
     * @return
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
     * @return distanceStr
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

        System.out.println("DISTANCE STRING " +distanceStr);
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

    /** Set min/max elevation string
     * @param altitude value to create string from
     * @return str
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
     * @return
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
     * @return
     */
    public static Double updateMinAltitudeValue(Double currAltitude, Double currMinAltitude) {
        if (currAltitude < currMinAltitude) {
            return currAltitude;
        } else {
            return currMinAltitude;
        }
    }
}
