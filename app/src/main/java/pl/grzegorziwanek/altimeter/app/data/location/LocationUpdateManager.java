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

import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.Session;
import pl.grzegorziwanek.altimeter.app.data.StaticHandler;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.GpsManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.NetworkManager;
import pl.grzegorziwanek.altimeter.app.data.location.managers.models.BarometerAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.managers.models.CombinedLocationModel;
import pl.grzegorziwanek.altimeter.app.data.location.managers.models.GpsAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.managers.models.NetworkAltitudeModel;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.BarometerListener;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.GpsLocationListener;
import pl.grzegorziwanek.altimeter.app.data.location.services.elevation.NetworkTaskRx;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.AddressService;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.AirportsTaskRx;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.AirportsWithDataTaskRx;
import pl.grzegorziwanek.altimeter.app.data.location.services.helpers.airporttask.xmlparser.XmlAirportValues;
import pl.grzegorziwanek.altimeter.app.utils.Constants;
import pl.grzegorziwanek.altimeter.app.utils.FormatAndValueConverter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class LocationUpdateManager implements LocationResponse {

    private GpsManager mGpsManager;
    private NetworkManager mNetworkManager;
    private BarometerManager mBarometerManager;
    private GpsAltitudeModel mGpsAltitudeModel;
    private NetworkAltitudeModel mNetworkAltitudeModel;
    private BarometerAltitudeModel mBarometerAltitudeModel;
    private CombinedLocationModel mCombinedLocationModel;
    private SessionUpdateModel mSessionUpdateModel;
    private FullInfoCallback callbackFullInfo;
    private LocationChangedCallback callbackInitiation;
    private AddressFoundCallback callbackAddress;
    private GpsElevationCallback callbackGps;
    private AddressResultReceiver mResultReceiver;
    private LocationResponse mGpsLocationListener;
    private BarometerListener mBarometerListener;
    private Context mContext;
    private StaticHandler handler;
    private Runnable mBarometerRunnable;
    private Runnable mNetworkRunnable;
    private Runnable mDataCombinedRunnable;
    private Session mSession;
    private Subscription mBarometerSubscription;

    public LocationUpdateManager(@NonNull Context context) {
        setVariables(context);
        setCallbacks();
        setModels();
        setManagers();
        setSession();
        setListeners();
        setRunnable();
        setVariablesDependantOnOtherObject();
    }

    private void setVariables(Context context) {
        mContext = context;
        mCombinedLocationModel = new CombinedLocationModel();
        mResultReceiver = new AddressResultReceiver(new Handler());
        handler = new StaticHandler();
    }

    private void setCallbacks() {
        callbackInitiation = new LocationChangedCallback() {
            @Override
            public void onInitialLocationIdentified(Location location) {
                if (isAirportUpdateRequired(location.getTime())) {
                    updateAirportInfo(location);
                }
                if (mGpsManager.isGpsEnabled() && location.getAltitude() != 0) {
                    mGpsAltitudeModel.setAltitude(location.getAltitude());
                }
                mSession.setCurrLocation(location);
            }
        };

        callbackGps = new GpsElevationCallback() {
            @Override
            public void onGpsLocationFound(Location location) {
                mGpsManager.setMeasureTime(System.currentTimeMillis());
                mSessionUpdateModel.saveSessionsLocation(mSession, location);
                mSessionUpdateModel.appendLocationToList(mSession);
                fetchAddressService(location);

                if (isAirportUpdateRequired(location.getTime())) {
                    updateAirportInfo(location);
                }
                if (location.getAltitude() != 0) {
                    saveNonZeroGpsAltitude(location);
                    mSession.appendGraphPoint(mSession.getCurrentLocation().getTime(), mCombinedLocationModel.getCombinedAltitude());
                }
            }
        };

        callbackAddress = new AddressFoundCallback() {
            @Override
            public void onAddressFound(String address) {
                mSession.setAddress(address);
            }
        };
    }

    private void setModels() {
        mGpsAltitudeModel = new GpsAltitudeModel();
        mNetworkAltitudeModel = new NetworkAltitudeModel();
        mBarometerAltitudeModel = new BarometerAltitudeModel();
        mSessionUpdateModel = new SessionUpdateModel();
    }

    private void setManagers() {
        mGpsManager = new GpsManager();
        mNetworkManager = new NetworkManager();
        mBarometerManager = new BarometerManager();
        setMangersDisabled();

        // TODO: 22.03.2017 refactor that static
        // TODO: 22.03.2017 create method to assign airport pressure saved in preferences, AFTER initiation of mBarometerListener
        mSessionUpdateModel.readAirportUpdateLocation(mContext, mBarometerManager);
        mSessionUpdateModel.readAirportPressure(mContext, mBarometerManager);
    }

    private void setSession() {
        mSession = new Session("","");
        saveCurrentIdDrawerMapGeneration();
    }

    private void setListeners() {
        mGpsLocationListener = new GpsLocationListener(mContext, callbackInitiation, callbackGps);
        mBarometerListener = new BarometerListener(mContext);
    }

    private void setVariablesDependantOnOtherObject() {
        mBarometerListener.setClosestAirportPressure(mBarometerManager.getClosestAirportPressure());
    }

    private void setMangersDisabled() {
        mGpsManager.setGpsEnabled(false);
        mNetworkManager.setNetworkEnabled(false);
        mBarometerManager.setBarometerEnabled(false);
    }

    private void setRunnable() {
        mBarometerRunnable = new Runnable() {
            @Override
            public void run() {
                mBarometerListener.registerListener();
            }
        };

        mNetworkRunnable = new Runnable() {
            @Override
            public void run() {
                fetchCurrentElevationRx(mSession.getCurrentLocation());
            }
        };

        mDataCombinedRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO: 23.02.2017 set case when GPS is enabled, and other disabled (app crash)
                // TODO: 23.02.2017 set case when only Barometer is enabled (only altitude will be shown)
                // TODO: 01.03.2017 1-only barometer -> index out of bounds, refactor code to ignore lack of location
                mSession.getLocationList().get(mSession.getLocationList().size()-1).setAltitude(mCombinedLocationModel.getCombinedAltitude());
                mCombinedLocationModel.setUpdateTime(System.currentTimeMillis());

                // TODO: 27.03.2017 possible place which produces first Y as zero
                mSession.appendGraphPoint(mCombinedLocationModel.getUpdateTime(), mCombinedLocationModel.getCombinedAltitude());

                mSessionUpdateModel.setCurrentElevation(mSession, mCombinedLocationModel);
                setTextViewStrings();
                callbackFullInfo.onFullInfoAcquired(mSession);
                handler.postDelayed(mDataCombinedRunnable, 20000);
            }
        };
    }

    private void fetchBarometerAltitudeRx() {
        if (mBarometerSubscription == null) {
            mBarometerSubscription = mBarometerListener.getPressureAltitudePublishSubject()
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Double>() {
                        @Override
                        public void onCompleted() {
                            this.unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onNext(Double barAltitude) {
                            mBarometerListener.unregisterListener();
                            mBarometerAltitudeModel.setAltitude(barAltitude);
                            barAltitude = FormatAndValueConverter.roundValue(barAltitude);
                            callbackFullInfo.onBarometerInfoAcquired(String.valueOf(barAltitude));
                            handler.postDelayed(mBarometerRunnable, 20000);
                            updateCombinedLocationAltitude();
                        }
                    });
        }
    }

    public Observable<Boolean> isSessionEmptyObservable() {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(isSessionEmpty());
            }
        });
    }

    private boolean isSessionEmpty() {
        return mSession.getLocationList() != null && mSession.getLocationList().size() < 1;
    }

    private void fetchCurrentElevationRx(Location location) {
        NetworkTaskRx taskRx = new NetworkTaskRx(location);
        taskRx.getElevationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Double>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Double elevation) {
                        elevation = (double) Math.round(elevation);
                        elevation = FormatAndValueConverter.roundValue(elevation);

                        mSessionUpdateModel.appendLocationToList(mSession);

                        //setTextViewStrings();

                        mNetworkAltitudeModel.setAltitude(elevation);
                        mNetworkAltitudeModel.setMeasureTime(System.currentTimeMillis());

                        if (!mGpsManager.isGpsEnabled() || isAddressUpdateRequired(System.currentTimeMillis())) {
                            fetchAddressService(mSession.getCurrentLocation());
                        }

                        String alt = String.valueOf(elevation);
                        callbackFullInfo.onNetworkInfoAcquired(alt);

                        updateCombinedLocationAltitude();
                        mSessionUpdateModel.setCurrentElevation(mSession, mCombinedLocationModel);
                        handler.postDelayed(mNetworkRunnable, Constants.NETWORK_INTERVAL_VALUE);

                        if (!mGpsManager.isGpsEnabled() || isAddressUpdateRequired(System.currentTimeMillis())) {
                            identifyCurrentLocation();
                        }
                        if (isAirportUpdateRequired(mSession.getCurrentLocation().getTime())) {
                            updateAirportInfo(mSession.getCurrentLocation());
                        }
                    }
                });
    }

    private void fetchNearestAirportsRx(Location location) {
        String airportRadialDistance = FormatAndValueConverter.setRadialDistanceString(
                location.getLatitude(), location.getLongitude());

        AirportsTaskRx taskRx = new AirportsTaskRx();
        taskRx.setRadialDistanceStr(airportRadialDistance);
        taskRx.getNearestAirportsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<XmlAirportValues>>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(List<XmlAirportValues> values) {
                        mBarometerManager.setAirportsList(values);
                        fetchAirportsPressureRx();
                    }
                });
    }

    private void fetchAirportsPressureRx() {
        String airportsSymbols =
                FormatAndValueConverter.getAirportsSymbolString(mBarometerManager.getAirportsList());

        AirportsWithDataTaskRx taskRx = new AirportsWithDataTaskRx();
        taskRx.setStationsString(airportsSymbols);
        taskRx.getAirportsWithDataObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<XmlAirportValues>>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(List<XmlAirportValues> xmlAirportValues) {
                        mBarometerManager.setAirportsList(xmlAirportValues);
                        assignAirportPressure();
                        BarometerManager.resetList();
                    }
                });
    }

    private void fetchAddressService(Location location) {
        Intent intent = new Intent(mContext, AddressService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA, location);
        mContext.startService(intent);
    }

    public void onActivityDestroyed() {
        updateStatisticsOnDestroy();
        unsubscribeOnDestroy();
        resetCurrentIdDrawerMapGeneration();
        resetAllData();
    }

    private void updateStatisticsOnDestroy() {
        mSessionUpdateModel.updateGlobalStatistics(mContext, mSession);
    }

    private void unsubscribeOnDestroy() {
        if (mBarometerSubscription != null) {
            mBarometerSubscription.unsubscribe();
            mBarometerSubscription = null;
        }
    }

    private void setTextViewStrings() {
        mSessionUpdateModel.setCurrentElevation(mSession, mCombinedLocationModel);
        mSessionUpdateModel.setElevationOnList(mSession, mCombinedLocationModel);
        mSessionUpdateModel.setGeoCoordinateStr(mSession);
        mSessionUpdateModel.setSessionsDistance(mSession);
        mSessionUpdateModel.setSessionsHeight(mSession);
    }

    private void saveNonZeroGpsAltitude(Location location) {
        mGpsAltitudeModel.setAltitude(location.getAltitude());
        updateCombinedLocationAltitude();
        Double gpsAlt = FormatAndValueConverter.roundValue(location.getAltitude());
        callbackFullInfo.onGpsInfoAcquired(String.valueOf(gpsAlt));
    }

    private void updateCombinedLocationAltitude() {
        boolean[] stateOfProviders = getStateOfProviders();
        double[] modelsAltitude = getProvidersAltitudes();
        mCombinedLocationModel.updateCombinedAltitude(stateOfProviders, modelsAltitude);
    }

    private boolean[] getStateOfProviders() {
        return new boolean[]{mGpsManager.isGpsEnabled(), mNetworkManager.isNetworkEnabled(),
                mBarometerManager.isBarometerEnabled()};
    }

    private double[] getProvidersAltitudes() {
        return new double[]{mGpsAltitudeModel.getAltitude(), mNetworkAltitudeModel.getAltitude(),
                mBarometerAltitudeModel.getAltitude()};
    }

    private void assignAirportPressure() {
        float pressure = FormatAndValueConverter.fetchAirportPressure(mBarometerManager.getAirportsList(),
                mBarometerManager.getUpdateLatitude(), mBarometerManager.getUpdateLongitude());
        mBarometerManager.setClosestAirportPressure(pressure);
        mBarometerListener.setClosestAirportPressure(pressure);
        mSessionUpdateModel.saveAirportPressure(mContext, mBarometerManager);
    }

    @Override
    public void startListenForLocations(@Nullable FullInfoCallback callback) {
        callbackFullInfo = callback;
        mSessionUpdateModel.updateDistanceUnits(mContext);

        if (mGpsManager.isGpsEnabled()) {
            mGpsLocationListener.startListenForLocations(null);
        }

        if (mNetworkManager.isNetworkEnabled()) {
            fetchCurrentElevationRx(mSession.getCurrentLocation());
            mNetworkManager.setMeasureTime(mSession.getCurrentLocation().getTime());
        }

        if (mBarometerManager.isBarometerEnabled()) {
            if (!mBarometerListener.isBarometerListenerRegistered()) {
                fetchBarometerAltitudeRx();
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
        resetHandlers();
        if (isLocked) {
            lockSession();
        }
    }

    private void lockSession() {
        mSession.setLocked(true);
    }

    public void setManagerState(Class<?> manager, boolean isEnabled) {
        if (manager == GpsManager.class) {
            mGpsManager.setGpsEnabled(isEnabled);
        } else if (manager == NetworkManager.class) {
            mNetworkManager.setNetworkEnabled(isEnabled);
        } else if (manager == BarometerManager.class){
            mBarometerManager.setBarometerEnabled(isEnabled);
        }
    }

    @Override
    public void resetAllData() {
        if (mSession != null && callbackFullInfo != null) {
            resetTextViews();
            resetHandlers();
            resetManagers();

            identifyCurrentLocation();
        }
    }

    private void resetTextViews() {
        resetSessionTextViews();
        resetElevationSourceTextViews();
    }

    private void resetSessionTextViews() {
        mSession.clearData();
        callbackFullInfo.onFullInfoAcquired(mSession);
    }

    private void resetElevationSourceTextViews() {
        callbackFullInfo.onGpsInfoAcquired(Constants.DEFAULT_TEXT);
        callbackFullInfo.onNetworkInfoAcquired(Constants.DEFAULT_TEXT);
        callbackFullInfo.onBarometerInfoAcquired(Constants.DEFAULT_TEXT);
    }

    private void resetHandlers() {
        handler.removeCallbacks(mBarometerRunnable);
        handler.removeCallbacks(mNetworkRunnable);
        handler.removeCallbacks(mDataCombinedRunnable);
    }

    private void resetManagers() {
        mGpsManager.resetData();
        mNetworkManager.resetData();
        mBarometerManager.resetData();
    }

    private void updateAirportInfo(Location location) {
        mBarometerManager.setAirportMeasureTime(location.getTime());
        mSessionUpdateModel.saveAirportUpdateLocation(location, mContext);
        fetchNearestAirportsRx(location);
    }

    private boolean isAirportUpdateRequired(double currentTime) {
        return (currentTime - mBarometerManager.getAirportMeasureTime()) > Constants.HALF_HOUR;
    }

    private boolean isAddressUpdateRequired(double currentTime) {
        return (currentTime - mGpsManager.getMeasureTime()) > Constants.TWENTY_SECONDS;
    }

    public Session getSession() {
        return mSession;
    }

    private void saveCurrentIdDrawerMapGeneration() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sessionId", mSession.getId());
        editor.apply();
    }

    private void resetCurrentIdDrawerMapGeneration() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sessionId", Constants.DEFAULT_TEXT);
        editor.apply();
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