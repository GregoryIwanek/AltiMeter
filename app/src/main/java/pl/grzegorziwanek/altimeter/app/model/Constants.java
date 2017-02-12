package pl.grzegorziwanek.altimeter.app.model;

/**
 * Created by Grzegorz Iwanek on 30.11.2016.
 * Consist constants keys used in called AddressIntentServicee and to retrieve result's back
 */
public final class Constants {
    //service constants (used in fetch address service by current location)
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    private static final String PACKAGE_NAME = "pl.grzegorziwanek.altimeter.app.model.location.LocationCollector";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA = PACKAGE_NAME + ".LOCATION_DATA";

    //JSON elevation google service
    public static final int URL_LENGTH_LIMIT = 8192;
    public static final String GOOGLEMAPS_BASE_URL = "https://maps.googleapis.com/maps/api/elevation/json?";
    public static final String OUTPUT_FORMAT = "json";
    public static final String PARAMETERS_LOCATIONS = "locations";
    public static final String PARAMETERS_PATH = "path";
    public static final String APPID_PARAM = "key";

    //Shared preferences default
    public static final int ALTITUDE_MIN = 20000;
    public static final int ALTITUDE_MAX = -20000;
    public static final int DISTANCE_DEFAULT = 0;
    public static final String DEFAULT_TEXT = "...";

    public static String sessionId = "";

    public static final String POSITIVE = "OK";
    public static final String CANCEL = "CANCEL";
}



