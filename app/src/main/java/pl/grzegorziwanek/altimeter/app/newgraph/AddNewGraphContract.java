package pl.grzegorziwanek.altimeter.app.newgraph;

import android.location.Location;

import java.util.ArrayList;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;

/**
 * Created by Grzegorz Iwanek on 31.01.2017.
 */

public interface AddNewGraphContract {

    public interface View extends BaseView<Presenter> {

        void setButtonTag(int buttonTag);

        void setButtonPicture(int imageId);

        void setAddressTextView(String address);

        void setElevationTextView(String elevation);

        void setMinHeightTextView(String minHeight);

        void setDistanceTextView(String distance);

        void setMaxHeightTextView(String maxHeight);

        void setLatTextView(String latitude);

        void setLongTextView(String longitude);

        void drawGraph(ArrayList<Location> locations);

        void updateGraph(ArrayList<Location> locations);

        void resetGraph();
    }

    public interface Presenter extends BasePresenter {

        void startLocationRecording();

        void stopLocationRecording();

        void resetData();
    }
}

//interface View extends BaseView<Presenter> {
//
//    void showEmptyTaskError();
//
//    void showTasksList();
//
//    void setTitle(String title);
//
//    void setDescription(String description);
//
//    boolean isActive();
//}
//
//interface Presenter extends BasePresenter {
//
//    void saveTask(String title, String description);
//
//    void populateTask();
//
//    boolean isDataMissing();
//}
