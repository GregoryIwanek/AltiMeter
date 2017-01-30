package pl.grzegorziwanek.altimeter.app.data;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Model class for Session; Stores location information about graph and session description;
 */

public final class Session {

    private final String mId;
    private final String mTitle;
    private String mDescription;
    private ArrayList<Location> mLocationList;

    public Session(@Nullable String title, @Nullable String description,
                   @NonNull String id) {
        mId = id;
        mTitle = title;
        mDescription = description;
    }

    public boolean isEmpty() {
        return mLocationList.isEmpty();
    }

    public void appendLocation(Location location) {
     mLocationList.add(location);
    }

    public void appendLocations(ArrayList<Location> locations) {
        for (Location l : locations) {
            mLocationList.add(l);
        }
    }

    public void removeLocation(int id) {
        mLocationList.remove(id);
    }

    public void removeLocations(int fromId, int toId) {
        if (fromId >= 0 && toId <= mLocationList.size()-1) {
            remove(fromId, toId);
        }
        //TODO-> add toast for "else": wrong given range, correct input id data
    }

    private void remove(int fromId, int toId) {
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
