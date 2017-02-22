package pl.grzegorziwanek.altimeter.app.details;

import java.util.Map;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;

/**
 * Created by Grzegorz Iwanek on 09.02.2017.
 */

interface DetailsContract {

    interface View extends BaseView<Presenter> {

        void setTitleTextView(String title);

        void setDescriptionTextView(String description);

        void setIdTextView(String id);

        void setNumPointsTextView(String numOfPoints);

        void setTimeStartTextView(String timeStart);

        void setTimeEndTextView(String timeEnd);

        void setDistanceTextView(String distance);

        void showChangesSaved();

        void sendChanges();
    }

    interface Presenter extends BasePresenter {

        void saveTextChanges();

        void saveChanges(Map<String, String> changes);
    }
}
