package pl.gregoryiwanek.altimeter.app.utils;

/**
 * Created by Grzegorz Iwanek on 30.11.2016.
 * Consist constants keys and values used in services, managers and to calculations.
 */
public final class Constants {
    // service constants (used in fetch address service by current location)
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    private static final String PACKAGE_NAME = "pl.grzegorziwanek.altimeter.app.model.location.LocationUpdateManager";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA = PACKAGE_NAME + ".LOCATION_DATA";

    // JSON Google Map Elevation API service
    public static final int NETWORK_INTERVAL_VALUE = 60000;
    public static final int NETWORK_FASTEST_INTERVAL_VALUE = 30000;
    public static final int URL_LENGTH_LIMIT = 8192;
    public static final String GOOGLEMAPS_BASE_URL = "https://maps.googleapis.com/maps/api/elevation/json?";
    public static final String OUTPUT_FORMAT = "json";
    public static final String PARAMETERS_LOCATIONS = "locations";
    public static final String PARAMETERS_PATH = "path";
    public static final String APPID_ELEVATION_PARAM = "key";

    // http://samples.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=b1b15e88fa797225412429c1c50c122a1
    // Open Weather Map API parameters
    public static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String LAT_PARAM = "lat";
    public static final String LON_PARAM = "lon";
    public static final String QUERY_PARAM = "q";
    public static final String FORMAT_PARAM = "mode";
    public static final String UNITS_PARAM = "units";
    public static final String DAYS_PARAM = "cnt";
    public static final String APPID_WEATHER_PARAM = "APPID";

    // Aviation Weather API (nearest airports in proximity, relative pressure in the airports)
    public static final String AVIATION_BASE_URL = "http://aviationweather.gov/adds/dataserver_current/httpparam?";
    public static final String AVIATION_DATA_SOURCE = "dataSource";
    public static final String AVIATION_REQUEST_TYPE = "requestType";
    public static final String AVIATION_FORMAT = "format";
    public static final String AVIATION_RADIAL_DISTANCE = "radialDistance";
    public static final String AVIATION_HOURS_PERIOD = "hoursBeforeNow";
    public static final String AVIATION_STATION = "stationString";
    public static final String AVIATION_MOST_RECENT_FOR_EACH = "mostRecentForEachStation";

    public static final String DEFAULT_TEXT = "...";
    public static final int MAX_NUMBER_SESSIONS = 4;

    public static String sessionId = "";

    // Notice Dialog choice parameters
    static final String POSITIVE = "OK";
    static final String CANCEL = "CANCEL";

    // Gps parameter
    public static final int GPS_INTERVAL_VALUE = 20000;
    public static final int GPS_FASTEST_INTERVAL_VALUE = 10000;
    public static final int ONE_HOUR = 3600000;
    public static final int HALF_HOUR = 1800000;
    public static final int TWENTY_SECONDS = 20000;

    // hectopascals <-> mercurial pressure multiplier
    static final double MULTIPLIER_HPA = 33.8638;
}