package pl.grzegorziwanek.altimeter.app;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.util.ArrayList;

/**
 * Created by Grzegorz Iwanek on 23.11.2016.
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public MainFragment() {}

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    public static final String PREFS_NAME = "MyPrefsFile";

    private GoogleApiClient mGoogleApiClient;
    private FetchDataInfoTask mFetchDataInfoTask;
    private static DataFormatAndValueConverter sDataFormatAndValueConverter;

    //variables to hold data as doubles and refactor them later into TextViews
    public Location mLastLocation;
    public ArrayList<Location> mLocationList;
    //TODO-> save these three variables in shared preferences ( values are reset after onResume is called)
    private double mMaxAltitudeValue = -20000;
    private double mMinAltitudeValue = 20000;
    private double mCurrentDistance = 0;

    //TextViews of View, fulled with refactored data from JSON objects and Google Play Service
    private static TextView sCurrElevationTextView;
    private static TextView sCurrLatitudeTextView;
    private static TextView sCurrLongitudeTextView;
    private static TextView sMaxElevTextView;
    private static TextView sMinElevTextView;
    private static TextView sCurrAddressTextView;
    private static TextView sDistanceTextView;

    //graph view field
    private static GraphViewDrawTask graphViewDrawTask;
    private static ArrayList<Double> sAltList = new ArrayList<>();
    private static AddressResultReceiver sResultReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initiate google play service ( used to update device's location in given intervals)
        initiateGooglePlayService();

        mLocationList = new ArrayList<>();
    }

    //consist actions to perform upon re/start of app ( update current location and information)
    @Override
    public void onStart()
    {
        //connect google play service and get current location
        mGoogleApiClient.connect();

        super.onStart();

        sResultReceiver = new AddressResultReceiver(new Handler());
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
        sDistanceTextView = (TextView) rootView.findViewById(R.id.distance_numbers);
        graphViewDrawTask = (GraphViewDrawTask) rootView.findViewById(R.id.graph_view);

        sDataFormatAndValueConverter = new DataFormatAndValueConverter();

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //check if activity is in a foreground, get current address, redraw altitude graph
        if (this.getActivity() != null)
        {
            //check if last location is saved (prevent errors on first run of app)
            if (mLastLocation != null)
            {
                startAddressIntentService(mLastLocation);
            }

            //redraw graph only when app backs from background, not when started first time
            if (!sAltList.isEmpty()) {
                graphViewDrawTask.deliverGraphOnResume(sAltList);
            }
        }
        System.out.println("MIN ON RESUME: " + mMinAltitudeValue);
        System.out.println("MAX ON RESUME: " + mMaxAltitudeValue);
    }

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
            sCurrAddressTextView.setText(mAddressOutput);
        }
    }

    protected void startAddressIntentService(Location location)
    {
        Intent intent = new Intent(this.getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, sResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        this.getActivity().startService(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //Initiate google play service (MainFragment needs to implement GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    //and override onConnected, onConnectionSuspended, onConnectionFailed; add LocationServices.API to update device location in real time;
    private void initiateGooglePlayService()
    {
        //connect in onStart, disconnect in onStop of the Activity
        if (mGoogleApiClient == null) {
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            updateCurrentMaxMinAltitude(mLastLocation.getAltitude());
            updateCurrentPositionTextViews(mLastLocation);
        }

        mLocationList.add(mLastLocation);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            //add new location point to the list
            sAltList.add(location.getAltitude());
            mLocationList.add(location);

            if (mLastLocation != null)
            {
                updateDistance(mLastLocation, location);
            }

            mLastLocation = location;

            //update min and max values even if app is in a background
            updateCurrentMaxMinAltitude(location.getAltitude());

            //perform ONLY if an activity is in FOREGROUND (updating TextViews and redrawing graph)
            if (this.getActivity() != null)
            {
                updateCurrentPositionTextViews(location);
                updateCurrentMaxMinStr();

                String elevationStr = sDataFormatAndValueConverter.formatElevation(location.getAltitude());
                sCurrElevationTextView.setText(elevationStr);

                startAddressIntentService(location);
                graphViewDrawTask.deliverGraph(sAltList);
            }
        }
    }

    private void updateCurrentPositionTextViews(Location currLocation)
    {
        //format geo coordinates to degrees/minutes/seconds (from XX:XX:XX.XX to XX*XX'XX''N)
        String latitudeStr = sDataFormatAndValueConverter.replaceDelimitersAddDirection(currLocation.getLatitude(), true);
        String longitudeStr = sDataFormatAndValueConverter.replaceDelimitersAddDirection(currLocation.getLongitude(), false);

        //set new values of current location coordinates text views
        sCurrLatitudeTextView.setText(latitudeStr);
        sCurrLongitudeTextView.setText(longitudeStr);
    }

    private void updateCurrentMaxMinAltitude(Double currAltitude)
    {
        //update variables holding max and min altitude (double)
        mMinAltitudeValue = sDataFormatAndValueConverter.updateMinAltitude(currAltitude, mMinAltitudeValue);
        mMaxAltitudeValue = sDataFormatAndValueConverter.updateMaxAltitude(currAltitude, mMaxAltitudeValue);
        System.out.println("MIN MIN MIN: " + mMinAltitudeValue);
        System.out.println("MAX MAX MAX: " + mMaxAltitudeValue);
    }

    private void updateCurrentMaxMinStr()
    {
        //refactor string with min max altitude to correct form
        String minAltitudeStr = sDataFormatAndValueConverter.updateCurrMinMaxString(mMinAltitudeValue);
        String maxAltitudeStr = sDataFormatAndValueConverter.updateCurrMinMaxString(mMaxAltitudeValue);

        System.out.println("MIN MIN MIN: " + minAltitudeStr);
        System.out.println("MAX MAX MAX: " + maxAltitudeStr);

        //update TextViews
        sMinElevTextView.setText(minAltitudeStr);
        sMaxElevTextView.setText(maxAltitudeStr);
    }

    private void updateDistance(Location lastLocation, Location currLocation)
    {
        if (lastLocation != null && currLocation != null)
        {
            float[] results = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    currLocation.getLatitude(), currLocation.getLongitude(), results);
            mCurrentDistance += results[0];

            sDistanceTextView.setText(sDataFormatAndValueConverter.formatDistance(mCurrentDistance, "MILES"));
        }

        Toast.makeText(this.getActivity(),"Current distance updated " + mCurrentDistance, Toast.LENGTH_SHORT).show();
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

//    public void fetchAddressButtonHandler(View view) {
//        // Only start the service to fetch the address if GoogleApiClient is connected.
//        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
//            System.out.println("starting");
//            startAddressIntentService(mLastLocation);
//        }
//    }

//    //TODO-> assign more content here, consider moving
//    //called onStart and restart-> update information to show on app start
//    private void updateAppInfo()
//    {
//        //FetchDataInfoTask fetchDataInfoTask = new FetchDataInfoTask();
//
//        //fetchDataInfoTask.execute();
//    }
