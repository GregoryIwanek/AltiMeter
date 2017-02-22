package pl.grzegorziwanek.altimeter.app.data.location;

import pl.grzegorziwanek.altimeter.app.data.Session;

/**
 * Created by Grzegorz Iwanek on 22.02.2017.
 */

public class LocationDataCollector implements LocationResponse {
    private Session mSession = null;

    @Override
    public void stopListenForLocations(boolean isLocked) {

    }

    @Override
    public void startListenForLocations(FullInfoCallback callback) {

    }

    @Override
    public void identifyCurrentLocation() {

    }

    @Override
    public void clearSessionData() {

    }
}
