package pl.grzegorziwanek.altimeter.app.data.database.source.local;

import android.provider.BaseColumns;

/**
 * Created by Grzegorz Iwanek on 27.01.2017.
 */

final class SessionDbContract {

    private SessionDbContract() {}

    /**
     * Sessions table entries. Holds general data about session, as well unique ID.
     */
    static abstract class SessionEntry implements BaseColumns {
        static final String TABLE_NAME = "records";
        static final String COLUMN_NAME_ENTRY_ID = "entryid";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DESCRIPTION = "description";
        static final String COLUMN_NAME_CURRENT_ALTITUDE = "currentaltitude";
        static final String COLUMN_NAME_MAX_HEIGHT = "maxheight";
        static final String COLUMN_NAME_MIN_HEIGHT = "minheight";
        static final String COLUMN_NAME_CURRENT_DISTANCE = "currentdistance";
        static final String COLUMN_NAME_CURRENT_ADDRESS = "currentaddress";
    }

    /**
     * Session's records entries. Detailed data about each location.
     * Name of the every table is set as equal to the session's unique ID.
     */
    static abstract class RecordsEntry implements BaseColumns {
        static final String COLUMN_NAME_ENTRY_ID = "entryid";
        static final String COLUMN_NAME_LATITUDE = "latitude";
        static final String COLUMN_NAME_LONGITUDE = "longitude";
        static final String COLUMN_NAME_ALTITUDE = "altitude";
        static final String COLUMN_NAME_ADDRESS = "address";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_DISTANCE = "distance";
    }
}
