package pl.grzegorziwanek.altimeter.app;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * Created by Grzegorz Iwanek on 27.11.2016.
 * Consist upgraded ArrayAdapter<String> class; used to update all wanted text views in view at once (single ArrayAdapter by default upgrade one object);
 * Responsible for comparing value of current data and if corresponding TextViews need to be changed;
 */
public class DataFormatAndValueConverter {

    public DataFormatAndValueConverter(){}

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static String sUnitsFormat;

    public static void setsUnitsFormat(String sUnitsFormat) {
        DataFormatAndValueConverter.sUnitsFormat = sUnitsFormat;
    }

    //consist code responsible for replacing format (23:32:32:3223132 -> 23°xx'xx")
    public String replaceDelimitersAddDirection(Double coordinate, boolean isLatitude) {

        //replace ":" with symbols
        String str = Location.convert(coordinate, Location.FORMAT_SECONDS);
        str = str.replaceFirst(":", "°");
        str = str.replaceFirst(":", "'");
        str = str.replaceFirst(",", "''");

        //get index of point, define end index of the given string and subtract it ONLY if it has "."
        int pointIndex;
        if (str.contains("''")) {
            pointIndex = str.indexOf("''");
        } else {
            pointIndex = str.length() + 1;
        }

        //subtract string if is longer than end index
        if (pointIndex < str.length() && str.contains("''")) {
            str = str.substring(0, pointIndex+2);
        }

        //add "''" at the end
        //str = str + "\"";

        //Define direction symbol
        //if coordinate is latitude -> add S/N
        if (isLatitude == true) {
            if (coordinate < 0) {
                str = str + "S";
            } else {
                str = str + "N";
            }
        } else {
            //if coordinate is longitude -> add E/W
            if (coordinate < 0) {
                str = str + "W";
            }
            else {
                str = str + "E";
            }
        }

        return str;
    }

    //format given altitude to X.XX
    public String formatElevation(Double altitude) {
        return DECIMAL_FORMAT.format(altitude);
    }

    public String addMetersAboveSeaLevel(String str) {
        str = str + " m n.p.m.";
        return str;
    }

    public Double updateMaxAltitude(Double altitude, Double currMax) {
        if (altitude > currMax) {
            return altitude;
        } else {
            return currMax;
        }
    }

    public Double updateMinAltitude(Double altitude, Double currMin) {
        if (altitude < currMin) {
            return altitude;
        } else {
            return currMin;
        }
    }

    public String updateCurrMinMaxString(Double currMinMaxAltitude) {
        String currMinMaxStr = formatElevation(currMinMaxAltitude);
        currMinMaxStr = addMetersAboveSeaLevel(currMinMaxStr);
        return currMinMaxStr;
    }

    public String formatDistance(Double currDistance) {
        Double distance = currDistance;
        String unit;

        //format value and unit by chosen format
        switch (sUnitsFormat) {
            case "METERS": unit = "m";
                break;
            case "KILOMETERS": distance /= 1000; unit = "km";
                break;
            case "MILES": distance /= 1609.344; unit = "mi";
                break;
            default: unit = "m";
                break;
        }

        //format output of distance to correct decimal
        String distanceStr = DECIMAL_FORMAT.format(distance);

        //locate point "." in a distance number
        int pointIndex;
        if (distanceStr.contains(".")) {
            pointIndex = distanceStr.indexOf(".");
        } else {
            pointIndex = distanceStr.length();
        }

        //subtract string 3 places after point
        if (pointIndex <= distanceStr.length()-4 && distanceStr.contains(".")) {
            distanceStr = distanceStr.substring(0, pointIndex + 3);
        }

        //set distance string
        distanceStr = distanceStr + " " + unit;

        System.out.println("DISTANCE STRING " +distanceStr);
        return distanceStr;
    }
}
