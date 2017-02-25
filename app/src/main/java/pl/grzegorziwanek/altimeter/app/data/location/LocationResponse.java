package pl.grzegorziwanek.altimeter.app.data.location;

import android.location.Location;
import android.support.annotation.Nullable;

import pl.grzegorziwanek.altimeter.app.data.Session;

/**
 * Created by Grzegorz Iwanek on 20.12.2016.
 */
public interface LocationResponse {

    interface LocationChangedCallback {

        void onInitialLocationIdentified(Location location);
    }

    interface BarometerElevationCallback {

        void onBarometerElevationFound(Double barAltitude);
    }

    interface AirportsCallback {

        void onNearestAirportsFound();

        void onAirportPressureFound();
    }

    interface AddressFoundCallback {

        void onAddressFound(String address);
    }

    interface GpsElevationCallback {

        void onGpsLocationFound(Location location);
    }

    interface NetworkElevationCallback {

        void onNetworkElevationFound(Double elevation);
    }

    interface FullInfoCallback {

        void onFullInfoAcquired(Session session);

        void onBarometerInfoAcquired(String barometerAlt);

        void onGpsInfoAcquired(String gpsAlt);

        void onNetworkInfoAcquired(String networkAlt);
    }

    /**
     * Terminates or pauses location recording, depending on clicked button form view (pause / lock button)
     * @param isLocked true if session can't be modified further
     */
    void stopListenForLocations(boolean isLocked);

    void startListenForLocations(FullInfoCallback callback);

    void identifyCurrentLocation();

    void clearSessionData();
}
