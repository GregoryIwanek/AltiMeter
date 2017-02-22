package pl.grzegorziwanek.altimeter.app.newgraph;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;

/**
 * Created by Grzegorz Iwanek on 31.01.2017. That's it/
 */

interface AddNewGraphContract {

    interface View extends BaseView<Presenter> {

        void setButtonTagAndPicture(int pictureId);

        void checkDataSourceOpen();

        void showSessionLocked();

        void showRecordingPaused();

        void showRecordingData();

        void showSessionMap(@NonNull String sessionId);

        void setAddressTextView(String address);

        void setElevationTextView(String elevation);

        void setMinHeightTextView(String minHeight);

        void setDistanceTextView(String distance);

        void setMaxHeightTextView(String maxHeight);

        void setLatTextView(String latitude);

        void setLongTextView(String longitude);

        void setGpsTextView(String gpsAlt);

        void setNetworkTextView(String networkAlt);

        void setBarometerTextView(String barometerAlt);

        void drawGraph(ArrayList<Location> locations);

        void resetGraph();
    }

    interface Presenter extends BasePresenter {

        void openSessionMap();

        void callStartLocationRecording();

        void startLocationRecording();

        void pauseLocationRecording();

        void enableGps();

        void disableGps();

        void enableNetwork();

        void disableNetwork();

        void enableBarometer();

        void disableBarometer();

        void resetSessionData();

        void lockSession();
    }
}
