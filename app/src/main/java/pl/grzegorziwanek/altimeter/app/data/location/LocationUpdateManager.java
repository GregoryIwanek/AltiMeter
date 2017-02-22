package pl.grzegorziwanek.altimeter.app.data.location;

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
import android.widget.Toast;

import pl.grzegorziwanek.altimeter.app.data.Constants;
import pl.grzegorziwanek.altimeter.app.data.MyHandler;
import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;
import pl.grzegorziwanek.altimeter.app.data.location.model.BarometerAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.model.CombinedLocationModel;
import pl.grzegorziwanek.altimeter.app.data.location.model.GoogleMapAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.model.GpsAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.BarometerListener;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.GoogleMapsTask;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.GpsLocationListener;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.AddressService;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.NearestAirportTask;
import pl.grzegorziwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Created by ... on 01.02.2017.
 */

public class LocationUpdateManager implements LocationResponse {

    private static LocationUpdateManager INSTANCE = null;
    private LocationResponse mGpsLocationListener;
    private static FullInfoCallback callbackFullInfo;
    private LocationChangedCallback callbackNewLocation;
    private NetworkElevationCallback callbackNetwork;
    private AddressFoundCallback callbackAddress;
    private BarometerElevationCallback callbackBarometer;
    private AirportsCallback callbackAirport;
    private GpsElevationCallback callbackGps;
    private AddressResultReceiver mResultReceiver;
    private Context mContext;
    private static Session mSession = null;
    private Boolean mHasCallback = false;
    private BarometerListener mBarometerListener;
    private MyHandler handler;
    private Runnable mBarometerRunnable;
    private Runnable mNetworkRunnable;
    private Runnable mDataCombinedRunnable;

    private LocationUpdateManager(@NonNull Context context) {
        setCallbacks();
        setVariables(context);
        setManagers();
        setRunnable();
        mBarometerListener = BarometerListener.getInstance(context, callbackBarometer);
    }

