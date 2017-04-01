package pl.gregoryiwanek.altimeter.app.recordingsession;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Window;

import java.util.ArrayList;

import pl.gregoryiwanek.altimeter.app.BasePresenter;
import pl.gregoryiwanek.altimeter.app.BaseView;
import pl.gregoryiwanek.altimeter.app.data.GraphPoint;

/**
 * Created by Grzegorz Iwanek on 31.01.2017. That's it/
 */
@SuppressWarnings("unused")
interface RecordingSessionContract {

    interface View extends BaseView<Presenter> {

        void setButtonTagAndPicture(int pictureId);

        void checkDataSourceOpen();

        void showSessionLocked();

        void showRecordingPaused();

        void showRecordingData();

        void askGenerateMap();

        void showShareMenu(Intent screenshotIntent);

        void showSessionMap(@NonNull String sessionId);

        void showMapEmpty();

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

        void drawGraph(ArrayList<GraphPoint> graphPoints);

        void resetGraph();
    }

    interface Presenter extends BasePresenter {

        void openMapOfSession();

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

        void onActivityDestroyedUnsubscribeRx();

        void shareScreenShot(Window window, ContentResolver cr, String[] textViewContent);

        void checkIsSessionEmpty();
    }
}
