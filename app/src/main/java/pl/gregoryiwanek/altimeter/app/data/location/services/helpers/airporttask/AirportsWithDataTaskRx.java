package pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask;

import android.net.Uri;

import java.util.List;

import pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
import pl.gregoryiwanek.altimeter.app.utils.Constants;
import rx.Observable;

/**Consists JavaRx airports task.
 * Case of task when information about surrounding airports location, and their name codes are known.
 * Used to download METARS weather data of given airports.
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
