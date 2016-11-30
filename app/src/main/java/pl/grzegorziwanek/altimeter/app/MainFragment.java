package pl.grzegorziwanek.altimeter.app;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

/**
 * Created by Grzegorz Iwanek on 23.11.2016.
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    public MainFragment() {
    }

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    //TODO-> assign location update to settings, not here (ONE_MINUTE variable)
    private static final int FIVE_SECONDS = 1000*5;

    private GoogleApiClient mGoogleApiClient;
    private FetchDataInfoTask mFetchDataInfoTask;
    private static DataFormatConverter sDataFormatConverter;

    //variables to hold data as doubles and refactor them later into TextViews
    private double mCurrentEleValue;
    private double mCurrentLngValue;
    private double mCurrentLatValue;
    private double mMaxAltitdeValue = -10000;
    private double mMinAltitudeValue = 10000;

    //TextViews of View, fulled with refactored data from JSON objects and Google Play Service
    private static TextView sCurrElevationTextView;
    private static TextView sCurrLatitudeTextView;
    private static TextView sCurrLongitudeTextView;
    private static TextView sMaxElevTextView;
    private static TextView sMinElevTextView;
    private static TextView sCurrAddressTextView;

    private AddressResultReceiver addressResultReceiver;

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver
    {
        String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);


            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initiate google play service ( used to update device's location in given intervals)
        initiateGooglePlayService();
    }

    //consist actions to perform upon re/start of app ( update current location and information)
    @Override
    public void onStart()
    {
        //connect google play service and get current location
        mGoogleApiClient.connect();

        super.onStart();

        //refresh info on screen on app start/restart
        updateAppInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //assign UI elements to inner variables
        sCurrElevationTextView = (TextView) rootView.findViewById(R.id.current_elevation_label);
        sCurrLatitudeTextView = (TextView) rootView.findViewById(R.id.current_latitude_value);
        sCurrLongitudeTextView = (TextView) rootView.findViewById(R.id.current_longitude_value);
        sMinElevTextView = (TextView) rootView.findViewById(R.id.min_height_numbers);
        sMaxElevTextView = (TextView) rootView.findViewById(R.id.max_height_numbers);
        sCurrAddressTextView = (TextView) rootView.findViewById(R.id.location_label);

        sDataFormatConverter = new DataFormatConverter();

        return rootView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    //TODO-> assign more content here, consider moving
    //called onStart and restart-> update information to show on app start
    private void updateAppInfo()
    {
        //FetchDataInfoTask fetchDataInfoTask = new FetchDataInfoTask();

        //fetchDataInfoTask.execute();
    }


    //Initiate google play service (MainFragment needs to implement GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    //and override onConnected, onConnectionSuspended, onConnectionFailed; add LocationServices.API to update device location in real time;
    private void initiateGooglePlayService()
    {
        //connect in onStart, disconnect in onStop of the Activity
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        //define location request of GooglePlayService
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //build location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        //TODO->analyse line below, if needed*
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        //call location request to get location update in set intervals-> must have
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        //!!! TODO -> change permissions system, for now API target version has been downgraded from API 25 to API 22 to make it work
        //!!! TODO -> from API 23 dangerous permissions have to be check in runtime; -> change to API 25 and add required code changes
        //onConnected is triggered after onStart(), so doInBackground() is completed first, then commands from here;
        //update TextViews with location, in case there is incorrect old value from
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null)
        {
            setMinMaxAltitude(lastLocation.getAltitude());
            updateCurrentPositionTextViews(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.v(LOG_TAG, "Connection suspended, no location updates will be received");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.v(LOG_TAG, "Error occur, connection failed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            updateCurrentPositionTextViews(location);

            //update current elevation TextView
            String elevationStr = sDataFormatConverter.formatElevation(location.getAltitude());
            sCurrElevationTextView.setText(elevationStr);

            updateMaxMinAltitude(location.getAltitude());
        }
    }

    //format geo coordinates to degrees/minutes/seconds and update TextViews
    private void updateCurrentPositionTextViews(Location location)
    {
        String latitudeStr = sDataFormatConverter.replaceDelimitersAddDirection(location.getLatitude(), true);
        String longitudeStr = sDataFormatConverter.replaceDelimitersAddDirection(location.getLongitude(), false);
        sCurrLatitudeTextView.setText(latitudeStr);
        sCurrLongitudeTextView.setText(longitudeStr);
    }

    //set at start of app to prevent min/maxAltitude equal to null
    private void setMinMaxAltitude(Double altitude)
    {
        //TODO->check if there is stored last location in preferences
        mMinAltitudeValue = altitude;
        mMaxAltitdeValue = altitude;
        String minAltitudeStr = sDataFormatConverter.formatElevation(mMinAltitudeValue);
        String maxAltitudeStr = sDataFormatConverter.formatElevation(mMaxAltitdeValue);
        minAltitudeStr = sDataFormatConverter.addMetersAboveSeaLevel(minAltitudeStr);
        maxAltitudeStr = sDataFormatConverter.addMetersAboveSeaLevel(maxAltitudeStr);
        sMinElevTextView.setText(minAltitudeStr);
        sMaxElevTextView.setText(maxAltitudeStr);
    }

    //if needed update min and max recorded altitude and update TextViews;
    private void updateMaxMinAltitude(Double altitude)
    {
        if (altitude < mMinAltitudeValue)
        {
            mMinAltitudeValue = altitude;
            String minAltitudeStr = sDataFormatConverter.formatElevation(mMinAltitudeValue);
            minAltitudeStr = sDataFormatConverter.addMetersAboveSeaLevel(minAltitudeStr);
            sMinElevTextView.setText(minAltitudeStr);
        }
        else if (altitude > mMaxAltitdeValue)
        {
            mMaxAltitdeValue = altitude;
            String maxAltitudeStr = sDataFormatConverter.formatElevation(mMaxAltitdeValue);
            maxAltitudeStr = sDataFormatConverter.addMetersAboveSeaLevel(maxAltitudeStr);
            sMaxElevTextView.setText(maxAltitudeStr);
        }
    }
}
