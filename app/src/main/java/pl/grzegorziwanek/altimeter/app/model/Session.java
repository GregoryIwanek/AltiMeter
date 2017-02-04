package pl.grzegorziwanek.altimeter.app.model;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Model class for Session; Stores location information about graph and session description;
 */

public final class Session {

    private final String mId;
    private String mTitle = "TITLE";
    private String mDescription = "DESCRIPTION";

    private String mLatitudeStr = "00°00'00''X";
    private String mLongitudeStr = "00°00'00''Y";
    private String mAddress = "Solar System," +'\n'+ "Milky Way," +'\n'+ "Laniakea";

    private Double mCurrElevation = null;
    private Double mMinHeight = (double) 10000;
    private Double mMaxHeight = (double) -10000;
    private String mMinHeightStr = "TEST 6"; //??
    private String mMaxHeightStr = "TEST 7"; //??
    private static Location mLastLocation = null;
    private static ArrayList<Location> mLocationList = null;

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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLatitude() {
        return mLatitudeStr;
    }

    public void setLatitude(String latitude) {
        mLatitudeStr = latitude;
    }

    public String getLongitude() {
        return mLongitudeStr;
    }

    public void setLongitude(String longitude) {
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

    public ArrayList<Location> getLocationList() {
        return mLocationList;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public void setLastLocation(Location location) {
        mLastLocation = location;
    }
}
