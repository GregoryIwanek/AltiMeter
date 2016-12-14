package pl.grzegorziwanek.altimeter.app;

/**
 * Created by Grzegorz Iwanek on 30.11.2016.
 * Consist constants keys used in called FetchAddressIntentService and to retrieve result's back
 */
public final class Constants
{
    //service constants (used in fetch address service by current location)
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "pl.grzegorziwanek.altimeter.app";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    //time constants (used to define intervals between two location request, depends on chosen settings)
    private static final int ONE_SECOND = 1000;
    private static final int FIVE_SECONDS = 1000 * 5;
    private static final int THIRTY_SECONDS = 1000 * 30;
    private static final int ONE_MINUTE = 1000 * 60;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    //Shared preferences default
    public static final int ALTITUDE_MIN = 20000;
    public static final int ALTITUDE_MAX = -20000;

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
