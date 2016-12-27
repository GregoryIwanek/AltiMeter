package pl.grzegorziwanek.altimeter.app;

/**
 * Created by Grzegorz Iwanek on 30.11.2016.
 * Consist constants keys used in called FetchAddressIntentService and to retrieve result's back
 */
public final class Constants {
    //service constants (used in fetch address service by current location)
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "pl.grzegorziwanek.altimeter.app";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    //time constants (used to define intervals between two location request, depends on chosen settings)
    public static final int ONE_SECOND = 1000;
    public static final int FIVE_SECONDS = 1000 * 5;
    public static final int THIRTY_SECONDS = 1000 * 30;
    public static final int ONE_MINUTE = 1000 * 60;
    public static final int TWO_MINUTES = 1000 * 60 * 2;

    //Shared preferences default
    public static final int ALTITUDE_MIN = 20000;
    public static final int ALTITUDE_MAX = -20000;
    public static final int DISTANCE_DEFAULT = 0;

    public static final String DEFAULT_TEXT = "...";
}

//    //TODO->remove button, test code to check some features
//    public Button button;
//    @Override
//    public void onClick(View view) {
//        System.out.println("clicked");
//        if (view == button) {
//            System.out.println("startIntent");
//            //startAddressIntentService();
//        }
//        graphViewDrawTask.deliverGraph(sAltList);
//    }

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

//TODO->analyse line below, if needed* onConnected !!!
//PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

//    //TODO->fix X axis positions
//    public void deliverGraphOnResume(ArrayList<Double> list) {
//        System.out.println("DELIVER GRAPH ON RESUME CALLED");
//        System.out.println("SIZE OF LIST ON RESUME " + list.size());
//        //update xAxisBorder
//        updateXBorderValue(list.size());
//
////        int i = getSeries().size();
//        System.out.println("CHECK SERIES BEFORE CLEAR " + this.getSeries());
//        //this.getSeries().clear();
//        //TODO->REMOVE LATER
//        //define list with DataPoints based on given altitude list
//        //ArrayList<DataPoint> pointList = new ArrayList<>();
//
////        for (Double point: list) {
////            //TODO->REMOVE LATER
////            //pointList.add(new DataPoint(i, point+1));
////            sSeries.appendData(new DataPoint(i, point), true, xAxisBorder);
////            i++;
////        }
//        //TODO->REMOVE LATER
//        //add points to sSeries of graph
//        //sSeries = new LineGraphSeries<DataPoint>(pointList.toArray(new DataPoint[]{}));
//        System.out.println("CHECK SERIES AFTER CLEAR "+ this.getSeries());
//        this.addSeries(sSeries);
//        System.out.println("CHECK SERIES AFTER ADDED ONRESUME " + this.getSeries());
//
//        refreshGraphLook(list.size());
//    }
