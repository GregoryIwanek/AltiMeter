package pl.grzegorziwanek.altimeter.app.model.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.grzegorziwanek.altimeter.app.model.Constants;
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
    private static Session mSession = null;
    private Boolean mHasCallback = false;

    private LocationCollector(@NonNull Context context) {
        setCallbacks();
        setVariables(context);
    }

    public static LocationCollector getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocationCollector(context);
        }
        return INSTANCE;
    }

    private void setVariables(Context context) {
        mSession = new Session("","");
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
                saveSessionsLocation(location);
                fetchCurrentElevation(location);
                fetchAddress(location);
            }
        };

        callbackElevation = new ElevationFetchedCallback() {
            @Override
            public void onElevationFound(Double elevation) {
                setCurrentElevation(elevation);
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

    private void saveSessionsLocation(Location location) {
        //save old, previous location
        if (mSession.getCurrentLocation() != null) {
            mSession.setLastLocation(mSession.getCurrentLocation());
        }

        //save new, current location
        mSession.setCurrLocation(location);
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

    private void setCurrentElevation(Double elevation) {
        mSession.setCurrentElevation(elevation);
        mSession.getCurrentLocation().setAltitude(elevation);
        mSession.appendOneLocationPoint(mSession.getCurrentLocation());
    }

    private void setAddress(String address) {
        mSession.setAddress(address);
    }

    private void checkIfHaveFullInfo() {
        if (mHasCallback && mSession.getCurrentElevation() != null && mSession.getAddress() != null) {
            setFullInfoOfSession();
            callbackFullInfo.onFullLocationInfoAcquired(mSession);
        }
    }

    private void setFullInfoOfSession() {
        setGeoCoordinateStr();
        setSessionsDistance();
        setSessionsHeight();
    }

    private void setGeoCoordinateStr() {
        Location location = mSession.getCurrentLocation();
        String latitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLatitude(), true);
        String longitudeStr =
                FormatAndValueConverter.setGeoCoordinateStr(location.getLongitude(), false);
        mSession.setLatitudeStr(latitudeStr);
        mSession.setLongitudeStr(longitudeStr);
    }

    private void setSessionsDistance() {
        if (mSession.getLastLocation() != null) {
            Location lastLocation = mSession.getLastLocation();
            Location currentLocation = mSession.getCurrentLocation();
            Double currentDistance = mSession.getDistance();

            Double distance = FormatAndValueConverter.updateDistanceValue(
                    lastLocation, currentLocation, currentDistance);
            mSession.setDistance(distance);

            String distanceStr =
                    FormatAndValueConverter.setDistanceStr(distance);
            mSession.setDistanceStr(distanceStr);
        }
    }

    private void setSessionsHeight() {
        Double currAltitude = mSession.getCurrentElevation();
        Double minHeight = mSession.getMinHeight();
        Double maxHeight = mSession.getMaxHeight();

        Double newMinHeight =
                FormatAndValueConverter.updateMinAltitudeValue(currAltitude, minHeight);
        Double newMaxHeight =
                FormatAndValueConverter.updateMaxAltitudeValue(currAltitude, maxHeight);
        String newMinStr =
                FormatAndValueConverter.setMinMaxString(newMinHeight);
        String newMaxStr =
                FormatAndValueConverter.setMinMaxString(newMaxHeight);

        mSession.setMinHeight(newMinHeight);
        mSession.setMinHeightStr(newMinStr);
        mSession.setMaxHeight(newMaxHeight);
        mSession.setMaxHeightStr(newMaxStr);
    }

    @Override
    public void stopListenForLocations() {
        mGoogleLocationListener.stopListenForLocations();
    }

    @Override
    public void startListenForLocations(@Nullable FullLocationInfoCallback callback) {
        callbackFullInfo = callback;
        mHasCallback = true;
        updateDistanceUnits();
        mGoogleLocationListener.startListenForLocations(null);
    }

    private void updateDistanceUnits() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }

    public Session getSession() {
        return mSession;
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


