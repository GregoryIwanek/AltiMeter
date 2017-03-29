package pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Consists class which parse xml airports data and splits them into separated objects.
 */
public class XmlAirportParser extends DefaultHandler {

    private static List<XmlAirportValues> list = null;
    private StringBuilder builder = null;
    private XmlAirportValues airportsValues = null;
    private String xmlMode;
    private int currentId;

    @Override
    public void startDocument() throws SAXException {
        if (list == null) {
            list = new ArrayList<>();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // Create or reset StringBuilder object to store xml node value
        setBuilder();

        System.out.println("PARSER, CALLING START ELEMENT");
        if(localName.equals("Station")){
            System.out.println("PARSER, START ELEMENT, LOCAL NAME EQUALS STATIONS");
            airportsValues = new XmlAirportValues();
        }
    }

    private void setBuilder() {
        System.out.println("CALLING SET BUILDER IN PARSER");
        if (builder == null) {
            builder = new StringBuilder();
            System.out.println("CALLING SET BUILDER IN PARSER, NEW BUILDER");
        } else {
            System.out.println("CALLING SET BUILDER IN PARSER, SET LENGTH 0");
            builder.setLength(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        System.out.println("END ELEMENT CALLED");
        if (isModeStations()) {
            System.out.println("END ELEMENT, mode stations");
            appendInStationsMode(localName);
        } else if (isModeMetar()) {
            System.out.println("END ELEMENT, mode METAR");
            appendInMetarMode(localName);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tempString = new String(ch, start, length);
        builder.append(tempString);
    }

    private void appendInStationsMode(String localName) {
        System.out.println("local name in station mode is: " + localName);
        switch (localName) {
            case "Station":
                System.out.println("ADDing airportsValues to list");
                list.add(airportsValues);
                break;
            case "station_id":
                airportsValues.setId(builder.toString());
                break;
            case "latitude":
                airportsValues.setLatitude(Float.parseFloat(builder.toString()));
                break;
            case "longitude":
                airportsValues.setLongitude(Float.parseFloat(builder.toString()));
                break;
            case "elevation":
                airportsValues.setElevation(Float.parseFloat(builder.toString()));
                break;
            case "site":
                airportsValues.setSite(builder.toString());
                break;
            case "country":
                airportsValues.setCountry(builder.toString());
                break;
        }
    }

    private void appendInMetarMode(String localName) {
        System.out.println("local name in METAR mode is: " + localName);
        System.out.println("list size in METAR mode is: " + list.size());
        switch (localName) {
            case "station_id":
                findStationOnList(builder.toString());
                break;
            case "altim_in_hg":
                System.out.println("pressure in HG is:");
                list.get(currentId).setPressureInHg(Double.parseDouble(builder.toString()));
                System.out.println("pressure in HG is: " + list.get(currentId).getPressureInHg());
                break;
        }
    }

    private void findStationOnList(String airport_id) {
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).getId().equals(airport_id)) {
                currentId = i;
                break;
            }
        }
    }

    public void setMode(String mode) {
        xmlMode = mode;
    }

    private boolean isModeStations() {
        return xmlMode.equals("GET_STATIONS");
    }

    private boolean isModeMetar() {
        return xmlMode.equals("GET_METAR");
    }

    public List<XmlAirportValues> getAirportsList() {
        return list;
    }

    public void clearList() {
        list = null;
    }
}
