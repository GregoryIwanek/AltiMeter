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
    private String mTitle = "TFUU";
    private String mDescription = "SRU";
    private String mLatitude = "OOO";
    private String mLongitude = "DADA";
    private String mAddress = "HEHE";
    private Double mCurrentElevation;
    private String mMinHeight = "HUE";
    private String mMaxHeight = "HI";
    private Location mLastLocation;
    private ArrayList<Location> mLocationList;

//    private Session mSession;
//    private Location mLastLocation;
//    private String mLatitude;
//    private String mLongitude;
//    private String mAdress;
//    private Double mCurrentElevation;

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
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public Double getCurrentElevation() {
        return mCurrentElevation;
    }

    public void setCurrentElevation(Double elevation) {
        mCurrentElevation = elevation;
    }
}
