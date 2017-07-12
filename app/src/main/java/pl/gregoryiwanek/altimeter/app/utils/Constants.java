package pl.gregoryiwanek.altimeter.app.utils;

import android.content.Context;

import pl.gregoryiwanek.altimeter.app.R;

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

    // Database expressions
    public static final String DATABASE_NAME = "Graphs.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String REAL_TYPE = " REAL";
    public static final String BOOLEAN_TYPE = " INTEGER";
    public static final String UNIQUE = " UNIQUE";
    public static final String COMMA_SEP = ",";

    public static String sessionId = "";

    // Notice Dialog choice parameters
    public static final String POSITIVE = "OK";
    public static final String CANCEL = "CANCEL";

    // Gps parameter
    public static final int GPS_INTERVAL_VALUE = 20000;
    public static final int GPS_FASTEST_INTERVAL_VALUE = 10000;
    public static final int ONE_HOUR = 3600000;
    public static final int HALF_HOUR = 1800000;
    public static final int TWENTY_SECONDS = 20000;

    // hectopascals <-> mercurial pressure multiplier
    public static final double MULTIPLIER_HPA = 33.8638;

    // version
    static final String FREE_VERSION = "free";
    static final String PRO_VERSION = "pro";
    public static final String MESSAGE_UPGRADE_TO_PRO_EXPORT = "Upgrade to AltiMeterPRO to export sessions data.\nDo you want to upgrade?";
    public static final String MESSAGE_UPGRADE_TO_PRO_MAX_SAVED = "Upgrade to AltiMeterPro to save more than 5 sessions.\nDo you want to upgrade?";

    // popup messages
    public static final String MESSAGE_GENERATE_MAP = "Generate a map?";
    public static final String MESSAGE_SEND_TO = "Send to";
    public static final String MESSAGE_SAVE_SESSION = "Save session to database?";
    public static final String MESSAGE_RESET_SESSION = "Reset session. Are you sure?";
    public static final String MESSAGE_DELETE_ALL = "Delete all?";
    public static final String MESSAGE_DELETE_CHECKED = "Delete checked?";

    // popup message codes
    public static final int CODE_GENERATE_MAP = 0;
    public static final int CODE_SEND_TO = 1;
    public static final int CODE_SAVE_SESSION = 2;
    public static final int CODE_RESET_SESSION = 3;
    public static final int CODE_DELETE_ALL = 4;
    public static final int CODE_DELETE_CHECKED = 5;
    public static final int CODE_UPGRADE_EXPORT = 6;
    public static final int CODE_UPGRADE_MAX_SAVED = 6;

    // toast messages
    public static final String TOAST_EMPTY_MAP = "Session has no recorded points. Record points in order to generate map";
    public static final String TOAST_SESSION_SAVED = "Session data saved";
    public static final String TOAST_SESSION_PAUSED = "Paused";
    public static final String TOAST_SESSION_RECORDING = "Recording data";
    public static final String TOAST_TURN_ON_SOURCE = "Turn on at least one data source";
    public static final String TOAST_MUST_STOP_SESSION = "You must stop session first.";
    public static String CREATE_TABLE = "CREATE TABLE ";
    public static String ON_UPGRADE = "";

    public enum TEXT {
        MESSAGE_GENERATE_MAP {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_generate_map);
            }
        },
        MESSAGE_SEND_TO {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_send_to);
            }
        },
        MESSAGE_SAVE_SESSION {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_save_session);
            }
        },
        MESSAGE_RESET_SESSION {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_reset_session);
            }
        },
        MESSAGE_DELETE_ALL {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_delete_all);
            }
        },
        MESSAGE_DELETE_CHECKED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_delete_checked);
            }
        },
        MESSAGE_SAVING_PLEASE_WAIT {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_saving_please_wait);
            }
        },
        TOAST_EMPTY_MAP {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_empty_map);
            }
        },
        TOAST_SESSION_SAVED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_session_saved);
            }
        },
        TOAST_SESSION_PAUSED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_session_paused);
            }
        },
        TOAST_SESSION_RECORDING {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_session_recording);
            }
        },
        TOAST_TURN_ON_SOURCE {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_turn_on_source);
            }
        },
        TOAST_MUST_STOP_SESSION {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_must_stop_session);
            }
        },
        TOAST_CHECKED_DELETED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_checked_deleted);
            }
        },
        TOAST_ALL_DELETED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_all_deleted);
            }
        },
        TOAST_CHANGES_SAVED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.toast_changes_saved);
            }
        },
        MESSAGE_UPGRADE_TO_PRO_EXPORT {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.upgrade_to_pro_export);
            }
        },
        MESSAGE_UPGRADE_TO_PRO_MAX_SAVED {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.upgrade_to_pro_max_saved);
            }
        },
        MESSAGE_RESET_STATISTICS {
            @Override
            public String getValue(Context context) {
                return context.getResources().getString(R.string.message_reset_statistics);
            }
        };

        public abstract String getValue(Context context);
    }
}