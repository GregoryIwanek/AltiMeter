package pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask;

import android.net.Uri;
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
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportParser;
import pl.gregoryiwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
import rx.Observable;
import rx.functions.Func0;

/**
 * Consists superclass of all airport data tasks.
 * Used to fetch data of closest airports and download specific weather information (which work
 * as a base for sensor calculations), required to calculate altitude of the device by using pressure sensor.
 */
abstract class BasicAirportsTask {

    private String sXmlStr;

    Observable<List<XmlAirportValues>> getNearestAirportsObservable(final Uri airportUri, final String parserMode) {
        return Observable.defer(new Func0<Observable<List<XmlAirportValues>>>() {
            @Override
            public Observable<List<XmlAirportValues>> call() {
                return Observable.just(getNearestAirports(airportUri, parserMode));
            }
        });
    }

    private List<XmlAirportValues> getNearestAirports(Uri airportUri, String parserMode) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(airportUri.toString());

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

            // set xml string to parse
            sXmlStr = stringBuilder.toString();
            System.out.println(sXmlStr);
        } catch (IOException e) {
            Log.d(getClass().getSimpleName(), "ERROR IOS EXCEPTION");
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

        // return list with airport stations (with basic station data)
        try {
            return getAirportsFromXml(parserMode);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<XmlAirportValues> getAirportsFromXml(String parserMode) throws ParserConfigurationException, SAXException, IOException {
        // Read xml
        BufferedReader bReader = new BufferedReader(new StringReader(sXmlStr));
        InputSource source = new InputSource(bReader);

        // Parse xml
        XmlAirportParser parser = new XmlAirportParser();
        parser.setMode(parserMode);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser sp = factory.newSAXParser();
        XMLReader reader = sp.getXMLReader();
        reader.setContentHandler(parser);
        reader.parse(source);

        //close reader
        try {
            bReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        List<XmlAirportValues> airportValuesList = parser.getAirportsList();
        if (parserMode.equals("GET_METAR")) {
            parser.clearList();
        }
        return airportValuesList;
    }
}
