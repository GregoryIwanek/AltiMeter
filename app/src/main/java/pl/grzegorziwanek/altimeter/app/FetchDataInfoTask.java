package pl.grzegorziwanek.altimeter.app;

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
import java.util.ArrayList;

/**
 *Inner class responsible for background update, have to extend AsyncTask<params, progress, result>
 *ASyncTask <params, progress, result> -> params: given entry data to work on; progress: data to show progress; result: result of background execution
 */

public class FetchDataInfoTask extends AsyncTask<Void, Void, Void>
{
    private final String LOG_TAG = FetchDataInfoTask.class.getSimpleName();
    final String APPID_KEY = "AIzaSyDz8OSO03MnSdoE-0FFN9sZaIyFRlpf79Y"; // TODO move that to config
    private String locationsStr;
    private AsyncResponse asyncResponse;
    private Double mCurrentEleValue;

    public FetchDataInfoTask(AsyncResponse asyncResponse)
    {
        this.asyncResponse = asyncResponse;
    }

    public void setLocationsStr(Location record)
    {
        locationsStr = null;
//        for (Location record : locationList)
//        {
//            locationsStr = Double.toString(record.getLongitude()) + "," + Double.toString(record.getLatitude());
//        }
        locationsStr = Double.toString(record.getLatitude()) + "," + Double.toString(record.getLongitude());
    }

    //download data from web as a background task
    @Override
    protected Void doInBackground(Void... Void)
    {
        //help class to connect to web and get data
        HttpURLConnection urlConnection = null;
        //reads data from given input stream (this case-> data from web to string format)
        BufferedReader bufferedReader = null;
        String altitudeJsonStr = null;

        try
        {
            //setp 1: construction of the URL query for google maps API, have to add personal API key to use gooogle maps API
            //TODO move it to a different subclass or abstract class
            //google maps API takes form: https://maps.googleapis.com/maps/api/elevation/outputFormat?parameters
            //example: https://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536,-104.9847034&key=AIzaSyDz8OSO03MnSdoE-0FFN9sZaIyFRlpf79Y
            final int URL_LENGTH_LIMIT = 8192;
            final String GOOGLEMAPS_BASE_URL = "https://maps.googleapis.com/maps/api/elevation/json?";
            final String OUTPUT_FORMAT = "json";
            final String PARAMETERS_LOCATIONS = "locations";
            final String PARAMETERS_PATH = "path";
            final String APPID_PARAM = "key";

            //important: use android.net URI class, not JAVA!!!
            //parse base url -> build instance of builder -> append query parameters -> build url
            Uri buildUri = Uri.parse(GOOGLEMAPS_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAMETERS_LOCATIONS, locationsStr)
                    .appendQueryParameter(APPID_PARAM, APPID_KEY)
                    .build();

            //check how generated uri looks like
            Log.v(LOG_TAG, "URI has been built: " + buildUri.toString());

            //build string url
            URL url = new URL(buildUri.toString());

            //step 2: creation of request to google maps API, opening connection with web
            //incompatible type if without (HttpURLConnection call)
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //step 3: read input stream from web by getting stream from opened connection
            // get input stream from url connection -> create StringBuffer instance ->
            // define BufferedReader here with InputStreamReader -> append strings to the StringBuffer in a loop ->
            // define string to show as StringBuffer.toString();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null)
            {
                //set as null if no data to show
                Log.v(LOG_TAG, "Input stream was empty, no data to shown, return null");
                return null;
            }

            StringBuffer stringBuffer = new StringBuffer();

            //define BufferedReader by got input stream
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //have to use additional string (line) to call readLine() just once ( so lines from stream won't be losed and empty)
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                //append "line" instead calling readLine() again
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0)
            {
                //string stream was empty, so no point in further parsing-> return null so no data is shown
                Log.v(LOG_TAG, "String stream was empty, no data to shown, return null");
                return null;
            }

            //define altitude string
            altitudeJsonStr = stringBuffer.toString();

            //log message with result string
            Log.v(LOG_TAG, "altitude string generated: " + altitudeJsonStr);
        }
        catch (IOException e)
        {
            //if error occur, code didn't get data from web so no point in performing further data parsing, return null
            Log.v(LOG_TAG, "Error, at getting data from web:", e);
            return null;
        }
        finally
        {
            //close opened url connection
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            //try to close opened buffered reader
            if (bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                    Log.v(LOG_TAG, "Error, at BufferedReaded closing:", e);
                    e.printStackTrace();
                }
            }
        }

        try
        {
            //TODO change num of points from fixed to method generated
            getAltitudeDataFromJson(altitudeJsonStr, 1);
        } catch (JSONException e)
        {
            Log.v(LOG_TAG, "Error occur, getting data from defined JSON string: ", e);
            e.printStackTrace();
        }

        return null;
    }

    //method to extract data in correct form from given Json String;
    // TODO-> methods to define number of points, right now fixed dummy
    public String[] getAltitudeDataFromJson(String altitudeJsonStr, int numOfPoints) throws JSONException
    {

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
        for (int i=0; i<altitudeJsonArray.length(); i++)
        {
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

        for (String pointEntry : resultArray)
        {
            Log.v(LOG_TAG, "Point entry created: " + pointEntry);
        }

        return resultArray;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        if (mCurrentEleValue != null)
        {
            //round elevation value (to set precision to meters)
            mCurrentEleValue = (double) Math.round(mCurrentEleValue);

            //pass data to MainFragment through AsyncResponse interface
            asyncResponse.processAccurateElevation(mCurrentEleValue);
        }
        else
        {
            Log.v(LOG_TAG, " onPostExecute, current elevation wasn't fetched from JSON, stopped");
        }
    }
}
