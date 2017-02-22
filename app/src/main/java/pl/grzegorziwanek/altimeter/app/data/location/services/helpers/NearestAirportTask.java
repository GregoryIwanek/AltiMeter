package pl.grzegorziwanek.altimeter.app.data.location.services.helpers;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.grzegorziwanek.altimeter.app.data.Constants;
import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;

/**
 * Created by Grzegorz Iwanek on 19.02.2017.
 */

public class NearestAirportTask extends AsyncTask<Void, Void, Void> {

    private String mRadialDistanceStr;
    private LocationResponse.AirportsCallback mCallback;

    public NearestAirportTask(LocationResponse.AirportsCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... Void) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String seaLevelPressureStr = null;

        try {
            // construction of the Open Weather Map URL query
            Uri buildUri = parseNearestAirportUri();
            System.out.println("URL NEAREST AIRPORT HAS BEEN BUILT: " + buildUri);
            URL url = new URL(buildUri.toString());

            // construction of request to Open Weather Map API, opening connection with a web
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read input stream from a web by getting stream from opened connection
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // set as null if no data to show
                System.out.println("INPUT STREAM NULL");
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                // append new line to builder
                stringBuilder.append(line).append("\n");
                System.out.println("APPEND LINE");
            }

            if (stringBuilder.length() == 0) {
                // string stream was empty, end with null
                System.out.println("STRING BUILDER LENGTH 0 NULL");
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

    private Uri parseNearestAirportUri() {
        return Uri.parse(Constants.AVIATION_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.AVIATION_DATA_SOURCE, "stations")
                .appendQueryParameter(Constants.AVIATION_REQUEST_TYPE, "retrieve")
                .appendQueryParameter(Constants.AVIATION_FORMAT, "xml")
                .appendQueryParameter(Constants.AVIATION_RADIAL_DISTANCE, mRadialDistanceStr)
                .build();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mCallback.onNearestAirportsFound();
    }

    public void setRadialDistanceStr(String radialDistanceStr) {
        mRadialDistanceStr = radialDistanceStr;
    }
}
//    <?xml version="1.0" encoding="UTF-8"?>
//    <response xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XML-Schema-instance" version="1.0" xsi:noNamespaceSchemaLocation="http://weather.aero/schema/station1_0.xsd">
//    <request_index>942754095</request_index>
//    <data_source name="stations" />
//    <request type="retrieve" />
//    <errors />
//    <warnings />
//    <time_taken_ms>5</time_taken_ms>
//    <data num_results="9">
//    <Station>
//    <station_id>EPBC</station_id>
//    <latitude>52.27</latitude>
//    <longitude>20.92</longitude>
//    <elevation_m>107.0</elevation_m>
//    <site>WARSZAWA/BABICE</site>
//    <country>PL</country>
//    </Station>
//    <Station>
//    <station_id>EPDE</station_id>
//    <latitude>51.55</latitude>
//    <longitude>21.9</longitude>
//    <elevation_m>120.0</elevation_m>
//    <site>DEBLIN-IRENA</site>
//    <country>PL</country>
//    <site_type>
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>EPLB</station_id>
//    <latitude>51.23</latitude>
//    <longitude>22.72</longitude>
//    <elevation_m>206.0</elevation_m>
//    <site>LUBLIN</site>
//    <country>PL</country>
//    <site_type>
//    <METAR />
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>EPMM</station_id>
//    <latitude>52.2</latitude>
//    <longitude>21.65</longitude>
//    <elevation_m>185.0</elevation_m>
//    <site>MINSK MAZOWIECKI</site>
//    <country>PL</country>
//    <site_type>
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>EPMO</station_id>
//    <latitude>52.45</latitude>
//    <longitude>20.65</longitude>
//    <elevation_m>104.0</elevation_m>
//    <site>MODLIN</site>
//    <country>PL</country>
//    <site_type>
//    <METAR />
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>EPRA</station_id>
//    <latitude>51.38</latitude>
//    <longitude>21.22</longitude>
//    <elevation_m>178.0</elevation_m>
//    <site>RADOM AIRPORT</site>
//    <country>PL</country>
//    <site_type>
//    <METAR />
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>EPTM</station_id>
//    <latitude>51.58</latitude>
//    <longitude>20.1</longitude>
//    <elevation_m>174.0</elevation_m>
//    <site>SPAA GLINNIK</site>
//    <country>PL</country>
//    <site_type>
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>EPWA</station_id>
//    <wmo_id>12375</wmo_id>
//    <latitude>52.17</latitude>
//    <longitude>20.97</longitude>
//    <elevation_m>107.0</elevation_m>
//    <site>WARSAW/OKECIE</site>
//    <country>PL</country>
//    <site_type>
//    <METAR />
//    <TAF />
//    </site_type>
//    </Station>
//    <Station>
//    <station_id>UMBB</station_id>
//    <latitude>52.12</latitude>
//    <longitude>23.9</longitude>
//    <elevation_m>143.0</elevation_m>
//    <site>BREST</site>
//    <country>BY</country>
//    <site_type>
//    <METAR />
//    <TAF />
//    </site_type>
//    </Station>
//    </data>
//    </response>
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
//
//https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&hoursBeforeNow=3&mostRecent=true&stationString=EPLB
//        https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=KDEN%20KSEA,%20PHNL&hoursBeforeNow=2
//        https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=100;22.22,52.00

//http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=%1$s&hoursBeforeNow=%2$d&MostRecent=true</string>
//
//http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=%1$d;%2$f,%3$f</string>

//<string name="airport_metar_url">http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=%1$s&hoursBeforeNow=%2$d&MostRecent=true</string>
//
//<string name="airport_nearest_url">http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=%1$d;%2$f,%3$f</string>