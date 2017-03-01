package pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask;

import android.net.Uri;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
import pl.grzegorziwanek.altimeter.app.utils.Constants;
import rx.Observable;

/**
 * Created by Grzegorz Iwanek on 01.03.2017.
 */

public class AirportsTaskRx extends BasicAirportsTask {

    private String mRadialDistanceStr;

    public Observable<List<XmlAirportValues>> getNearestAirportsObservable() {
        return super.getNearestAirportsObservable(parseNearestAirportUri(), "GET_STATIONS");
    }
    private Uri parseNearestAirportUri() {
        return Uri.parse(Constants.AVIATION_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.AVIATION_DATA_SOURCE, "stations")
                .appendQueryParameter(Constants.AVIATION_REQUEST_TYPE, "retrieve")
                .appendQueryParameter(Constants.AVIATION_FORMAT, "xml")
                .appendQueryParameter(Constants.AVIATION_RADIAL_DISTANCE, mRadialDistanceStr)
                .build();
    }

    public void setRadialDistanceStr(String radialDistanceStr) {
        mRadialDistanceStr = radialDistanceStr;
    }
}
