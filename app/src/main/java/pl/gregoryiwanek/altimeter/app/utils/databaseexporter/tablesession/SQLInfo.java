package pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablesession;

public class SQLInfo {

    private final String sessionId;
    private final String title;
    private final String description;
    private final String adress;
    private final String distance;
    private final String maxHeight;
    private final String minHeight;
    private final String altitude;

    public SQLInfo(Builder builder) {
        sessionId = builder.mSessionId;
        title = builder.title;
        description = builder.description;
        adress = builder.address;
        distance = builder.distance;
        maxHeight = builder.maxHeight;
        minHeight = builder.minHeight;
        altitude = builder.altitude;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAdress() {
        return adress;
    }

    public String getDistance() {
        return distance;
    }

    public String getMaxHeight() {
        return maxHeight;
    }

    public String getMinHeight() {
        return minHeight;
    }

    public String getAltitude() {
        return altitude;
    }

    public static class Builder {
        // required parameters
        private final String mSessionId;

        // optional parameters
        private String title = "";
        private String description = "";
        private String address = "";
        private String distance = "";
        private String maxHeight = "";
        private String minHeight = "";
        private String altitude = "";

        public Builder(String mSessionId) {
            this.mSessionId = mSessionId;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String mDescription) {
            this.description = mDescription;
            return this;
        }

        public Builder address(String mAdress) {
            this.address = mAdress;
            return this;
        }

        public Builder distance(String mDistance) {
            this.distance = mDistance;
            return this;
        }

        public Builder maxheight(String mMaxHeight) {
            this.maxHeight = mMaxHeight;
            return this;
        }

        public Builder minheight(String mMinHeight) {
            this.minHeight = mMinHeight;
            return this;
        }

        public Builder altitude(String mAltitude) {
            this.altitude = mAltitude;
            return this;
        }

        public SQLInfo build() {
            return new SQLInfo(this);
        }
    }
}
