package pl.grzegorziwanek.altimeter.app.data.location.services.elevation;

import android.location.Location;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.grzegorziwanek.altimeter.app.utils.Constants;
import rx.Observable;
import rx.functions.Func0;

/**
 * Consist JavaRx task. Retrieves elevation value of the given location.
 * Requires location object as an input to work.
 */
public class NetworkTaskRx {

    private String mLocationsStr;

    // private constructor to prevent instantiation without location object input
    private NetworkTaskRx() {}

    public NetworkTaskRx(Location location) {
        setLocationsStr(location);
    }

    private void setLocationsStr(Location record) {
        mLocationsStr = Double.toString(record.getLatitude()) + "," + Double.toString(record.getLongitude());
    }

    public Observable<Double> getElevationObservable() {
        return Observable.defer(new Func0<Observable<Double>>() {
            @Override
            public Observable<Double> call() {
                try {
                    return Observable.just(getElevation());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    private double getElevation() throws JSONException {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String altitudeJsonStr = null;

        try {
            // construction of the URL query for google maps API
            Uri buildUri = parseGoogleUri();
            URL url = new URL(buildUri.toString());

            // construction of request to google maps API, opening connection with web
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read input stream from web by getting stream from opened connection
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                //set as null if no data to show
                return 0;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                // append new line to builder
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() == 0) {
                // string stream was empty, end with null
                return 0;
            }

            // define altitude string
            altitudeJsonStr = stringBuilder.toString();
        } catch (IOException e) {
            return 0;
        } finally {
            // close opened url connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            getAltitudeDataFromJson(altitudeJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAltitudeDataFromJson(altitudeJsonStr);
    }

    private Uri parseGoogleUri() {
        return Uri.parse(Constants.GOOGLEMAPS_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.PARAMETERS_LOCATIONS, mLocationsStr)
                .build();
    }

    private double getAltitudeDataFromJson(String altitudeJsonStr) throws JSONException {
        final String OMW_RESULTS = "results";
        final String OMW_ELEVATION = "elevation";

        JSONObject altitudeJson = new JSONObject(altitudeJsonStr);
        JSONArray altitudeJsonArray = altitudeJson.getJSONArray(OMW_RESULTS);
        JSONObject pointData = altitudeJsonArray.getJSONObject(0);

        return pointData.getDouble(OMW_ELEVATION);
    }
}
