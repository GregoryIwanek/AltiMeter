package pl.gregoryiwanek.altimeter.app.data.location.managers;

public class NetworkManager {

    private boolean mNetworkEnabled;

    public boolean isNetworkEnabled() {
        return mNetworkEnabled;
    }

    public void setNetworkEnabled(boolean networkEnabled) {
        mNetworkEnabled = networkEnabled;
    }

    public void resetData() {
        mNetworkEnabled = false;
    }
}
