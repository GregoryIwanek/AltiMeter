package pl.gregoryiwanek.altimeter.app.data.location.services.helpers;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.gregoryiwanek.altimeter.app.utils.Constants;

/**
 * Created by Grzegorz Iwanek on 30.11.2016.
 * Consist code responsible for reversed geocoding (getting address possible to retrieve through passing latitude and longitude)
 * Implements IntentService; Has to be included in manifest file in corresponding activity section;
 * Returns address through use of geocoder class;
 */
public class AddressService extends IntentService {

    private ResultReceiver mResultReceiver;

    public AddressService() {
        super("EMPTY CONSTRUCTOR");
    }

    public AddressService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //bind and assign receiver from here and activity (through Constants)
        bindResultReceiver(intent);

        //create geocoder instance-> it will handle reversed geocoding operation
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA);

        String errorMessage = "";

        //geocoder's getFromLocation is a key phrase-> returns list of addresses in close proximity to given coordinates
        //possible errors: No location data provided, Invalid latitude or longitude used, No geocoder available, Sorry, no address found
        List<Address> addresses = null;

        try {
            //try to assign allowed number of addresses to a list; have to
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException | IllegalArgumentException ioException) {
            //thrown in case of service offline
            //errorMessage = getString(R.string.service_not_available);
            errorMessage = "Solar System," +'\n'+ "Milky Way," +'\n'+ "Laniakea";
        }

        //check for case of no address found
        if (addresses == null || addresses.size() == 0) {
            //check if different error has already occur
            if(errorMessage.isEmpty()) {
                errorMessage = "Solar System," +'\n'+ "Milky Way," +'\n'+ "Laniakea";
            }

            //deliver info about failure
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            //address was found
            Address address = addresses.get(0);
            ArrayList<String> addressLines = new ArrayList<>();

            //fetching each of address lines from given address object to the List
            for (int i=0; i<address.getMaxAddressLineIndex(); i++) {
                addressLines.add(address.getAddressLine(i));
            }

            //delivering address to the receiver
            String combinedAddress = TextUtils.join(System.getProperty("line.separator"), addressLines);
            deliverResultToReceiver(Constants.SUCCESS_RESULT, combinedAddress);
        }
    }

    private void bindResultReceiver(Intent intent) {
        mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mResultReceiver.send(resultCode, bundle);
    }
}