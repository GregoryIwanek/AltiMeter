package pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser;

/**
 * Consists class holding data about a single airport.
 */
public class XmlAirportValues {

    private String id;
    private String country = "";
    private String site = "";
    private float latitude;
    private float longitude;
    private float elevation;
    private double pressureInHg;
    private float distance = 10000;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public double getPressureInHg() {
        return pressureInHg;
    }

    public void setPressureInHg(double pressureInHg) {
        this.pressureInHg = pressureInHg;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}