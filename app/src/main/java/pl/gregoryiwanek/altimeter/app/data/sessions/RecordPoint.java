package pl.gregoryiwanek.altimeter.app.data.sessions;

public class RecordPoint {

    private final String latitude;
    private final String longitude;
    private String altitude;
    private String date;
    private String address;
    private String distance;

    public RecordPoint(Builder builder) {
        latitude = builder.latitude;
        longitude = builder.longitude;
        altitude = builder.altitude;
        date = builder.date;
        address = builder.address;
        distance = builder.distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getDistance() {
        return distance;
    }

    public static class Builder {
        private final String latitude;
        private final String longitude;
        private String altitude;
        private String date;
        private String address;
        private String distance;

        public Builder(String latitude, String longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Builder setAltitude(String altitude) {
            this.altitude = altitude;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setDistance(String distance) {
            this.distance = distance;
            return this;
        }

        public RecordPoint build() {
            return new RecordPoint(this);
        }
    }
}
