package pl.grzegorziwanek.altimeter.app.data.location.services.helpers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.xmlparser.XmlAirportParser;
import pl.grzegorziwanek.altimeter.app.utils.Constants;

/**
 * Created by Grzegorz Iwanek on 19.02.2017.
 */

public class PressureAirportTask extends AsyncTask<Void, Void, Void> {

    private String mRadialDistanceStr;
    private String mStationsStr;
    private final LocationResponse.AirportsCallback mCallback;
    private boolean isFetchingStations;

    public PressureAirportTask(LocationResponse.AirportsCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... Void) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String airportStationsXml;

        try {
            Uri buildUri = constructUri();
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
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() == 0) {
                // string stream was empty, end with null
                return null;
            }

            // define sea pressure string
            airportStationsXml = stringBuilder.toString();
            parseXml(airportStationsXml);
        } catch (IOException e) {
            Log.d(getClass().getSimpleName(), "ERROR IOS EXCEPTION");
            return null;
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
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
            getSeaPressureFromJson();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void getSeaPressureFromJson() {
    }

    private void parseXml(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
        // Read xml
        BufferedReader br = new BufferedReader(new StringReader(xmlStr));
        InputSource source = new InputSource(br);

        // Parse xml
        XmlAirportParser parser = XmlAirportParser.getInstance();
        parser.setMode(getMode());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser sp = factory.newSAXParser();
        XMLReader reader = sp.getXMLReader();
        reader.setContentHandler(parser);
        reader.parse(source);
    }

    private Uri constructUri() {
        if (isFetchingStations) {
            return parseNearestAirportUri();
        } else {
            return parseAirportPressureUri();
        }
    }

    private Uri parseNearestAirportUri() {
        return Uri.parse(Constants.AVIATION_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.AVIATION_DATA_SOURCE, "stations")
                .appendQueryParameter(Constants.AVIATION_REQUEST_TYPE, "retrieve")
                .appendQueryParameter(Constants.AVIATION_FORMAT, "xml")
                .appendQueryParameter(Constants.AVIATION_RADIAL_DISTANCE, mRadialDistanceStr)
                .build();
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(isFetchingStations) {
            mCallback.onNearestAirportsFound();
        } else {
            mCallback.onAirportPressureFound();
        }
    }

    private String getMode() {
        if (isFetchingStations) {
            return "STATIONS";
        } else {
            return "METAR";
        }
    }

    public void setRadialDistanceStr(String radialDistanceStr) {
        mRadialDistanceStr = radialDistanceStr;
    }

    public void setStationsString(String stationsStr) {
        mStationsStr = stationsStr;
    }

    public void setFetchingStations(boolean fetchingStations) {
        isFetchingStations = fetchingStations;
    }
}

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