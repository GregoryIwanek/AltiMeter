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

    private String mTitle;
    private String mDescription;
    private ArrayList<Location> mLocationList;
    private final String mId;

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

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}
