package pl.grzegorziwanek.altimeter.app.model.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.model.location.services.AddressIntentService;
import pl.grzegorziwanek.altimeter.app.model.location.services.FetchElevationTask;
import pl.grzegorziwanek.altimeter.app.model.location.services.FormatAndValueConverter;
import pl.grzegorziwanek.altimeter.app.model.location.services.GoogleLocationListener;

/**
 * Created by ... on 01.02.2017.
 */

public class LocationCollector implements CallbackResponse {

    private static LocationCollector INSTANCE = null;
    private CallbackResponse mGoogleLocationListener;
    private FullLocationInfoCallback callbackFullInfo;
    private LocationChangedCallback callbackNewLocation;
    private ElevationFetchedCallback callbackElevation;
    private AddressFetchedCallback callbackAddress;
    private AddressResultReceiver mResultReceiver;
    private Context mContext;
    private Session mSession = null;
    private Boolean mHasCallback = false;

    private LocationCollector(@NonNull Context context) {
        setCallbacks();
        setVariables(context);
        mSession = new Session("DLA", "CIEBIE");
    }

    public static LocationCollector getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocationCollector(context);
        }
        return INSTANCE;
    }

    private void setVariables(Context context) {
        mContext = context;
        mGoogleLocationListener = GoogleLocationListener.getInstance(context, callbackNewLocation);
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    /**
     * Callbacks used to communicate between THIS {@link LocationCollector} and members:
     * {@link GoogleLocationListener}   sends back updates of location,
     * {@link FetchElevationTask}       sends back elevation of location,
     * {@link AddressIntentService}     sends back closest address of location.
     */
    private void setCallbacks() {
        callbackNewLocation = new LocationChangedCallback() {
            @Override
            public void onNewLocationFound(Location location) {
                setGeoCoordinateStr(location);
                saveLastLocation(location);
                fetchCurrentElevation(location);
                fetchAddress(location);
            }
        };

        callbackElevation = new ElevationFetchedCallback() {
            @Override
            public void onElevationFound(Double elevation) {
                setCurrentElevation(elevation);
                mSession.getLastLocation().setAltitude(elevation);
                mSession.appendOneLocationPoint(mSession.getLastLocation());
                checkIfHaveFullInfo();
            }
        };

        callbackAddress = new AddressFetchedCallback() {
            @Override
            public void onAddressFound(String address) {
                setAddress(address);
                checkIfHaveFullInfo();
            }
        };
    }

    private void checkIfHaveFullInfo() {
        if (mHasCallback && mSession.getCurrentElevation() != null && mSession.getAddress() != null) {
            callbackFullInfo.onFullLocationInfoAcquired(mSession);
        }
    }

    private void setGeoCoordinateStr(Location location) {
        String latitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLatitude(), true);
        String longitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLongitude(), false);
        mSession.setLatitude(latitudeStr);
        mSession.setLongitude(longitudeStr);
    }

    private void saveLastLocation(Location location) {
        mSession.setLastLocation(location);
    }

    private void fetchCurrentElevation(Location location) {
        FetchElevationTask task = new FetchElevationTask(callbackElevation);
        task.setLocationsStr(location);
        task.execute();
    }

    private void fetchAddress(Location location) {
        Intent intent = new Intent(mContext, AddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA, location);
        mContext.startService(intent);
    }

    private void setAddress(String address) {
        mSession.setAddress(address);
    }

    private void setCurrentElevation(Double elevation) {
        mSession.setCurrentElevation(elevation);
    }

    @Override
    public void stopListenForLocations() {
        mGoogleLocationListener.stopListenForLocations();
    }

    @Override
    public void startListenForLocations(@Nullable FullLocationInfoCallback callback) {
        callbackFullInfo = callback;
        mHasCallback = true;
        mGoogleLocationListener.startListenForLocations(null);
    }

    @SuppressLint("ParcelCreator")
    private class AddressResultReceiver extends ResultReceiver {

        String mAddressOutput;

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            callbackAddress.onAddressFound(mAddressOutput);
        }
    }
}


