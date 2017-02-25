package pl.grzegorziwanek.altimeter.app.data.location.services.helpers.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;

/**
 * Created by Grzegorz Iwanek on 24.02.2017.
 */

public class XmlAirportParser extends DefaultHandler {
    private static  XmlAirportParser parser;
    private static List<XmlAirportValues> list = null;
    private StringBuilder builder = null;
    private XmlAirportValues airportsValues = null;
    private String xmlMode;
    private int currentId;

    private XmlAirportParser() {

    }

    public static XmlAirportParser getInstance() {
        if (parser == null) {
            parser = new XmlAirportParser();
        }
        return parser;
    }

    @Override
    public void startDocument() throws SAXException {
        if (list == null) {
            list = new ArrayList<>();
            BarometerManager.setAirportsList(list);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // Create StringBuilder object to store xml node value
        builder=new StringBuilder();

        if(localName.equals("Station")){
            airportsValues = new XmlAirportValues();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isModeStations()) {
            appendInStationsMode(localName);
        } else if (isModeMetar()) {
            appendInMetarMode(localName);
        }
        BarometerManager.setAirportsList(list);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tempString = new String(ch, start, length);
        builder.append(tempString);
    }

    private void appendInStationsMode(String localName) {
        switch (localName) {
            case "Station":
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
        switch (localName) {
            case "station_id":
                findStationOnList(builder.toString());
                break;
            case "altim_in_hg":
                list.get(currentId).setPressureInHg(Double.parseDouble(builder.toString()));
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

    public List<XmlAirportValues> getList() {
        return list;
    }

    public void setMode(String mode) {
        xmlMode = mode;
    }

    private boolean isModeStations() {
        return xmlMode.equals("STATIONS");
    }

    private boolean isModeMetar() {
        return xmlMode.equals("METAR");
    }

    public void clearList() {
        list = null;
    }
}
