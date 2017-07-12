package pl.gregoryiwanek.altimeter.app.data.sessions;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model class for Session; Stores location information about graph and session description;
 */
public class Session {

    private final String mId;
    private boolean mCompleted = false;
    private boolean mLocked = false;
    private String mTitle = "TITLE";
    private String mDescription = "DESCRIPTION";

    private String mLatitudeStr = "...";
    private String mLongitudeStr = "...";
    private String mAddress = "Solar System," +'\n'+ "Milky Way," +'\n'+ "Laniakea";
    private String mMinHeightStr = "...";
    private String mMaxHeightStr = "...";
    private String mDistanceStr = "0 m";

    private Double mDistance = (double) 0;
    private Double mCurrElevation = (double) 0;
    private Double mMinHeight = (double) 10000;
    private Double mMaxHeight = (double) -10000;
    private Location mLastLocation = null;
    private Location mCurrLocation = null;
    private ArrayList<Location> mLocationList = null;
    private ArrayList<GraphPoint> mGraphList = null;
    private ArrayList<RecordPoint> mRecordPointList = null;

    // temporary variables
    // TODO: 05.07.2017 variables to remove
    private String mDistanceStringTemporary = "0";
    private String mDateTemporary = "";
    private String mCurrElevationTemporary = "";

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
        mRecordPointList = new ArrayList<>();
    }

    public void appendLocationPoint(Location location) {
        mLocationList.add(location);
        // TODO: 05.07.2017 remove append record point
        appendRecordPoint(location);
    }

    public ArrayList<Location> getLocationList() {
        return mLocationList;
    }

    public void appendGraphPoint(long xValue, double yValue) {
        GraphPoint point = new GraphPoint(xValue, yValue);
        mGraphList.add(point);
    }

    // TODO: 07.07.2017 remove for loop; added just to increase number of points
    public void appendRecordPoint(Location location) {
        // TODO: 11.07.2017 to remove if else, merge that
        mRecordPointList.add(
                new RecordPoint.Builder(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()))
                        .setAltitude(mCurrElevationTemporary)
                        .setDate(mDateTemporary)
                        .setAddress(mAddress)
                        .setDistance(mDistanceStringTemporary)
                        .build()
        );
    }

    public ArrayList<RecordPoint> getRecordList() {
        return mRecordPointList;
    }

    public ArrayList<GraphPoint> getGraphList() {
        return mGraphList;
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
        // TODO: 05.07.2017 to remove temporary variable
        mCurrElevationTemporary = String.valueOf(elevation);
    }

    public void setElevationOnList(Double elevation) {
        mLocationList.get(mLocationList.size()-1).setAltitude(elevation);
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
        // TODO: 05.07.2017 remove this distance temporary
        mDistanceStringTemporary = String.valueOf(distance);
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
        // TODO: 05.07.2017 remove date temporary
        mDateTemporary = String.valueOf(mCurrLocation.getTime());
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

    public void clearData() {
        mCompleted = false;
        mTitle = "TITLE";
        mDescription = "DESCRIPTION";

        mLatitudeStr = "...";
        mLongitudeStr = "...";
        mAddress = "Solar System," + '\n' + "Milky Way," + '\n' + "Laniakea";
        mMinHeightStr = "...";
        mMaxHeightStr = "...";
        mDistanceStr = "0 m";

        mDistance = (double) 0;
        mCurrElevation = (double) 0;
        mMinHeight = (double) 10000;
        mMaxHeight = (double) -10000;
        mLastLocation = null;
        mCurrLocation = null;
        mLocationList.clear();
        mGraphList.clear();
    }
}
