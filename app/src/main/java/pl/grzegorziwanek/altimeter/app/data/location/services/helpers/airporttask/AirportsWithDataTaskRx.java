package pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask;

import android.net.Uri;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
import pl.grzegorziwanek.altimeter.app.utils.Constants;
import rx.Observable;

/**
 * Created by Grzegorz Iwanek on 01.03.2017.
 */

public class AirportsWithDataTaskRx extends BasicAirportsTask {

    private String mStationsStr;

    public Observable<List<XmlAirportValues>> getAirportsWithDataObservable() {
        return super.getNearestAirportsObservable(parseAirportPressureUri(), "GET_METAR");
    }

    private Uri parseAirportPressureUri() {
        return Uri.parse(Constants.AVIATION_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.AVIATION_DATA_SOURCE, "metars")
                .appendQueryParameter(Constants.AVIATION_REQUEST_TYPE, "retrieve")
                .appendQueryParameter(Constants.AVIATION_FORMAT, "xml")
                .appendQueryParameter(Constants.AVIATION_STATION, mStationsStr)
                .appendQueryParameter(Constants.AVIATION_HOURS_PERIOD, "2")
                .appendQueryParameter(Constants.AVIATION_MOST_RECENT_FOR_EACH, "true")
                .build();
    }

    public void setStationsString(String stationsStr) {
        mStationsStr = stationsStr;
    }
}
