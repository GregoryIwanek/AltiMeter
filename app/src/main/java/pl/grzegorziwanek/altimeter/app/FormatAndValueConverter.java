package pl.grzegorziwanek.altimeter.app;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * Created by Grzegorz Iwanek on 27.11.2016.
 * Consist upgraded ArrayAdapter<String> class; used to update all wanted text views in view at once (single ArrayAdapter by default upgrade one object);
 * Responsible for comparing value of current data and if corresponding TextViews need to be changed;
 */
public class FormatAndValueConverter {

    public FormatAndValueConverter(){}

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private String mUnitsFormat;
    private String mUnitsSymbol;

    public void setUnitsFormat(String unitsFormat) {
        mUnitsFormat = unitsFormat;
    }

    /** Set geographical coordinates string
     * example: 51°23'13''N*/
    public String setGeoCoordinateStr(Double coordinate, boolean isLatitude) {
        String str;
        str = convertCoordinateToStr(coordinate);
        str = replaceSpecialSigns(str);

        int indexOfDoubleApostropheInStr;
        indexOfDoubleApostropheInStr = getIndexOfDoubleApostropheInStr(str);
        str = subtractStr(str, indexOfDoubleApostropheInStr);
        str = setGeoDirectionSymbol(str, coordinate, isLatitude);

        return str;
    }

    private String convertCoordinateToStr(Double coordinate) {
        return Location.convert(coordinate, Location.FORMAT_SECONDS);
    }

    private String replaceSpecialSigns(String str) {
        str = str.replaceFirst(":", "°");
        str = str.replaceFirst(":", "'");
        str = str.replaceFirst(",", "''");
        return str;
    }

    private int getIndexOfDoubleApostropheInStr(String str) {
        if (str.contains("''")) {
            return str.indexOf("''");
        } else {
            return str.length() + 1;
        }
    }

    private String subtractStr(String str, int indexOfSignInStr) {
        if (indexOfSignInStr < str.length() && str.contains("''")) {
            return str.substring(0, indexOfSignInStr+2);
        } else {
            return str;
        }
    }

    private String setGeoDirectionSymbol(String str, Double coordinate, boolean isLatitude) {
        if (isLatitude) {
            return appendSymbolForLatitude(str, coordinate);
        } else {
            return appendSymbolForLongitude(str, coordinate);
        }
    }

    private String appendSymbolForLatitude(String str, Double coordinate) {
        if (coordinate < 0) {
            return str + "S";
        } else {
            return str + "N";
        }
    }

    private String appendSymbolForLongitude(String str, Double coordinate) {
        if (coordinate < 0) {
            return str + "W";
        }
        else {
            return str + "E";
        }
    }

    /** Set distance string
     *  example: 13.23 mi*/
    public String setDistanceStr(Double currDistance) {
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

    private Double formatValueOfDistance(Double distance) {
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

    private String formatDistanceToStr(Double distance) {
        return DECIMAL_FORMAT.format(distance);
    }

    private int getIndexOfPointInDistance(String str) {
        if (str.contains(".")) {
            return str.indexOf(".");
        } else {
            return str.length();
        }
    }

    private String subtractDistanceStr(String str, int pointIndex) {
        if (pointIndex <= str.length()-4 && str.contains(".")) {
            return str.substring(0, pointIndex + 3);
        } else {
            return str;
        }
    }

    private String appendUnitSymbol(String str) {
        return str + " " + mUnitsSymbol;
    }

    /** Set minimum elevation and maximum elevation string
     *  example: 13.23 m n.p.m.*/
    public String setMinMaxString(Double altitude) {
        String str = formatElevation(altitude);
        str = appendMetersAboveSeaLevel(str);
        return str;
    }

    public String formatElevation(Double altitude) {
        return DECIMAL_FORMAT.format(altitude);
    }

    public String appendMetersAboveSeaLevel(String str) {
        str = str + " m n.p.m.";
        return str;
    }

    public Double updateMaxAltitudeValue(Double altitude, Double currMaxAltitude) {
        if (altitude > currMaxAltitude) {
            return altitude;
        } else {
            return currMaxAltitude;
        }
    }

    public Double updateMinAltitudeValue(Double altitude, Double currMinAltitude) {
        if (altitude < currMinAltitude) {
            return altitude;
        } else {
            return currMinAltitude;
        }
    }
}
