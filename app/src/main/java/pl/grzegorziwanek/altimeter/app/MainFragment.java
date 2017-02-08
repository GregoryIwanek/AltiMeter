package pl.grzegorziwanek.altimeter.app;

import android.annotation.SuppressLint;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
 * Created by XXX XXX on 23.11.2016.
 * Consist main UI fragment within, extension of Fragment;
 * Implements:
 * google's api location client (ConnectionCallbacks, OnConnectionFailedListener, LocationListener);
 * customized CallbackResponse interface (to return location data through AsyncTask's onPostExecute method);
 * inner class to catch data from AddressIntentServicee;
 * Uses ButcherKnife outer library;
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, AsyncResponse {

    public MainFragment() {}

    public static MainFragment newInstance() {
        return new MainFragment();
    }

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
    @BindView(R.id.reset_button) ImageButton sRefreshButton;
    @BindView(R.id.pause_button) ImageButton sPlayPauseButton;
    @BindView(R.id.map_fragment_button) ImageButton sMapFragmentButton;

    //GraphView section
    @BindView(R.id.graph_view) GraphViewDrawTask graphViewDrawTask;
    private static AddressResultReceiver sResultReceiver;

    //map section
    private static MyMapFragment myMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initiateGooglePlayService();
        initiateVariables();
    }

    @Override
    public void onStart() {
        super.onStart();
        connectGoogleAPIClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        initiateButtonsTags();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, " onPause CALLED");
        updateSharedPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateOnResume();
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
            mLocationList.add(mLastLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        //TODO->remove part which is checking for "pause icon" and replace it with something else
//        if (location != null && Integer.parseInt((sPlayPauseButton.getTag()).toString()) == R.drawable.ic_pause_white_18dp) {
//            appendLocationToList(location);
//
//            if ((Double) mLastLocation.getAltitude() != null) {
//                updateDistance(mLastLocation, location);
//            }
//
//            if (mLastLocation != null) {
//                setGeoCoordinates(location);
//            }
//            fetchCurrAltitude(location);
//
//            if (checkActivityIsVisible()) {
//                updateCurrentPosition(location);
//                startAddressIntentService(location);
//                updateSharedPreferences();
//            }
//        }
    }

    //CallbackResponse interface methods (send data back to this activity from AsyncTask's onPostExecute method)
    @Override
    public void processAccurateElevation(Double elevation) {
        updateMinMaxAltitude(elevation);

        String elevationStr = sFormatAndValueConverter.formatElevation(elevation);
        sCurrElevationTextView.setText(elevationStr);

        mLocationList.get(mLocationList.size()-1).setAltitude(elevation);
        graphViewDrawTask.deliverGraph(mLocationList);

        mLastLocation.setAltitude(elevation);
    }

    //!!!
    @OnClick(R.id.pause_button)
    public void onPlayPauseButtonClick() {
        //on click pause play -> switch button image and perform play/pause action;
//        if (sPlayPauseButton.getTag() != null) {
//            //TODO->change condition from checking id of picture to different (connect somehow to styles)
//            if (Integer.parseInt((sPlayPauseButton.getTag()).toString()) == R.drawable.ic_pause_white_18dp) {
//                sPlayPauseButton.setBackgroundResource(R.drawable.ic_play_arrow_white_18dp);
//                sPlayPauseButton.setTag(R.drawable.ic_play_arrow_white_18dp);
//                Toast.makeText(this.getActivity(), "Paused", Toast.LENGTH_SHORT).show();
//
//                System.out.println(Integer.parseInt((sPlayPauseButton.getTag()).toString()));
//            } else {
////                sPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_white_18dp);
////                sPlayPauseButton.setTag(R.drawable.ic_pause_white_18dp);
//
//                LocationRequest locationRequest = new LocationRequest();
//                locationRequest = setLocationRequest(locationRequest);
//                checkPermissionsAndRequestUpdates(locationRequest);
//
//                updateDistanceUnits();
//
//                Toast.makeText(this.getActivity(), "Resumed", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Log.v(LOG_TAG, "PAUSE BUTTON IMAGE TAG WAS NOT FOUND, ON CLICK OPERATION CANCELED");
//        }
    }

    //!!!
    @OnClick(R.id.reset_button)
    public void onRefreshButtonClick() {
        changePlayPauseButtonIcon();
        clearData();
    }

    //!!!
    @OnClick(R.id.map_fragment_button)
    public void onMapButtonClick() {
        setMapFragment();
        replaceFragmentWithMap();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    private void updateOnResume() {
        if (checkActivityIsVisible()) {
            updateDistanceUnits();
            runAddressIntentService();
            runGraphDrawing();
            updatePreferencesOnResume();
        }
    }

    private boolean checkActivityIsVisible() {
        return this.getActivity() != null;
    }

    private void runAddressIntentService() {
        if (mLastLocation != null) {
            startAddressIntentService(mLastLocation);
        }
    }

    private void runGraphDrawing() {
        if (!mLocationList.isEmpty()) {
            graphViewDrawTask.deliverGraph(mLocationList);
        }
    }

    private void updatePreferencesOnResume() {
        if (isPrefValueProvided()) {
            updateValuesOnResume();
        } else {
            resetValues();
        }
    }

    private boolean isPrefValueProvided() {
        SharedPreferences sharedPreferences = getMyPreferences();
        return !(getPreferencesFloat(sharedPreferences, "CurrentMin") == Constants.ALTITUDE_MIN
                || getPreferencesFloat(sharedPreferences, "CurrentMax") == Constants.ALTITUDE_MAX);
    }

    private void updateValuesOnResume() {
        updateValues();
        mLastLocation.setAltitude(1000);
        if ((Double) mLastLocation.getAltitude() != null) {
            updateMinMaxAltitude(mLastLocation.getAltitude());
        }
        updateDistanceUnits();
        updateDistanceTextView(mCurrentDistance);
    }

    private void updateValues() {
        SharedPreferences sharedPreferences = getMyPreferences();
        mMinAltitudeValue = getPreferencesFloat(sharedPreferences, "CurrentMin");
        mMaxAltitudeValue = getPreferencesFloat(sharedPreferences, "CurrentMax");
        mCurrentDistance = getPreferencesFloat(sharedPreferences, "CurrentDistance");
    }

    private void resetValues() {
        mMinAltitudeValue = Constants.ALTITUDE_MIN;
        mMaxAltitudeValue = Constants.ALTITUDE_MAX;
        mCurrentDistance = Constants.DISTANCE_DEFAULT;
        Log.v(LOG_TAG, " Min and Max altitude not provided, default values are used instead...");
    }

    private SharedPreferences getMyPreferences() {
        return getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    }

    private Float getPreferencesFloat(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "CurrentMin":
                return sharedPreferences.getFloat("CurrentMin", Constants.ALTITUDE_MIN);
            case "CurrentMax":
                return sharedPreferences.getFloat("CurrentMax", Constants.ALTITUDE_MAX);
            case "CurrentDistance":
                return sharedPreferences.getFloat("CurrentDistance", Constants.DISTANCE_DEFAULT);
            default:
                return (float) Constants.DISTANCE_DEFAULT;
        }
    }

    private String getPreferencesString(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_sync_frequency_key":
                return sharedPreferences.getString("pref_sync_frequency_key", "5");
            case "pref_set_units":
                return sharedPreferences.getString("pref_set_units", "KILOMETERS");
            default:
                return Constants.DEFAULT_TEXT;
        }
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

    private void initiateVariables() {
        mLocationList = new ArrayList<>();
        sFormatAndValueConverter = new FormatAndValueConverter();
        mFetchDataInfoTask = new FetchDataInfoTask(this);
        sResultReceiver = new AddressResultReceiver(new Handler());
    }

    private void connectGoogleAPIClient() {
        mGoogleApiClient.connect();
    }

    private void initiateButtonsTags() {
//        if (isButtonTagUndefined()) {
//            sRefreshButton.setTag(R.drawable.ic_refresh_white_18dp);
//
//            if (isPlayImageOnButton()) {
//                sPlayPauseButton.setTag(R.drawable.ic_play_arrow_white_18dp);
//            } else {
//                sPlayPauseButton.setTag(R.drawable.ic_pause_white_18dp);
//            }
//        }
    }

    private boolean isButtonTagUndefined() {
        return sPlayPauseButton.getTag() == null || sRefreshButton.getTag() == null;
    }

    private boolean isPlayImageOnButton() {
//        return sPlayPauseButton.getBackground().equals(
//                ContextCompat.getDrawable(getActivity(), R.drawable.ic_play_arrow_white_18dp));
        return true;
    }

    //TODO
    private void checkButtonTag(int buttonTag) {
//        switch (buttonTag) {
//            case R.drawable.ic_pause_white_18dp:
//                setButtonTagAndImage(sPlayPauseButton, R.drawable.ic_play_arrow_white_18dp);
//                break;
//            case R.drawable.ic_play_arrow_white_18dp:
//                setButtonTagAndImage(sPlayPauseButton, R.drawable.ic_pause_white_18dp);
//                break;
//            case R.drawable.ic_refresh_white_18dp:
//                changePlayPauseButtonIcon();
//            default:
//                Log.d(LOG_TAG, " checkButtonTag: NO CORRECT BUTTON TAG PROVIDED");
//                break;
//        }
    }

    //TODO
    private void setButtonTagAndImage(ImageButton button, int imageId) {
        button.setBackgroundResource(imageId);
        button.setTag(imageId);
    }

    private void changePlayPauseButtonIcon() {
//        if (Integer.parseInt((sPlayPauseButton.getTag()).toString()) == R.drawable.ic_pause_white_18dp) {
//            sPlayPauseButton.setBackgroundResource(R.drawable.ic_play_arrow_white_18dp);
//            sPlayPauseButton.setTag(R.drawable.ic_play_arrow_white_18dp);
//        }
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

    private void setMapFragment() {
        if (myMapFragment == null) {
            myMapFragment = new MyMapFragment();
            myMapFragment.setListOfPoints(mLocationList);
            myMapFragment.updateMap();
        }
    }

    private void replaceFragmentWithMap() {
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.screen_welcome_activity, myMapFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }

    protected void startAddressIntentService(Location location) {
        Intent intent = new Intent(this.getActivity(), AddressIntentServicee.class);
        intent.putExtra(Constants.RECEIVER, sResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        this.getActivity().startService(intent);
    }

    public LocationRequest setLocationRequest(LocationRequest locationRequest) {
        //get preferences from app Settings screen (different from prefs file which contains current session data)
        SharedPreferences sharedPreferences = getDefaultPreferences();

        String interval = getPreferencesString(sharedPreferences, "pref_sync_frequency_key");
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
        { //TODO-> finish this part (inside of "if"
        } else { //permissions has been granted, proceed
            //remove previous location request
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            //call new one
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            //update last location, in case there is incorrect old value
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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

        SharedPreferences sharedPreferences = getMyPreferences();
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
        mMinAltitudeValue =
                sFormatAndValueConverter.updateMinAltitudeValue(currElevation, mMinAltitudeValue);
        mMaxAltitudeValue =
                sFormatAndValueConverter.updateMaxAltitudeValue(currElevation, mMaxAltitudeValue);
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
        SharedPreferences sharedPreferences = getDefaultPreferences();
        String units = getPreferencesString(sharedPreferences, "pref_set_units");
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
