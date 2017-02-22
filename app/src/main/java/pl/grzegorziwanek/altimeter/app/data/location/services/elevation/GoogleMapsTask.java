package pl.grzegorziwanek.altimeter.app.data.location.services.elevation;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import pl.grzegorziwanek.altimeter.app.data.Constants;
import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;

/**
 *Inner class responsible for background update, have to extend AsyncTask<params, progress, result>
 *ASyncTask <params, progress, result> -> params: given entry data to work on; progress: data to show progress; result: result of background execution
 */

public class GoogleMapsTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = GoogleMapsTask.class.getSimpleName();
    private LocationResponse.NetworkElevationCallback mCallback;
    private String mLocationsStr;
    private Double mCurrentEleValue;

    public GoogleMapsTask(LocationResponse.NetworkElevationCallback callback) {
        mCallback = callback;
    }

    public void setLocationsStr(Location record) {
        mLocationsStr = null;
        mLocationsStr = Double.toString(record.getLatitude()) + "," + Double.toString(record.getLongitude());
    }

    @Override
    protected Void doInBackground(Void... Void) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String altitudeJsonStr = null;

        try {
            // construction of the URL query for google maps API
            Uri buildUri = parseGoogleUri();
            System.out.println(buildUri);
            URL url = new URL(buildUri.toString());

            // construction of request to google maps API, opening connection with web
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read input stream from web by getting stream from opened connection
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                //set as null if no data to show
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                // append new line to builder
                stringBuilder.append(line + "\n");
            }

            if (stringBuilder.length() == 0) {
                // string stream was empty, end with null
                return null;
            }

            // define altitude string
            altitudeJsonStr = stringBuilder.toString();
        } catch (IOException e) {
            return null;
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
            getAltitudeDataFromJson(altitudeJsonStr, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Uri parseGoogleUri() {
        return Uri.parse(Constants.GOOGLEMAPS_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.PARAMETERS_LOCATIONS, mLocationsStr)
                .build();
    }

    //method to extract data in correct form from given Json String;
    // TODO-> methods to define number of points, right now fixed dummy
    public String[] getAltitudeDataFromJson(String altitudeJsonStr, int numOfPoints) throws JSONException {
        //JSON objects names which need to be extracted from given string
        final String OMW_RESULTS = "results";
        final String OMW_ELEVATION = "elevation";
        final String OMW_LOCATION = "location";
        final String OMW_LATITUDE = "lat";
        final String OMW_LONGITUDE = "lng";
        final String OMW_RESOLUTION = "resolution";
        final String OMW_STATUS = "status";

        //create Json object and array with data, assign given Json string parameter to object
        JSONObject altitudeJson = new JSONObject(altitudeJsonStr);
        JSONArray altitudeJsonArray = altitudeJson.getJSONArray(OMW_RESULTS);
        String[] resultArray = new String[numOfPoints];

        //hatch data from Json array into result String array
        for (int i=0; i<altitudeJsonArray.length(); i++) {
            //get JSONObject representing the single point on a map
            JSONObject pointData = altitudeJsonArray.getJSONObject(i);

            //there is only one main array (RESULTS) and all of points are inside that array;
            //each cell consist elevation, location which is subarray and consist lan and lng, and resolution

            //elevation extraction
            Double pointDataElevation = pointData.getDouble(OMW_ELEVATION);
            mCurrentEleValue = pointDataElevation;

            //location extraction and assignation lat and lng
            JSONObject pointDataLocation = pointData.getJSONObject(OMW_LOCATION);
            Double pointDataLatitude = pointDataLocation.getDouble(OMW_LATITUDE);
            Double pointDataLongitude = pointDataLocation.getDouble(OMW_LONGITUDE);

            //resolution extraction
            Double pointDataResolution = pointData.getDouble(OMW_RESOLUTION);

            resultArray[i] = "Elevation: " + pointDataElevation.toString() + ", " + pointDataLatitude.toString() + ", " + pointDataLongitude.toString();

            //TODO move from here to separated method/class
            DecimalFormat df = new DecimalFormat("#.###");
            String currentElevation = df.format(pointDataElevation);
        }

        for (String pointEntry : resultArray) {
            Log.v(LOG_TAG, "Point entry created: " + pointEntry);
        }

        return resultArray;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mCurrentEleValue != null) {
            //round elevation value (to set precision to meters)
            mCurrentEleValue = (double) Math.round(mCurrentEleValue);

            mCallback.onNetworkElevationFound(mCurrentEleValue);
        } else {
            Log.v(LOG_TAG, " onPostExecute, current elevation wasn't fetched from JSON, stopped");
        }
    }
}
