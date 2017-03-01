package pl.grzegorziwanek.altimeter.app.data.location.services.elevation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.io.IOException;

import pl.grzegorziwanek.altimeter.app.utils.Constants;
import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;
import pl.grzegorziwanek.altimeter.app.utils.EarthGravitationalModel;
import rx.Observer;

/**
 * Created by on 02.02.2017.
 */
public class GpsLocationListener implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, LocationResponse {

    private final LocationChangedCallback mCallback;
    private final GpsElevationCallback mGpsCallback;
    private final String LOG_TAG = getClass().getSimpleName();
    private final Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private EarthGravitationalModel mGravitationModel;
    private boolean isFastLocationRequest = false;

    private GpsLocationListener(Context context, LocationChangedCallback callback, GpsElevationCallback gpsCallback) {
        mContext = context;
        mCallback = callback;
        mGpsCallback = gpsCallback;
        mGravitationModel = new EarthGravitationalModel();
        buildGooglePlayService();
        connectGoogleAPIClient();
    }

    public static GpsLocationListener getInstance(Context context, LocationChangedCallback callback, GpsElevationCallback gpsCallback) {
        return new GpsLocationListener(context, callback, gpsCallback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        loadEarthGravitationalModel();
        identifyCurrentLocation();
    }

    private void loadEarthGravitationalModel() {
        // fix ellipsoid's WSG84 incorrect data
        try {
            mGravitationModel.load("", mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isFastLocationRequest) {
            mCallback.onInitialLocationIdentified(location);
            isFastLocationRequest = false;
            disconnectListener();
            return;
        }

        if (location.getAltitude() == 0) {
            mGpsCallback.onGpsLocationFound(location);
        } else {
            Location gpsLocation = fixGpsAltitude(location);
            mGpsCallback.onGpsLocationFound(gpsLocation);
        }
    }

    /**
     * Fixes altitude of the WGS84 ellipsoid by taking into consideration earth gravitational model.
     * Android GPS location uses WGS84 as a zero level, which isn't useful for most users.
     * @param location android GPS point to fix (location in WGS84 system)
     * @return location with corrected altitude by offset
     */
    private Location fixGpsAltitude(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        double altitudeWgs84 = location.getAltitude();

        // Calculate the offset between the ellipsoid and geoid of the surface
        double fixedWgs84 = altitudeWgs84 + fixByOffset(lat, lon, altitudeWgs84);
        Location fixedLocation = new Location(location);
        fixedLocation.setAltitude(fixedWgs84);
        return fixedLocation;
    }

    private double fixByOffset(double lat, double lon, double altitudeWgs84) {
        double altitudeOffset = 0;
        // Calculate the offset between the ellipsoid and geoid of the earth surface
        try {
            altitudeOffset = mGravitationModel.heightOffset(lat, lon, altitudeWgs84);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return altitudeOffset;
    }


    @Override
    public void startListenForLocations(FullInfoCallback callback) {
        isFastLocationRequest = false;
        LocationRequest locationRequest = new LocationRequest();
        locationRequest = setStandardLocationRequest(locationRequest);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        checkLocationPermissions(mContext, locationRequest);
    }

    @Override
    public void identifyCurrentLocation() {
        isFastLocationRequest = true;
        LocationRequest locationRequest = new LocationRequest();
        locationRequest = setFastLocationRequest(locationRequest);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        checkLocationPermissions(mContext, locationRequest);
    }

    @Override
    public void resetAllData() {
        //nothing to do, LocationUpdateManager deals with clear
    }

    @Override
    public void stopListenForLocations(boolean isLocked) {
        disconnectListener();
        if (isLocked) {
            mGoogleApiClient.disconnect();
        }
    }

    private void disconnectListener() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private LocationRequest setStandardLocationRequest(LocationRequest request) {
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(Constants.GPS_INTERVAL_VALUE);
        request.setFastestInterval(Constants.GPS_FASTEST_INTERVAL_VALUE);
        return request;
    }

    private LocationRequest setFastLocationRequest(LocationRequest request) {
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, " connection suspended triggered!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, " connection failed triggered!");
    }
}
