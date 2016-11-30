package pl.grzegorziwanek.altimeter.app;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Grzegorz Iwanek on 30.11.2016.
 * Consist code responsible for reversed geocoding (getting adrress possible to retrieve through passing latitude and longitude)
 * Implements IntentService; Has to be included in manifest file in corresponding activity section;
 */
public class FetchAddressIntentService extends IntentService
{
    //constructor
    public FetchAddressIntentService(String name) {super(name);}

    private static final String LOG_TAG = FetchAddressIntentService.class.getSimpleName();

    protected ResultReceiver resultReceiver;

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //create geocoder instance-> it will handle reversed geocoding operation
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        String errorMessage = "";

        //geocoder's getFromLocation is a key phrase-> returns list of addresses in close proximity to given coordinates
        //possible errors: No location data provided, Invalid latitude or longitude used, No geocoder available, Sorry, no address found
        List<Address> addresses = null;

        try
        {
            //try to assign allowed number of addresses to a list; have to
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (IOException ioException)
        {
            //thrown in case of service offline
            errorMessage = getString(R.string.service_not_available);
            Log.e(LOG_TAG, errorMessage, ioException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            //thrown in case of wrong given coordinates
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(LOG_TAG, errorMessage + ", " + "Latitude: " + location.getLatitude()
                    + " , " + "Longitude: " + location.getLongitude(), illegalArgumentException);
        }

        //check for case of no address found
        if (addresses == null || addresses.size() == 0)
        {
            //check if different error has already occur
            if(errorMessage.isEmpty())
            {
                errorMessage = getString(R.string.no_address_found);
                Log.e(LOG_TAG, errorMessage);
            }

            //deliver info about failure
            deliverResultToReciever(Constants.FAILURE_RESULT, errorMessage);
        }
        //if address was found
        else
        {
            Address address = addresses.get(0);
            ArrayList<String> addressLines = new ArrayList<>();

            //fetching each of address lines from given address object to the List
            for (int i=0; i<address.getMaxAddressLineIndex(); i++)
            {
                addressLines.add(address.getAddressLine(i));
            }
            Log.i(LOG_TAG, getString(R.string.address_found));

            //delivering address to the receiver
            String combinedAddress = TextUtils.join(System.getProperty("line.separator"), addressLines);
            deliverResultToReciever(Constants.SUCCESS_RESULT, combinedAddress);
        }
    }

    //delivers message to receiver which  delivers to activity which called for intent process
    private void deliverResultToReciever(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }
}

//package com.exercise.AndroidFromLocation;
//
//        import java.io.IOException;
//        import java.util.List;
//        import java.util.Locale;
//
//        import android.app.Activity;
//        import android.location.Address;
//        import android.location.Geocoder;
//        import android.os.Bundle;
//        import android.widget.TextView;
//
//public class AndroidFromLocation extends Activity {
//
//    double LATITUDE = 37.42233;
//    double LONGITUDE = -122.083;
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        TextView myLatitude = (TextView)findViewById(R.id.mylatitude);
//        TextView myLongitude = (TextView)findViewById(R.id.mylongitude);
//        TextView myAddress = (TextView)findViewById(R.id.myaddress);
//
//        myLatitude.setText("Latitude: " + String.valueOf(LATITUDE));
//        myLongitude.setText("Longitude: " + String.valueOf(LONGITUDE));
//
//        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
//
//        try {
//            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
//
//            if(addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
//                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                myAddress.setText(strReturnedAddress.toString());
//            }
//            else{
//                myAddress.setText("No Address returned!");
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            myAddress.setText("Canont get Address!");
//        }
//
//    }
//}


//package android.javapapers.com.androidgeocodelocation;
//
//        import android.content.Context;
//        import android.location.Address;
//        import android.location.Geocoder;
//        import android.os.Bundle;
//        import android.os.Handler;
//        import android.os.Message;
//        import android.util.Log;
//
//        import java.io.IOException;
//        import java.util.List;
//        import java.util.Locale;
//
//public class LocationAddress {
//    private static final String TAG = "LocationAddress";
//
//    public static void getAddressFromLocation(final double latitude, final double longitude,
//                                              final Context context, final Handler handler) {
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//                String result = null;
//                try {
//                    List<Address> addressList = geocoder.getFromLocation(
//                            latitude, longitude, 1);
//                    if (addressList != null && addressList.size() > 0) {
//                        Address address = addressList.get(0);
//                        StringBuilder sb = new StringBuilder();
//                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                            sb.append(address.getAddressLine(i)).append("\n");
//                        }
//                        sb.append(address.getLocality()).append("\n");
//                        sb.append(address.getPostalCode()).append("\n");
//                        sb.append(address.getCountryName());
//                        result = sb.toString();
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Unable connect to Geocoder", e);
//                } finally {
//                    Message message = Message.obtain();
//                    message.setTarget(handler);
//                    if (result != null) {
//                        message.what = 1;
//                        Bundle bundle = new Bundle();
//                        result = "Latitude: " + latitude + " Longitude: " + longitude +
//                                "\n\nAddress:\n" + result;
//                        bundle.putString("address", result);
//                        message.setData(bundle);
//                    } else {
//                        message.what = 1;
//                        Bundle bundle = new Bundle();
//                        result = "Latitude: " + latitude + " Longitude: " + longitude +
//                                "\n Unable to get address for this lat-long.";
//                        bundle.putString("address", result);
//                        message.setData(bundle);
//                    }
//                    message.sendToTarget();
//                }
//            }
//        };
//        thread.start();
//    }
//}




//package android.javapapers.com.androidgeocodelocation;
//
//        import android.app.Service;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.location.Location;
//        import android.location.LocationListener;
//        import android.location.LocationManager;
//        import android.os.Bundle;
//        import android.os.IBinder;

//public class AppLocationService extends Service implements LocationListener {
//
//    protected LocationManager locationManager;
//    Location location;
//
//    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
//    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
//
//    public AppLocationService(Context context) {
//        locationManager = (LocationManager) context
//                .getSystemService(LOCATION_SERVICE);
//    }
//
//    public Location getLocation(String provider) {
//        if (locationManager.isProviderEnabled(provider)) {
//            locationManager.requestLocationUpdates(provider,
//                    MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
//            if (locationManager != null) {
//                location = locationManager.getLastKnownLocation(provider);
//                return location;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//    }
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return null;
//    }
//
//}



//package android.javapapers.com.androidgeocodelocation;
//
//        import android.app.Activity;
//        import android.app.AlertDialog;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.location.Location;
//        import android.location.LocationManager;
//        import android.os.Bundle;
//        import android.os.Handler;
//        import android.os.Message;
//        import android.provider.Settings;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.TextView;
//
//public class MyActivity extends Activity {
//
//    Button btnGPSShowLocation;
//    Button btnShowAddress;
//    TextView tvAddress;
//
//    AppLocationService appLocationService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my);
//        tvAddress = (TextView) findViewById(R.id.tvAddress);
//        appLocationService = new AppLocationService(
//                MyActivity.this);
//
//        btnGPSShowLocation = (Button) findViewById(R.id.btnGPSShowLocation);
//        btnGPSShowLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Location gpsLocation = appLocationService
//                        .getLocation(LocationManager.GPS_PROVIDER);
//                if (gpsLocation != null) {
//                    double latitude = gpsLocation.getLatitude();
//                    double longitude = gpsLocation.getLongitude();
//                    String result = "Latitude: " + gpsLocation.getLatitude() +
//                            " Longitude: " + gpsLocation.getLongitude();
//                    tvAddress.setText(result);
//                } else {
//                    showSettingsAlert();
//                }
//            }
//        });
//
//        btnShowAddress = (Button) findViewById(R.id.btnShowAddress);
//        btnShowAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                Location location = appLocationService
//                        .getLocation(LocationManager.GPS_PROVIDER);
//
//                //you can hard-code the lat & long if you have issues with getting it
//                //remove the below if-condition and use the following couple of lines
//                //double latitude = 37.422005;
//                //double longitude = -122.084095
//
//                if (location != null) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    LocationAddress locationAddress = new LocationAddress();
//                    locationAddress.getAddressFromLocation(latitude, longitude,
//                            getApplicationContext(), new GeocoderHandler());
//                } else {
//                    showSettingsAlert();
//                }
//
//            }
//        });
//
//    }
//
//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                MyActivity.this);
//        alertDialog.setTitle("SETTINGS");
//        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
//        alertDialog.setPositiveButton("Settings",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(
//                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        MyActivity.this.startActivity(intent);
//                    }
//                });
//        alertDialog.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        alertDialog.show();
//    }
//
//    private class GeocoderHandler extends Handler {
//        @Override
//        public void handleMessage(Message message) {
//            String locationAddress;
//            switch (message.what) {
//                case 1:
//                    Bundle bundle = message.getData();
//                    locationAddress = bundle.getString("address");
//                    break;
//                default:
//                    locationAddress = null;
//            }
//            tvAddress.setText(locationAddress);
//        }
//    }
//}