    public static LocationUpdateManager getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocationUpdateManager(context);
        } else {
            INSTANCE.setNewSession();
        }
        return INSTANCE;
    }

    private void setVariables(Context context) {
        setNewSession();
        mContext = context;
        mGpsLocationListener = GpsLocationListener.getInstance(context, callbackNewLocation, callbackGps);
        mResultReceiver = new AddressResultReceiver(new Handler());
        handler = new MyHandler();
    }

    private void setRunnable() {
        mBarometerRunnable = new Runnable() {
            @Override
            public void run() {
                if (BarometerManager.isBarometerEnabled()) {
                    mBarometerListener.registerListener();
                }
            }
        };

        mNetworkRunnable = new Runnable() {
            @Override
            public void run() {
                if (NetworkManager.isNetworkEnabled()) {
                    GoogleMapsTask task = new GoogleMapsTask(callbackNetwork);
                    task.setLocationsStr(mSession.getCurrentLocation());
                    task.execute();
                }
            }
        };

        mDataCombinedRunnable = new Runnable() {
            @Override
            public void run() {
                callbackFullInfo.onFullInfoAcquired(mSession);
                handler.postDelayed(mDataCombinedRunnable, 20000);
            }
        };
    }

    private void setNewSession() {
        if (mSession != null) {
            System.out.println("SESSION IS NOT NULL!!!");
        }
        //TODO -> refactor here, analyse if Session need constructor with Title and Description parameters
        mSession = new Session("","");
    }

    /**
     * Callbacks used to communicate between THIS {@link LocationUpdateManager} and members:
     * {@link GpsLocationListener}   sends back updates of location,
     * {@link GoogleMapsTask}       sends back elevation of location,
     * {@link AddressService}     sends back closest address of location.
     */
    private void setCallbacks() {
        callbackNewLocation = new LocationChangedCallback() {
            @Override
            public void onNewLocationFound(Location location, boolean isHeightEmpty) {
                saveSessionsLocation(location);
                fetchCurrentElevation(location, isHeightEmpty);
                fetchAddress(location);
            }

            @Override
            public void onInitialLocationIdentified(Location location) {
                updateAirportInfo(location);
                mSession.setCurrLocation(location);
                if (GpsManager.isGpsEnabled() && location.getAltitude() != 0) {
                    GpsAltitudeModel.setAltitude(location.getAltitude());
                }
            }
        };

        callbackNetwork = new NetworkElevationCallback() {
            @Override
            public void onNetworkElevationFound(Double elevation) {
                appendLocationToList();
                checkIfHaveFullInfo();

                GoogleMapAltitudeModel.setAltitude(elevation);
                GoogleMapAltitudeModel.setMeasureTime(System.currentTimeMillis());

                elevation = FormatAndValueConverter.roundValue(elevation);
                String alt = String.valueOf(elevation);
                callbackFullInfo.onNetworkInfoAcquired(alt);

                CombinedLocationModel.updateCombinedAltitude();
                setCurrentElevation(CombinedLocationModel.getCombinedAltitude());
                handler.postDelayed(mNetworkRunnable, Constants.NETWORK_INTERVAL_VALUE);
                Toast.makeText(mContext, "NETWORK ELEVATION: " + String.valueOf(elevation), Toast.LENGTH_SHORT).show();
            }
        };

        callbackAddress = new AddressFoundCallback() {
            @Override
            public void onAddressFound(String address) {
                setAddress(address);
                checkIfHaveFullInfo();
            }
        };

        callbackAirport = new AirportsCallback() {
            @Override
            public void onNearestAirportsFound() {

            }
        };

        callbackGps = new GpsElevationCallback() {
            @Override
            public void onGpsLocationFound(Location location) {
                if (GpsManager.isGpsEnabled()) {
                    saveSessionsLocation(location);
                    appendLocationToList();
                    fetchAddress(location);
                    if (location.getAltitude() != 0) {
                        GpsAltitudeModel.setAltitude(location.getAltitude());
                        Double gpsAlt = FormatAndValueConverter.roundValue(location.getAltitude());
                        String alt = String.valueOf(gpsAlt);
                        callbackFullInfo.onGpsInfoAcquired(alt);
                        CombinedLocationModel.updateCombinedAltitude();
                    }
                }
            }
        };

        callbackBarometer = new BarometerElevationCallback() {
            @Override
            public void onBarometerElevationFound(Double barAltitude) {
                BarometerAltitudeModel.setAltitude(barAltitude);
                mBarometerListener.unregisterListener();
                barAltitude = FormatAndValueConverter.roundValue(barAltitude);
                String altitude = String.valueOf(barAltitude);
                callbackFullInfo.onBarometerInfoAcquired(altitude);
                CombinedLocationModel.updateCombinedAltitude();
                handler.postDelayed(mBarometerRunnable, 20000);
            }
        };
    }

    private void setManagers() {
        GpsManager.setGpsEnabled(false);
        NetworkManager.setNetworkEnabled(false);

        BarometerManager.setBarometerEnabled(false);
        readAirportUpdateLocation();
    }

    public void setManagerState(Class<?> manager, boolean isEnabled) {
        if (manager == GpsManager.class) {
            GpsManager.setGpsEnabled(isEnabled);
        } else if (manager == NetworkManager.class) {
            NetworkManager.setNetworkEnabled(isEnabled);
        } else if (manager == BarometerManager.class){
            BarometerManager.setBarometerEnabled(isEnabled);
        }
    }

    private void saveSessionsLocation(Location location) {
        //save old, previous location
        if (mSession.getCurrentLocation() != null) {
            mSession.setLastLocation(mSession.getCurrentLocation());
        }

        //save new, current location
        mSession.setCurrLocation(location);
    }

    private void fetchCurrentElevation(Location location, boolean isHeightEmpty) {
        if (isHeightEmpty) {
            GoogleMapsTask task = new GoogleMapsTask(callbackNetwork);
            task.setLocationsStr(location);
            task.execute();
        } else {
            //TODO-> refactor this part to better form
            double elevation = FormatAndValueConverter.roundValue(location.getAltitude());
            setCurrentElevation(elevation);
            checkIfHaveFullInfo();
        }
    }

    private void fetchAddress(Location location) {
        Intent intent = new Intent(mContext, AddressService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA, location);
        mContext.startService(intent);
    }

    private void fetchNearestAirports(Location location) {
        String airportRadialDistance = FormatAndValueConverter.setRadialDistanceString(
                location.getLatitude(), location.getLongitude());
        NearestAirportTask task = new NearestAirportTask(callbackAirport);
        task.setRadialDistanceStr(airportRadialDistance);
        //task.execute();
    }

    private void setCurrentElevation(Double elevation) {
        mSession.setCurrentElevation(elevation);
        mSession.getCurrentLocation().setAltitude(elevation);
    }

    private void appendLocationToList() {
        mSession.appendOneLocationPoint(mSession.getCurrentLocation());
    }

    private void setAddress(String address) {
        mSession.setAddress(address);
    }

    private void checkIfHaveFullInfo() {
        if (mHasCallback && mSession.getCurrentElevation() != null && mSession.getAddress() != null) {
            setFullInfoOfSession();
            callbackFullInfo.onFullInfoAcquired(mSession);
        }
    }

    private void setFullInfoOfSession() {
        setGeoCoordinateStr();
        setSessionsDistance();
        setSessionsHeight();

        Double alt = (double) 0;
        int count = 0;
        if (NetworkManager.isNetworkEnabled() && GoogleMapAltitudeModel.getAltitude() != 0) {
            alt += GoogleMapAltitudeModel.getAltitude();
            count++;
        }
        if (BarometerManager.isBarometerEnabled() && BarometerAltitudeModel.getAltitude() != 0) {
            alt += BarometerAltitudeModel.getAltitude();
            count++;
        }
        if (GpsManager.isGpsEnabled() && GpsAltitudeModel.getAltitude() != 0) {
            alt += GpsAltitudeModel.getAltitude();
            count++;
        }
        if (count != 0) {
            alt = alt/count;
            mSession.setCurrentElevation(FormatAndValueConverter.roundValue(alt));
            mSession.setElevationOnList(FormatAndValueConverter.roundValue(alt));
        }
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
    public void startListenForLocations(@Nullable FullInfoCallback callback) {
        callbackFullInfo = callback;
        mHasCallback = true;
        updateDistanceUnits();

        if (GpsManager.isGpsEnabled()) {
            mGpsLocationListener.startListenForLocations(null);
        }

        if (NetworkManager.isNetworkEnabled()) {
            fetchCurrentElevation(mSession.getCurrentLocation(), true);
            NetworkManager.setMeasureTime(mSession.getCurrentLocation().getTime());
        }

        if (BarometerManager.isBarometerEnabled()) {
            if (!mBarometerListener.isBarometerListenerRegistered()) {
                mBarometerListener.registerListener();
            }
        }

        handler.postDelayed(mDataCombinedRunnable, 20000);
    }

    @Override
    public void identifyCurrentLocation() {
        mGpsLocationListener.identifyCurrentLocation();
    }

    @Override
    public void stopListenForLocations(boolean isLocked) {
        mGpsLocationListener.stopListenForLocations(isLocked);
        mBarometerListener.unregisterListener();
        handler.removeCallbacks(mDataCombinedRunnable);
        if (isLocked) {
            lockSession();
        }
    }

    private void lockSession() {
        mSession.setLocked(true);
    }

    @Override
    public void clearSessionData() {
        mSession.clearData();
        callbackFullInfo.onFullInfoAcquired(mSession);
        handler.removeCallbacks(mBarometerRunnable);
        handler.removeCallbacks(mNetworkRunnable);
        handler.removeCallbacks(mDataCombinedRunnable);
    }

    private void updateDistanceUnits() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }

    private void updateAirportInfo(Location location) {
        if (isAirportUpdateRequired(location.getTime())) {
            BarometerManager.setAirportMeasureTime(location.getTime());
            saveAirportUpdateLocation(location);
            fetchNearestAirports(location);
        }
    }

    private boolean isAirportUpdateRequired(double currentTime) {
        return (currentTime - BarometerManager.getAirportMeasureTime()) > Constants.ONE_HOUR;
    }

    private void saveAirportUpdateLocation(Location location) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("measureTime", String.valueOf(location.getTime()));
        editor.putFloat("updateLatitude", (float) location.getLatitude());
        editor.putFloat("updateLongitude", (float) location.getLongitude());
        editor.apply();
    }

    private void readAirportUpdateLocation() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String str = sharedPref.getString("measureTime", "0");
        long time = Long.valueOf(str);
        float lat = sharedPref.getFloat("updateLatitude", 0);
        float lon = sharedPref.getFloat("updateLongitude", 0);
        BarometerManager.setAirportMeasureTime(time);
        BarometerManager.setUpdateLatitude(lat);
        BarometerManager.setUpdateLongitude(lon);
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


