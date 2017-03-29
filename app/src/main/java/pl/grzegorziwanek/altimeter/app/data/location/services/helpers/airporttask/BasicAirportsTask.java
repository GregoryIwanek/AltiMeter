package pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask;

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
import java.sql.SQLOutput;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportParser;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
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
                System.out.println("CALLED GET NEAREST AIRPORTS OBSERVABLE with mode: " + parserMode);
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
            System.out.println("got sXmlStr as: " + sXmlStr);
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
        System.out.println("CALLED GET AIRPORTS FROM XML");
        // Read xml
        BufferedReader bReader = new BufferedReader(new StringReader(sXmlStr));
        InputSource source = new InputSource(bReader);

        System.out.println("PARSING XML");
        // Parse xml
        XmlAirportParser parser = new XmlAirportParser();
        System.out.println("PARSER CREATED");
        parser.setMode(parserMode);
        System.out.println("PARSER MODE SET");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        System.out.println("PARSER FACTORY CREATED");
        SAXParser sp = factory.newSAXParser();
        System.out.println("SAX PARSER CREATED");
        XMLReader reader = sp.getXMLReader();
        System.out.println("XML READER CREATED");
        reader.setContentHandler(parser);
        System.out.println("READER SET CONTENT HANDLER");
        reader.parse(source);
        System.out.println("READER PARSE SOURCE");

        System.out.println("XML PARSED");
        //close reader
        try {
            bReader.close();
        } catch (IOException e)
        {
            System.out.println("IN XML PARSER IO EXCEPTION OCCUR");
            e.printStackTrace();
        }

        List<XmlAirportValues> airportValuesList = parser.getAirportsList();
        if (parserMode.equals("GET_METAR")) {
            parser.clearList();
        }
        System.out.println("GOT TO THE RETURN PARSED AIRPORT LIST");
        return airportValuesList;
    }
}
