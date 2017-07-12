package pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablerecords;

public class SQLRecordRow {

    private final String id;
    private final String latitude;
    private final String longitude;
    private final String altitude;
    private final String date;
    private final String address;
    private final String distance;

    private SQLRecordRow(Builder builder) {
        id = builder.id;
        latitude = builder.latitude;
        longitude = builder.longitude;
        altitude = builder.altitude;
        date = builder.date;
        address = builder.address;
        distance = builder.distance;
    }

    public String getId() {
        return id;
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

    static class Builder {

        private final String id;
        private String latitude = "";
        private String longitude = "";
        private String altitude = "";
        private String date = "";
        private String address = "";
        private String distance = "";

        Builder(String id) {
            this.id = id;
        }

        public Builder setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(String longitude) {
            this.longitude = longitude;
            return this;
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

        public SQLRecordRow build() {
            return new SQLRecordRow(this);
        }
    }
}

