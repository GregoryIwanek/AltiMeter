package pl.grzegorziwanek.altimeter.app.model.location.services;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import pl.grzegorziwanek.altimeter.app.model.location.CallbackResponse;

/**
 * Created by on 02.02.2017.
 */
public class GoogleLocationListener implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CallbackResponse {

    private final LocationChangedCallback mCallback;
    private final String LOG_TAG = getClass().getSimpleName();
    private final Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private GoogleLocationListener(Context context, LocationChangedCallback callback) {
        mContext = context;
        mCallback = callback;
        buildGooglePlayService();
        connectGoogleAPIClient();
    }

    public static GoogleLocationListener getInstance(Context context, LocationChangedCallback callback) {
        return new GoogleLocationListener(context, callback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Do nothing, location request is built after PLAY is clicked
    }

    @Override
    public void onLocationChanged(Location location) {
        mCallback.onNewLocationFound(location);
    }

    @Override
    public void startListenForLocations(@Nullable FullLocationInfoCallback callback) {
        // set location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest = setLocationRequest(locationRequest);

        // build and add request
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        // check for location permissions
        checkLocationPermissions(mContext, locationRequest);
    }

    @Override
    public void stopListenForLocations() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private LocationRequest setLocationRequest(LocationRequest locationRequest) {
        // set accuracy mode and decimal accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setSmallestDisplacement(5);

        // get preferences from app settings screen
        SharedPreferences preferences = getDefaultPreferences();

        // set interval
        String interval = preferences.getString("pref_sync_frequency_key", "5");
        Long intervalLong = Long.valueOf(interval);
        locationRequest.setInterval(intervalLong);

        // set fastest possible interval
        if (intervalLong < 60000) {
            locationRequest.setFastestInterval(30000);
        } else {
            locationRequest.setFastestInterval(intervalLong/2);
        }

        return locationRequest;
    }

    private void checkLocationPermissions(@NonNull Context context, LocationRequest locationRequest) {
        // check for location permissions (required in android API 23 and above)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            // remove old location request and add new one
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    private void buildGooglePlayService() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void connectGoogleAPIClient() {
        mGoogleApiClient.connect();
    }

    private SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, " connection suspended triggered!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, " connection failed triggered!");
    }
}
