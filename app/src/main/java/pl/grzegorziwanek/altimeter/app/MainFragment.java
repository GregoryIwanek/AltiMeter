package pl.grzegorziwanek.altimeter.app;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private GoogleApiClient mGoogleApiClient;
    private FetchDataInfoTask mFetchDataInfoTask;
    private static DataFormatConverter sDataFormatConverter;

    //variables to hold data as doubles and refactor them later into TextViews
    private double mCurrentEleValue;
    private double mCurrentLngValue;
    private double mCurrentLatValue;
    private double mMaxElevValue = 0;
    private double mMinElevValue = 0;

    //TextViews of View, fulled with refactored data from JSON objects and Google Play Service
    private static TextView sCurrElevationTextView;
    private static TextView sCurrLatitudeTextView;
    private static TextView sCurrLongitudeTextView;
    private static TextView sMaxElevTextView;
    private static TextView sMinElevTextView;

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
        FetchDataInfoTask fetchDataInfoTask = new FetchDataInfoTask();

        fetchDataInfoTask.execute();
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

        System.out.println("IS GOOGLE API CLIENT CONNECTED?: " + mGoogleApiClient.isConnected());

        //TODO-> move outside of this method, just to test current state
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        //TODO-> changed to priority high accuracy to make it work on emulator,
        //TODO-> on real device choose balanced mode
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        //!!! TODO -> change permissions system, for now API target version has been downgraded from API 25 to API 22 to make it work
        //!!! TODO -> from API 23 dangerous permissions have to be check in runtime
        //onConnected is triggered after onStart(), so doInBackground() is completed first, then
        //commands from here
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        System.out.println(mLastLocation);
        if (mLastLocation != null)
        {
            sCurrLatitudeTextView.setText(String.valueOf(mLastLocation.getLatitude()));
            sCurrLongitudeTextView.setText(String.valueOf(mLastLocation.getLongitude()));
            System.out.println(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            //format geo coordinates to degrees/minutes/seconds and update TextViews
            String latitudeStr = sDataFormatConverter.replaceDelimitersAddDirection(location.getLatitude(), true);
            String longitudeStr = sDataFormatConverter.replaceDelimitersAddDirection(location.getLongitude(), false);
            sCurrLatitudeTextView.setText(latitudeStr);
            sCurrLongitudeTextView.setText(longitudeStr);
        }
    }
}
