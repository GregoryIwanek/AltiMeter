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
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    public MainFragment() {
    }

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private FetchDataInfoTask mFetchDataInfoTask;
    private static DataFormatAndValueConverter sDataFormatAndValueConverter;

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

    private AddressResultReceiver mResultReceiver;
    public Location mLastLocation;
    public Button button;
    //public GraphView graphView;
    public GraphViewDrawTask graphViewDrawTask;
    public ArrayList<Double> altList = new ArrayList<>();

    //TODO->remove button, test code to check some features
    @Override
    public void onClick(View view) {
        System.out.println("clicked");
        if (view == button) {
            System.out.println("startIntent");
            //startIntentService();
        }
        graphViewDrawTask.deliverGraph(altList);
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
        //mResultReceiver = new AddressResultReceiver(new Handler());
        System.out.println("creating intent");
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        System.out.println("created and adding constants");
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        System.out.println("GET EXTRAS " + intent.getExtras());
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        System.out.println("starting service");
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

        mResultReceiver = new AddressResultReceiver(new Handler());
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

        sDataFormatAndValueConverter = new DataFormatAndValueConverter();

        button = (Button) rootView.findViewById(R.id.moje_id);
        button.setOnClickListener(this);
        //graphView = (GraphView) rootView.findViewById(R.id.graph_view);
        graphViewDrawTask = (GraphViewDrawTask) rootView.findViewById(R.id.graph_view);

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
            String elevationStr = sDataFormatAndValueConverter.formatElevation(location.getAltitude());
            sCurrElevationTextView.setText(elevationStr);

            updateMaxMinAltitude(location.getAltitude());

            startIntentService(location);

            //TODO->kick somewhere else
            altList.add(location.getAltitude());
            graphViewDrawTask.deliverGraph(altList);
        }
    }

    //format geo coordinates to degrees/minutes/seconds and update TextViews
    private void updateCurrentPositionTextViews(Location location)
    {
        String latitudeStr = sDataFormatAndValueConverter.replaceDelimitersAddDirection(location.getLatitude(), true);
        String longitudeStr = sDataFormatAndValueConverter.replaceDelimitersAddDirection(location.getLongitude(), false);
        sCurrLatitudeTextView.setText(latitudeStr);
        sCurrLongitudeTextView.setText(longitudeStr);
    }

    //set at start of app to prevent min/maxAltitude equal to null
    private void setMinMaxAltitude(Double altitude)
    {
        //TODO->check if there is stored last location in preferences
        mMinAltitudeValue = altitude;
        mMaxAltitdeValue = altitude;
        String minAltitudeStr = sDataFormatAndValueConverter.formatElevation(mMinAltitudeValue);
        String maxAltitudeStr = sDataFormatAndValueConverter.formatElevation(mMaxAltitdeValue);
        minAltitudeStr = sDataFormatAndValueConverter.addMetersAboveSeaLevel(minAltitudeStr);
        maxAltitudeStr = sDataFormatAndValueConverter.addMetersAboveSeaLevel(maxAltitudeStr);
        sMinElevTextView.setText(minAltitudeStr);
        sMaxElevTextView.setText(maxAltitudeStr);
    }

    //update current minimum and maximum elevation value text views
    private void updateMaxMinAltitude(Double currAltitude)
    {
        mMinAltitudeValue = sDataFormatAndValueConverter.updateMinAltitude(currAltitude, mMinAltitudeValue);
        mMaxAltitdeValue = sDataFormatAndValueConverter.updateMaxAltitude(currAltitude, mMaxAltitdeValue);

        String minAltitudeStr = sDataFormatAndValueConverter.updateCurrMinMaxString(mMinAltitudeValue);
        String maxAltitudeStr = sDataFormatAndValueConverter.updateCurrMinMaxString(mMaxAltitdeValue);

        sMinElevTextView.setText(minAltitudeStr);
        sMaxElevTextView.setText(maxAltitudeStr);
    }
}
