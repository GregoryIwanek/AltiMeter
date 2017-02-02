package pl.grzegorziwanek.altimeter.app.model.location;

import android.location.Location;

/**
 * Created by Grzegorz Iwanek on 20.12.2016.
 */
public interface CallbackResponse {

    public interface LocationChangedCallback {

        void onNewLocationFound(Location location);
    }

    void stopListenForLocations();

    void startListenForLocations();
}
