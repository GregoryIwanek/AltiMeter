package pl.grzegorziwanek.altimeter.app;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    public MainFragment(){}

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private FetchDataInfoTask mFetchDataInfoTask;
    private static DataFormatAndValueConverter sDataFormatAndValueConverter;

    //variables to hold data as doubles and refactor them later into TextViews
    public Location mLastLocation;
    private double mCurrentEleValue;
    private double mCurrentLngValue;
    private double mCurrentLatValue;
    private double mMaxAltitdeValue = -20000;
    private double mMinAltitudeValue = 20000;

    //TextViews of View, fulled with refactored data from JSON objects and Google Play Service
    private static TextView sCurrElevationTextView;
    private static TextView sCurrLatitudeTextView;
    private static TextView sCurrLongitudeTextView;
    private static TextView sMaxElevTextView;
    private static TextView sMinElevTextView;
    private static TextView sCurrAddressTextView;

    //graph view field
    private static GraphViewDrawTask graphViewDrawTask;
    private static ArrayList<Double> sAltList = new ArrayList<>();
    private static AddressResultReceiver sResultReceiver;

//    //TODO->remove button, test code to check some features
//    public Button button;
//    @Override
//    public void onClick(View view) {
//        System.out.println("clicked");
//        if (view == button) {
//            System.out.println("startIntent");
//            //startIntentService();
//        }
//        graphViewDrawTask.deliverGraph(sAltList);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initiate google play service ( used to update device's location in given intervals)
        initiateGooglePlayService();

        //TODO->remove that, dummy data
        mLastLocation = new Location(LocationManager.NETWORK_PROVIDER);
        mLastLocation.setLatitude(51.797247);
        mLastLocation.setLongitude(22.236283);
        mLastLocation.setAltitude(42.00);
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

        sResultReceiver = new AddressResultReceiver(new Handler());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //assign UI elements to inner variables
        sCurrElevationTextView = (TextView) rootView.findViewById(R.id.current_elevation_label);
        sCurrLatitudeTextView = (TextView) rootView.findViewById(R.id.current_latitude_value);
        sCurrLongitudeTextView = (TextView) rootView.findViewById(R.id.current_longitude_value);
        sMinElevTextView = (TextView) rootView.findViewById(R.id.min_height_numbers);
        sMaxElevTextView = (TextView) rootView.findViewById(R.id.max_height_numbers);
        sCurrAddressTextView = (TextView) rootView.findViewById(R.id.location_label);
        graphViewDrawTask = (GraphViewDrawTask) rootView.findViewById(R.id.graph_view);

        sDataFormatAndValueConverter = new DataFormatAndValueConverter();

//        button = (Button) rootView.findViewById(R.id.moje_id);
//        button.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //check if activity in foreground, get current address, redraw altitude graph
        if (this.getActivity() != null)
        {
            startIntentService(mLastLocation);

            //redraw graph only when app backs from background, not when started first time
            if (!sAltList.isEmpty())
            {
                graphViewDrawTask.deliverGraphOnResume(sAltList);
            }
        }
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


            System.out.println("NEW ADDRESS");
            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            sCurrAddressTextView.setText(mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }
        }
    }

    protected void startIntentService(Location location)
    {
        Intent intent = new Intent(this.getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, sResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        this.getActivity().startService(intent);
    }

    public void fetchAddressButtonHandler(View view)
    {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        System.out.println("checking if connected");
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            System.out.println("starting");
            startIntentService(mLastLocation);
        }
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
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null)
        {
            updateCurrentMaxMinAltitude(lastLocation.getAltitude());
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
            //add new location point to the list
            sAltList.add(location.getAltitude());

            //perform ONLY if an activity is in foreground (updating TextViews and redrawing graph)
            if (this.getActivity() != null)
            {
                //TODO->update current max min in separeted method, without updating textviews
                updateCurrentPositionTextViews(location);
                updateCurrentMaxMinAltitude(location.getAltitude());

                String elevationStr = sDataFormatAndValueConverter.formatElevation(location.getAltitude());
                sCurrElevationTextView.setText(elevationStr);

                startIntentService(location);
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
        mMaxAltitdeValue = sDataFormatAndValueConverter.updateMaxAltitude(currAltitude, mMaxAltitdeValue);

        //refactor string with min max altitude to correct form
        String minAltitudeStr = sDataFormatAndValueConverter.updateCurrMinMaxString(mMinAltitudeValue);
        String maxAltitudeStr = sDataFormatAndValueConverter.updateCurrMinMaxString(mMaxAltitdeValue);

        //update TextViews
        sMinElevTextView.setText(minAltitudeStr);
        sMaxElevTextView.setText(maxAltitudeStr);
    }
}
