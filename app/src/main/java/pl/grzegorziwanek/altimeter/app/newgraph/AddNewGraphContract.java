package pl.grzegorziwanek.altimeter.app.newgraph;

import android.location.Location;

import java.util.ArrayList;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;

/**
 * Created by Grzegorz Iwanek on 31.01.2017. That's it/
 */

interface AddNewGraphContract {

    interface View extends BaseView<Presenter> {

        void setButtonTag(int buttonTag);

        void setButtonPicture(int imageId);

        void showSessionLocked();

        void showRecordingPaused();

        void showRecordingData();

        void setAddressTextView(String address);

        void setElevationTextView(String elevation);

        void setMinHeightTextView(String minHeight);

        void setDistanceTextView(String distance);

        void setMaxHeightTextView(String maxHeight);

        void setLatTextView(String latitude);

        void setLongTextView(String longitude);

        void drawGraph(ArrayList<Location> locations);

        void resetGraph();
    }

    interface Presenter extends BasePresenter {

        void startLocationRecording();

        void stopLocationRecording();

        void resetSessionData();

        void lockSession();

        void generateMap();
    }
}
