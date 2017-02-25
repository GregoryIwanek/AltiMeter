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

import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.StaticHandler;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;
import pl.grzegorziwanek.altimeter.app.data.location.model.BarometerAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.model.CombinedLocationModel;
import pl.grzegorziwanek.altimeter.app.data.location.model.GpsAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.model.NetworkAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.BarometerListener;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.GpsLocationListener;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.NetworkTask;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.AddressService;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.PressureAirportTask;
import pl.grzegorziwanek.altimeter.app.utils.Constants;
import pl.grzegorziwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Created by ... on 01.02.2017.
 */

public class LocationUpdateManager implements LocationResponse {
    private static LocationUpdateManager INSTANCE = null;
    private static FullInfoCallback callbackFullInfo;
    private LocationChangedCallback callbackInitiation;
    private NetworkElevationCallback callbackNetwork;
    private AddressFoundCallback callbackAddress;
    private BarometerElevationCallback callbackBarometer;
    private AirportsCallback callbackAirport;
    private GpsElevationCallback callbackGps;
    private AddressResultReceiver mResultReceiver;
    private LocationResponse mGpsLocationListener;
    private BarometerListener mBarometerListener;
    private Context mContext;
    private Boolean mHasCallback = false;
    private StaticHandler handler;
    private Runnable mBarometerRunnable;
    private Runnable mNetworkRunnable;
    private Runnable mDataCombinedRunnable;
    private static Session mSession = null;

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
            //INSTANCE.setNewSession();
            INSTANCE = new LocationUpdateManager(context);
        }
        return INSTANCE;
    }

    private void setVariables(Context context) {
        setNewSession();
        mContext = context;
        mGpsLocationListener = GpsLocationListener.getInstance(context, callbackInitiation, callbackGps);
        mResultReceiver = new AddressResultReceiver(new Handler());
        handler = new StaticHandler();
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
                    NetworkTask task = new NetworkTask(callbackNetwork);
                    task.setLocationsStr(mSession.getCurrentLocation());
                    task.execute();
                }
            }
        };

        mDataCombinedRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO: 23.02.2017 set case when GPS is enabled, and other disabled (app crash)
                // TODO: 23.02.2017 set case when GPS is disabled and network is enabled (call of "identify position" every 30sec)
                // TODO: 23.02.2017 set case when only Barometer is enabled (only altitude will be shown)
                mSession.getLocationList().get(mSession.getLocationList().size()-1).setAltitude(CombinedLocationModel.getCombinedAltitude());
                CombinedLocationModel.setUpdateTime(System.currentTimeMillis());
                mSession.appendGraphPoint(CombinedLocationModel.getUpdateTime(), CombinedLocationModel.getCombinedAltitude());
                callbackFullInfo.onFullInfoAcquired(mSession);
                handler.postDelayed(mDataCombinedRunnable, 20000);
            }
        };
    }

    private void setNewSession() {
        mSession = new Session("","");
    }

    /**
     * Callbacks used to communicate between THIS {@link LocationUpdateManager} and members:
     * {@link GpsLocationListener}   sends back updates of location,
     * {@link NetworkTask}       sends back elevation of location,
     * {@link AddressService}     sends back closest address of location.
     */
    private void setCallbacks() {
        callbackInitiation = new LocationChangedCallback() {
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
                setTextViewStrings();

                NetworkAltitudeModel.setAltitude(elevation);
                NetworkAltitudeModel.setMeasureTime(System.currentTimeMillis());

                elevation = FormatAndValueConverter.roundValue(elevation);
                String alt = String.valueOf(elevation);
                callbackFullInfo.onNetworkInfoAcquired(alt);

                CombinedLocationModel.updateCombinedAltitude();
                setCurrentElevation(CombinedLocationModel.getCombinedAltitude());
                handler.postDelayed(mNetworkRunnable, Constants.NETWORK_INTERVAL_VALUE);

                if (!GpsManager.isGpsEnabled()) {
                    identifyCurrentLocation();
                }
            }
        };

        callbackAddress = new AddressFoundCallback() {
            @Override
            public void onAddressFound(String address) {
                setAddress(address);
            }
        };

        callbackAirport = new AirportsCallback() {
            @Override
            public void onNearestAirportsFound() {
                if (!isAirportsListEmpty()) {
                    fetchAirportsPressure();
                }
            }

            @Override
            public void onAirportPressureFound() {
                FormatAndValueConverter.setAirportsDistance(BarometerManager.getAirportsList(),
                        BarometerManager.getUpdateLatitude(), BarometerManager.getUpdateLongitude());
                FormatAndValueConverter.sortAirportsByDistance(BarometerManager.getAirportsList());
                float pressure = FormatAndValueConverter.getClosestAirportPressure(BarometerManager.getAirportsList());
                pressure = FormatAndValueConverter.convertHgPressureToHPa(pressure);
                BarometerManager.setClosestAirportPressure(pressure);
                saveAirportPressure();
                BarometerManager.resetList();
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
                    mSession.appendGraphPoint(mSession.getCurrentLocation().getTime(), CombinedLocationModel.getCombinedAltitude());
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
        readAirportPressure();
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
            NetworkTask task = new NetworkTask(callbackNetwork);
            task.setLocationsStr(location);
            task.execute();
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
        PressureAirportTask task = new PressureAirportTask(callbackAirport);
        task.setFetchingStations(true);
        task.setRadialDistanceStr(airportRadialDistance);
        task.execute();
    }

    private void fetchAirportsPressure() {
        String airportsSymbols =
                FormatAndValueConverter.getAirportsSymbolString(BarometerManager.getAirportsList());
        PressureAirportTask task = new PressureAirportTask(callbackAirport);
        task.setFetchingStations(false);
        task.setStationsString(airportsSymbols);
        task.execute();
    }

    private boolean isAirportsListEmpty() {
        return BarometerManager.getAirportsList().isEmpty();
    }

    private void setCurrentElevation(Double elevation) {
        mSession.setCurrentElevation(elevation);
        mSession.getCurrentLocation().setAltitude(elevation);
    }

    private void appendLocationToList() {
        mSession.appendLocationPoint(mSession.getCurrentLocation());
    }

    private void setAddress(String address) {
        mSession.setAddress(address);
    }

    private void checkIfHaveFullInfo() {
        if (mHasCallback && mSession.getCurrentElevation() != null && mSession.getAddress() != null) {
            //setFullInfoOfSession();
            callbackFullInfo.onFullInfoAcquired(mSession);
        }
    }

    private void setFullInfoOfSession() {
        setGeoCoordinateStr();
        setSessionsDistance();
        setSessionsHeight();

        Double alt = (double) 0;
        int count = 0;
        if (NetworkManager.isNetworkEnabled() && NetworkAltitudeModel.getAltitude() != 0) {
            alt += NetworkAltitudeModel.getAltitude();
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

    private void setTextViewStrings() {
        mSession.setCurrentElevation(FormatAndValueConverter.roundValue(CombinedLocationModel.getCombinedAltitude()));
        mSession.setElevationOnList(FormatAndValueConverter.roundValue(CombinedLocationModel.getCombinedAltitude()));
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

        handler.postDelayed(mDataCombinedRunnable, 10000);
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
        removeHandlers();
        resetDataSourceTextView();
        resetListeners();
    }

    private void removeHandlers() {
        handler.removeCallbacks(mBarometerRunnable);
        handler.removeCallbacks(mNetworkRunnable);
        handler.removeCallbacks(mDataCombinedRunnable);
    }

    private void resetDataSourceTextView() {
        callbackFullInfo.onGpsInfoAcquired(Constants.DEFAULT_TEXT);
        callbackFullInfo.onNetworkInfoAcquired(Constants.DEFAULT_TEXT);
        callbackFullInfo.onBarometerInfoAcquired(Constants.DEFAULT_TEXT);
    }

    private void resetListeners() {
        GpsManager.resetData();
        NetworkManager.resetData();
        BarometerManager.resetData();
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
        return (currentTime - BarometerManager.getAirportMeasureTime()) > Constants.HALF_HOUR;
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

    private void saveAirportPressure() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("airportPressure", String.valueOf(BarometerManager.getClosestAirportPressure()));
        editor.apply();
    }

    private void readAirportPressure() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String pressureStr = sharedPref.getString("airportPressure", "0");
        double pressure = Double.valueOf(pressureStr);
        BarometerManager.setClosestAirportPressure(pressure);
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


