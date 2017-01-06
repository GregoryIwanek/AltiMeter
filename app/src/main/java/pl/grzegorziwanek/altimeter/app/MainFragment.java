package pl.grzegorziwanek.altimeter.app;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.grzegorziwanek.altimeter.app.Map.MyMapFragment;

/**
 * Created by Grzegorz Iwanek on 23.11.2016.
 * Consist main UI fragment within, extension of Fragment;
 * Implements:
 * google's api location client (ConnectionCallbacks, OnConnectionFailedListener, LocationListener);
 * customized AsyncResponse interface (to return location data through AsyncTask's onPostExecute method);
 * inner class to catch data from AddressIntentService;
 * Uses ButcherKnife outer library;
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, AsyncResponse {

    public MainFragment() {}

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    public static final String PREFS_NAME = "MyPrefsFile";

    private GoogleApiClient mGoogleApiClient;
    private FetchDataInfoTask mFetchDataInfoTask;
    private static FormatAndValueConverter sFormatAndValueConverter;

    //variables to hold data as doubles and refactor them later into TextViews
    public Location mLastLocation;
    public ArrayList<Location> mLocationList;

    //TODO-> save these three variables in shared preferences ( values are reset after onResume is called)
    private double mMaxAltitudeValue;
    private double mMinAltitudeValue;
    private double mCurrentDistance;

    //ButterKnife
    //TextViews of View, fulled with refactored data from JSON objects and Google Play Service
    @BindView(R.id.current_elevation_label) TextView sCurrElevationTextView;
    @BindView(R.id.current_latitude_value) TextView sCurrLatitudeTextView;
    @BindView(R.id.current_longitude_value) TextView sCurrLongitudeTextView;
    @BindView(R.id.max_height_numbers) TextView sMaxElevTextView;
    @BindView(R.id.min_height_numbers) TextView sMinElevTextView;
    @BindView(R.id.location_label) TextView sCurrAddressTextView;
    @BindView(R.id.distance_numbers) TextView sDistanceTextView;
    @BindView(R.id.refresh_button) ImageButton sRefreshButton;
    @BindView(R.id.pause_button) ImageButton sPlayPauseButton;
    @BindView(R.id.map_fragment_button) ImageButton sMapFragmentButton;

    //graph view field
    @BindView(R.id.graph_view) GraphViewDrawTask graphViewDrawTask;
    private static AddressResultReceiver sResultReceiver;

    //map section
    private static MyMapFragment myMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initiateGooglePlayService();
        mLocationList = new ArrayList<>();
        sFormatAndValueConverter = new FormatAndValueConverter();
        mFetchDataInfoTask = new FetchDataInfoTask(this);
        sResultReceiver = new AddressResultReceiver(new Handler());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        setButtonsTags();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkActivityIsVisible()) {

            //check if last location is saved (prevent errors on first run of an app)
            if (mLastLocation != null) {
                startAddressIntentService(mLastLocation);
            }

            //redraw graph only when app backs from background, not when started first time (when altitude list is empty)
            if (!mLocationList.isEmpty()) {
                graphViewDrawTask.deliverGraph(mLocationList);
            }

            //update shared preferences values onResumed app
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Float sharedPrefMin = sharedPreferences.getFloat("CurrentMin", Constants.ALTITUDE_MIN);
            Float sharedPrefMax = sharedPreferences.getFloat("CurrentMax", Constants.ALTITUDE_MAX);
            Float sharedPrefDistance = sharedPreferences.getFloat("CurrentDistance", Constants.DISTANCE_DEFAULT);

            //update
            if (sharedPrefMin == Constants.ALTITUDE_MIN || sharedPrefMax == Constants.ALTITUDE_MAX) {
                mMinAltitudeValue = Constants.ALTITUDE_MIN;
                mMaxAltitudeValue = Constants.ALTITUDE_MAX;
                mCurrentDistance = Constants.DISTANCE_DEFAULT;
                Log.v(LOG_TAG, " Min and Max altitude not provided, default values are used instead...");
            } else {
                mMinAltitudeValue = sharedPrefMin;
                mMaxAltitudeValue = sharedPrefMax;
                mCurrentDistance = sharedPrefDistance;
                updateMinMaxAltitude(mLastLocation.getAltitude());
                updateDistanceUnits();
                updateDistanceTextView(mCurrentDistance);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, " onPause CALLED");
        updateSharedPreferences();
    }

    private void initiateGooglePlayService() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void setButtonsTags() {
        sRefreshButton.setTag(R.drawable.ic_refresh_white_18dp);
        sPlayPauseButton.setTag(R.drawable.ic_play_arrow_white_18dp);
    }

    @OnClick(R.id.pause_button)
    public void onPlayPauseButtonClick() {
        //on click pause play -> switch button image and perform play/pause action;
        if (sPlayPauseButton.getTag() != null) {
            //TODO->change condition from checking id of picture to different (connect somehow to styles)
            if (Integer.parseInt((sPlayPauseButton.getTag()).toString()) == R.drawable.ic_pause_white_18dp) {
                sPlayPauseButton.setBackgroundResource(R.drawable.ic_play_arrow_white_18dp);
                sPlayPauseButton.setTag(R.drawable.ic_play_arrow_white_18dp);
                Toast.makeText(this.getActivity(), "Paused", Toast.LENGTH_SHORT).show();

                System.out.println(Integer.parseInt((sPlayPauseButton.getTag()).toString()));
            } else {
                sPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_white_18dp);
                sPlayPauseButton.setTag(R.drawable.ic_pause_white_18dp);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest = setLocationRequest(locationRequest);
                checkPermissionsAndRequestUpdates(locationRequest);

                updateDistanceUnits();

                Toast.makeText(this.getActivity(), "Resumed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.v(LOG_TAG, "PAUSE BUTTON IMAGE TAG WAS NOT FOUND, ON CLICK OPERATION CANCELED");
        }
    }

    private void checkButtonTag(int buttonTag) {
        switch (buttonTag) {
            case R.drawable.ic_pause_white_18dp:
                setButtonTagAndImage(sPlayPauseButton, R.drawable.ic_play_arrow_white_18dp);
                break;
            case R.drawable.ic_play_arrow_white_18dp:
                setButtonTagAndImage(sPlayPauseButton, R.drawable.ic_pause_white_18dp);
                break;
            case R.drawable.ic_refresh_white_18dp:
                changePlayPauseButtonIcon();
            default:
                Log.d(LOG_TAG, " checkButtonTag: NO CORRECT BUTTON TAG PROVIDED");
                break;
        }
    }

    private void setButtonTagAndImage(ImageButton button, int imageId) {
        button.setBackgroundResource(imageId);
        button.setTag(imageId);
    }

    private boolean checkActivityIsVisible() {
        return this.getActivity() != null;
    }

    @OnClick(R.id.refresh_button)
    public void onRefreshButtonClick() {
        changePlayPauseButtonIcon();
        clearData();
    }

    private void changePlayPauseButtonIcon() {
        if (Integer.parseInt((sPlayPauseButton.getTag()).toString()) == R.drawable.ic_pause_white_18dp) {
            sPlayPauseButton.setBackgroundResource(R.drawable.ic_play_arrow_white_18dp);
            sPlayPauseButton.setTag(R.drawable.ic_play_arrow_white_18dp);
        }
    }

    private void clearData() {
        mLocationList.clear();
        mMaxAltitudeValue = Constants.ALTITUDE_MAX;
        mMinAltitudeValue = Constants.ALTITUDE_MIN;
        mCurrentDistance = Constants.DISTANCE_DEFAULT;
        sDistanceTextView.setText(Constants.DEFAULT_TEXT);
        sCurrAddressTextView.setText(Constants.DEFAULT_TEXT);
        sCurrElevationTextView.setText(Constants.DEFAULT_TEXT);
        sCurrLatitudeTextView.setText(Constants.DEFAULT_TEXT);
        sCurrLongitudeTextView.setText(Constants.DEFAULT_TEXT);
        sMaxElevTextView.setText(Constants.DEFAULT_TEXT);
        sMinElevTextView.setText(Constants.DEFAULT_TEXT);
        graphViewDrawTask.getSeries().clear();
    }

    @OnClick(R.id.map_fragment_button)
    public void onMapButtonClick() {
        setMapFragment();
        replaceFragmentWithMap();
    }

    private void setMapFragment() {
        if (myMapFragment == null) {
            myMapFragment = new MyMapFragment();
            myMapFragment.setListOfPoints(mLocationList);
            myMapFragment.updateMap();
        }
    }

    private void replaceFragmentWithMap() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_welcome_activity, myMapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //AsyncResponse interface methods (send data back to this activity from AsyncTask's onPostExecute method)
    @Override
    public void processAccurateElevation(Double elevation) {
        Log.v(LOG_TAG, " processAccurateElevation CALLED");

        updateMinMaxAltitude(elevation);

        String elevationStr = sFormatAndValueConverter.formatElevation(elevation);
        sCurrElevationTextView.setText(elevationStr);

        mLocationList.get(mLocationList.size()-1).setAltitude(elevation);
        graphViewDrawTask.deliverGraph(mLocationList);

        mLastLocation.setAltitude(elevation);
    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {

        String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            sCurrAddressTextView.setText(mAddressOutput);
        }
    }

    protected void startAddressIntentService(Location location) {
        Intent intent = new Intent(this.getActivity(), AddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, sResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        this.getActivity().startService(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public LocationRequest setLocationRequest(LocationRequest locationRequest) {
        //get preferences from app Settings screen (different from prefs file which contains current session data)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        String interval = sharedPreferences.getString("pref_sync_frequency_key", "5");
        Long intervalLong = Long.valueOf(interval);
        locationRequest.setInterval(intervalLong);

        if (intervalLong < 10000) {
            locationRequest.setFastestInterval(5000);
        } else {
            locationRequest.setFastestInterval(intervalLong/2);
        }

        //TODO-> in final version switch from comment to code
        //locationRequest.setSmallestDisplacement(5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    public void checkPermissionsAndRequestUpdates(LocationRequest locationRequest) {
        //check for location permissions Google Service
        //permissions has not been granted, ask for new one
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println(ActivityCompat.checkSelfPermission(this.getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION));
            System.out.println(ActivityCompat.checkSelfPermission(this.getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION));
            return;
        } else { //permissions has been granted, proceed
            //remove previous location request
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            //call new one
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            //update last location, in case there is incorrect old value
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //define location request of GooglePlayService
        LocationRequest locationRequest = new LocationRequest();
        locationRequest = setLocationRequest(locationRequest);

        //build location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        //check for location permissions Google Service and request location updates
        checkPermissionsAndRequestUpdates(locationRequest);

        //update TextViews with location, in case there is incorrect old value
        if (mLastLocation != null) {
            updateCurrentPosition(mLastLocation);
        }

        mLocationList.add(mLastLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        //TODO->remove part which is checking for "pause icon" and replace it with something else
        if (location != null && Integer.parseInt((sPlayPauseButton.getTag()).toString()) == R.drawable.ic_pause_white_18dp) {
            appendLocationToList(location);

            if (mLastLocation != null) {
                updateDistance(mLastLocation, location);
            }

            setGeoCoordinates(location);
            fetchCurrAltitude(location);

            if (checkActivityIsVisible()) {
                updateCurrentPosition(location);
                startAddressIntentService(location);
                updateSharedPreferences();
            }
        }
    }

    private void appendLocationToList(Location location) {
        mLocationList.add(location);
    }

    private void setGeoCoordinates(Location location) {
        mLastLocation.setLongitude(location.getLongitude());
        mLastLocation.setLatitude(location.getLatitude());
    }

    private void fetchCurrAltitude(Location location) {
        mFetchDataInfoTask = new FetchDataInfoTask(this);
        mFetchDataInfoTask.setLocationsStr(location);
        mFetchDataInfoTask.execute();
    }

    private void updateSharedPreferences() {
        Float latitude = (float) mLastLocation.getLatitude();
        Float longitude = (float) mLastLocation.getLongitude();
        Float altitude = (float) mLastLocation.getAltitude();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("CurrentLatitude", latitude);
        editor.putFloat("CurrentLongitude", longitude);
        editor.putFloat("CurrentAltitude", altitude);
        editor.putFloat("CurrentDistance", (float) mCurrentDistance);
        editor.putFloat("CurrentMin", (float) mMinAltitudeValue);
        editor.putFloat("CurrentMax", (float) mMaxAltitudeValue);
        editor.apply();
    }

    private void updateCurrentPosition(Location currLocation) {
        String latitudeStr = setLatLongStr(currLocation.getAltitude(), true);
        String longitudeStr = setLatLongStr(currLocation.getAltitude(), false);
        updateCurrPositionTextView(latitudeStr, longitudeStr);
    }

    private String setLatLongStr(Double currLocation, boolean isLatitude) {
        return sFormatAndValueConverter.setGeoCoordinateStr(currLocation, isLatitude);
    }

    private void updateCurrPositionTextView(String latitudeStr, String longitudeStr) {
        sCurrLatitudeTextView.setText(latitudeStr);
        sCurrLongitudeTextView.setText(longitudeStr);
    }

    private void updateMinMaxAltitude(double currAltitude) {
        updateMinMaxAltitudeValue(currAltitude);

        String minAltitudeStr = setMinMaxStr(mMinAltitudeValue);
        String maxAltitudeStr = setMinMaxStr(mMaxAltitudeValue);
        updateMinMaxTextView(minAltitudeStr, maxAltitudeStr);
    }

    private void updateMinMaxAltitudeValue(Double currElevation) {
        mMinAltitudeValue = sFormatAndValueConverter.updateMinAltitudeValue(currElevation, mMinAltitudeValue);
        mMaxAltitudeValue = sFormatAndValueConverter.updateMaxAltitudeValue(currElevation, mMaxAltitudeValue);
    }

    private String setMinMaxStr(double altValue) {
        return sFormatAndValueConverter.setMinMaxString(altValue);
    }

    private void updateMinMaxTextView(String minAltitudeStr, String maxAltitudeStr) {
        sMinElevTextView.setText(minAltitudeStr);
        sMaxElevTextView.setText(maxAltitudeStr);
    }

    private void updateDistance(Location lastLocation, Location currLocation) {
        if (lastLocation != null && currLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    currLocation.getLatitude(), currLocation.getLongitude(), results);
            mCurrentDistance += results[0];

            //TODO-> add in settings km m and miles to chose
            updateDistanceTextView(mCurrentDistance);
        }
    }

    private void updateDistanceTextView(Double currentDistance) {
        sDistanceTextView.setText(sFormatAndValueConverter.setDistanceStr(currentDistance));
    }

    private void updateDistanceUnits() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String units = sharedPreferences.getString("pref_set_units", "KILOMETERS");
        sFormatAndValueConverter.setUnitsFormat(units);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "Connection suspended, no location updates will be received");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(LOG_TAG, "Error occur, connection failed: " + connectionResult.getErrorMessage());
    }
}
