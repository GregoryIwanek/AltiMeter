package pl.gregoryiwanek.altimeter.app.data.location.managers;

/**
 * Created by Grzegorz Iwanek on 21.02.2017.
 */
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
