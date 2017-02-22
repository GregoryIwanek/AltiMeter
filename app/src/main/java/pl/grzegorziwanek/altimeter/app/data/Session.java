package pl.grzegorziwanek.altimeter.app.data;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Model class for Session; Stores location information about graph and session description;
 */

public class Session {

    private boolean mCompleted = false;
    private boolean mLocked = false;
    private String mId;
    private String mTitle = "TITLE";
    private String mDescription = "DESCRIPTION";

    private String mLatitudeStr = "00°00'00''X";
    private String mLongitudeStr = "00°00'00''Y";
    private String mAddress = "Solar System," +'\n'+ "Milky Way," +'\n'+ "Laniakea";
    private String mMinHeightStr = "";
    private String mMaxHeightStr = "";
    private String mDistanceStr = "0 m";

    private Double mDistance = (double) 0;
    private Double mCurrElevation = null;
    private Double mMinHeight = (double) 10000;
    private Double mMaxHeight = (double) -10000;
    private Location mLastLocation = null;
    private Location mCurrLocation = null;
    private ArrayList<Location> mLocationList = null;
    private ArrayList<GraphPoint> mGraphList = null;

    private Double mBarometerAlt = null;

    /**
     * Use this constructor to create new recording session. Unique ID generated automatically.
     * @param title         session's title
     * @param description   sessions's description (generated from other member fields) TODO
     */
    public Session(@Nullable String title, @Nullable String description) {
        this(title, description, UUID.randomUUID().toString());
    }

    /**
     * Use this constructor to create new recording session. ID created manually.
     * @param title         session's title
     * @param description   session's description (generated from other member fields) TODO
     * @param id            session's unique id, has to be unique
     */
    public Session(@Nullable String title, @Nullable String description, @NonNull String id) {
        mTitle = title;
        mDescription = description;
        mId = id;
        mLocationList = new ArrayList<>();
        mGraphList = new ArrayList<>();
    }

    public boolean isEmpty() {
        return mLocationList.isEmpty();
    }

    public void appendOneLocationPoint(Location location) {
        mLocationList.add(location);
    }

    public void appendManyLocationPoints(ArrayList<Location> locations) {
        for (Location l : locations) {
            mLocationList.add(l);
        }
    }

    public void removeOneLocationPoint(int id) {
        mLocationList.remove(id);
    }

    public void removeManyLocationPoints(int fromId, int toId) {
        if (fromId >= 0 && toId <= mLocationList.size()-1) {
            removePoints(fromId, toId);
        }
        //TODO-> add toast for "else": wrong given range, correct input id data
    }

    private void removePoints(int fromId, int toId) {
        for (int i=toId; i>fromId; i--) {
            mLocationList.remove(i);
        }
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLatitudeStr() {
        return mLatitudeStr;
    }

    public String getLatNumericStr() {
        return String.valueOf(mCurrLocation.getLatitude());
    }

    public void setLatitudeStr(String latitude) {
        mLatitudeStr = latitude;
    }

    public String getLongitudeStr() {
        return mLongitudeStr;
    }

    public String getLongNumericStr() {
        return String.valueOf(mCurrLocation.getLongitude());
    }

    public void setLongitudeStr(String longitude) {
        mLongitudeStr = longitude;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public Double getCurrentElevation() {
        return mCurrElevation;
    }

    public void setCurrentElevation(Double elevation) {
        mCurrElevation = elevation;
    }

    public void setElevationOnList(Double elevation) {
        mLocationList.get(mLocationList.size()-1).setAltitude(elevation);
    }

    public ArrayList<Location> getLocationList() {
        return mLocationList;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public void setLastLocation(Location location) {
        mLastLocation = location;
    }

    public String getMinHeightStr() {
        return mMinHeightStr;
    }

    public void setMinHeightStr(String minHeightStr) {
        mMinHeightStr = minHeightStr;
    }

    public String getMaxHeightStr() {
        return mMaxHeightStr;
    }

    public void setMaxHeightStr(String maxHeightStr) {
        mMaxHeightStr = maxHeightStr;
    }

    public String getDistanceStr() {
        return mDistanceStr;
    }

    public void setDistanceStr(String distanceStr) {
        mDistanceStr = distanceStr;
    }

    public Double getDistance() {
        return mDistance;
    }

    public void setDistance(Double distance) {
        mDistance = distance;
    }

    public Double getMinHeight() {
        return mMinHeight;
    }

    public void setMinHeight(Double minHeight) {
        mMinHeight = minHeight;
    }

    public Double getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(Double maxHeight) {
        mMaxHeight = maxHeight;
    }

    public Location getCurrentLocation() {
        return mCurrLocation;
    }

    public void setCurrLocation(Location currLocation) {
        mCurrLocation = currLocation;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }

    public boolean ismLocked() {
        return mLocked;
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    public Double getBarometerAlt() {
        return mBarometerAlt;
    }

    public void setBarometerAlt(Double barometerAlt) {
        mBarometerAlt = barometerAlt;
    }

    public void clearData() {
        mCompleted = false;
        mTitle = "TITLE";
        mDescription = "DESCRIPTION";

        mLatitudeStr = "00°00'00''X";
        mLongitudeStr = "00°00'00''Y";
        mAddress = "Solar System," + '\n' + "Milky Way," + '\n' + "Laniakea";
        mMinHeightStr = null;
        mMaxHeightStr = null;
        mDistanceStr = "0 m";

        mDistance = (double) 0;
        mCurrElevation = null;
        mMinHeight = (double) 10000;
        mMaxHeight = (double) -10000;
        mLastLocation = null;
        mCurrLocation = null;
        mLocationList.clear();
    }

    public void appendGraphPoint(double xValue, double yValue) {
        GraphPoint point = new GraphPoint(xValue, yValue);
        mGraphList.add(point);
    }

    private class GraphPoint {
        private double xTime;
        private double yAltitude;

        GraphPoint(double xValue, double yValue) {
            xTime = xValue;
            yAltitude = yValue;
        }

        public double getXValue() {
            return xTime;
        }

        public double getYValue() {
            return yAltitude;
        }
    }
}
