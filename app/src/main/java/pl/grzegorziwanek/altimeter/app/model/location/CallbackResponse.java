package pl.grzegorziwanek.altimeter.app.model.location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import pl.grzegorziwanek.altimeter.app.model.Session;

/**
 * Created by Grzegorz Iwanek on 20.12.2016.
 */
public interface CallbackResponse {

    public interface LocationChangedCallback {

        void onNewLocationFound(Location location);
    }

    @SuppressLint("ParcelCreator")
    public interface AddressFetchedCallback extends Parcelable{

        void onAddressFound(String address);
    }

    public interface ElevationFetchedCallback {

        void onElevationFound(Double elevation);
    }

    public interface FullLocationInfoCallback {

        void onFullLocationInfoAcquired(Session session);
    }

    void stopListenForLocations();

    void startListenForLocations(@Nullable FullLocationInfoCallback callback);
}
