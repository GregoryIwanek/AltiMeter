package pl.grzegorziwanek.altimeter.app.data.location.services.helpers;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.grzegorziwanek.altimeter.app.utils.Constants;

/**
 * Created by Grzegorz Iwanek on 18.02.2017.
 */

public class WeatherPressureService extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... Void) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String seaLevelPressureStr = null;

        try {
            // construction of the Open Weather Map URL query
            Uri buildUri = parseOpenWeatherUri();
            System.out.println("URL FORECAST HAS BEEN BUILT: " + buildUri);
            URL url = new URL(buildUri.toString());

            // construction of request to Open Weather Map API, opening connection with a web
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read input stream from a web by getting stream from opened connection
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // set as null if no data to show
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

            // define sea pressure string
            seaLevelPressureStr = stringBuilder.toString();
            System.out.println("JSON PRESSURE IS: " + seaLevelPressureStr);
        } catch (IOException e) {
            return null;
        } finally {
            // close opened url connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                // close buffered reader
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            getSeaPressureFromJson(seaLevelPressureStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getSeaPressureFromJson(String seaLevelPressureJsonStr) {
        return "";
    }

    private Uri parseOpenWeatherUri() {
        String APPID_KEY = "7ff99464f74720c3c0dc8286b789ab2b";
        return Uri.parse(Constants.FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.LAT_PARAM, "51.7971542")
                .appendQueryParameter(Constants.LON_PARAM, "22.2361077")
                .appendQueryParameter(Constants.APPID_WEATHER_PARAM, APPID_KEY)
                .build();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    //    @Override
//    protected void onPostExecute(String[] result) {
//        if (result != null && mForecastAdapter != null) {
//            mForecastAdapter.clear();
//            for(String dayForecastStr : result) {
//                mForecastAdapter.add(dayForecastStr);
//            }
//            // New data is back from the server.  Hooray!
//        }
//    }
}

//
//// If there's no zip code, there's nothing to look up.  Verify size of params.
//if (params.length == 0) {
//        return null;
//        }
//        String locationQuery = params[0];
//
//        // These two need to be declared outside the try/catch
//        // so that they can be closed in the finally block.
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//
//        // Will contain the raw JSON response as a string.
//        String forecastJsonStr = null;
//
//        String format = "json";
//        String units = "metric";
//        int numDays = 14;
//
//        try {
//// Construct the URL for the OpenWeatherMap query
//// Possible parameters are avaiable at OWM's forecast API page, at
//// http://openweathermap.org/API#forecast
//final String FORECAST_BASE_URL =
//        "http://api.openweathermap.org/data/2.5/forecast/daily?";
//final String QUERY_PARAM = "q";
//final String FORMAT_PARAM = "mode";
//final String UNITS_PARAM = "units";
//final String DAYS_PARAM = "cnt";
//final String APPID_PARAM = "APPID";
//
//        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//        .appendQueryParameter(QUERY_PARAM, params[0])
//        .appendQueryParameter(FORMAT_PARAM, format)
//        .appendQueryParameter(UNITS_PARAM, units)
//        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
//        .build();
//
//        URL url = new URL(builtUri.toString());
//
//        // Create the request to OpenWeatherMap, and open the connection
//        urlConnection = (HttpURLConnection) url.openConnection();
//        urlConnection.setRequestMethod("GET");
//        urlConnection.connect();
//
//        // Read the input stream into a String
//        InputStream inputStream = urlConnection.getInputStream();
//        StringBuffer buffer = new StringBuffer();
//        if (inputStream == null) {
//        // Nothing to do.
//        return null;
//        }
//        reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        String line;
//        while ((line = reader.readLine()) != null) {
//        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//        // But it does make debugging a *lot* easier if you print out the completed
//        // buffer for debugging.
//        buffer.append(line + "\n");
//        }
//
//        if (buffer.length() == 0) {
//        // Stream was empty.  No point in parsing.
//        return null;
//        }
//        forecastJsonStr = buffer.toString();
//        } catch (IOException e) {
//        Log.e(LOG_TAG, "Error ", e);
//        // If the code didn't successfully get the weather data, there's no point in attemping
//        // to parse it.
//        return null;
//        } finally {
//        if (urlConnection != null) {
//        urlConnection.disconnect();
//        }
//        if (reader != null) {
//        try {
//        reader.close();
//        } catch (final IOException e) {
//        Log.e(LOG_TAG, "Error closing stream", e);
//        }
//        }
//        }
//
//        try {
//        return getWeatherDataFromJson(forecastJsonStr, locationQuery);
//        } catch (JSONException e) {
//        Log.e(LOG_TAG, e.getMessage(), e);
//        e.printStackTrace();
//        }
//        // This will only happen if there was an error getting or parsing the forecast.
//        return null;

