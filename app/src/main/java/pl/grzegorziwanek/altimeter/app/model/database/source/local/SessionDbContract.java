package pl.grzegorziwanek.altimeter.app.model.database.source.local;

import android.provider.BaseColumns;

/**
 * Created by Grzegorz Iwanek on 27.01.2017.
 */

public final class SessionDbContract {

    //private constructor, prevents from instantiation of that contract class
    private SessionDbContract() {}

    public static abstract class SessionEntry implements BaseColumns {

        public static final String TABLE_NAME = "records";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ALTITUDE = "altitude";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_RADIUS = "radius";
     }
}
