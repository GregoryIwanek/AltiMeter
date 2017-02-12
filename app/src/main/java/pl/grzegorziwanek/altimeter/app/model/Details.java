package pl.grzegorziwanek.altimeter.app.model;

/**
 * Created by Grzegorz Iwanek on 12.02.2017.
 */

public class Details {

    private String mTitle;
    private String mDescription;
    private String mUniqueId;
    private String mNumOfPoints;
    private String mTimeStart;
    private String mTimeEnd;
    private String mDistance;
    private String mMaxHeight;
    private String mMinHeight;
    private String mLastAddress;

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setUniqueId(String uniqueId) {
        mUniqueId = uniqueId;
    }

    public String getUniqueId() {
        return mUniqueId;
    }

    //TODO-> consider parameter String or Int??
    public void setNumOfPoints(int numOfPoints) {
        mNumOfPoints = String.valueOf(numOfPoints);
    }

    public String getNumOfPoints() {
        return mNumOfPoints;
    }

    public void setTimeStart(String timeStart) {
        mTimeStart = timeStart;
    }

    public String getTimeStart() {
        return mTimeStart;
    }

    public void setTimeEnd(String timeEnd) {
        mTimeEnd = timeEnd;
    }

    public String getTimeEnd() {
        return mTimeEnd;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getDistance() {
        return mDistance;
    }

    public String getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(String maxHeight) {
        mMaxHeight = maxHeight;
    }

    public String getMinHeight() {
        return mMinHeight;
    }

    public void setMinHeight(String minHeight) {
        mMinHeight = minHeight;
    }

    public String getLastAddress() {
        return mLastAddress;
    }

    public void setLastAddress(String address) {
        mLastAddress = address;
    }
}

