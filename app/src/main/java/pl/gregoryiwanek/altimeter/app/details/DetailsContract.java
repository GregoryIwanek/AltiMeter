package pl.gregoryiwanek.altimeter.app.details;

import java.util.Map;

import pl.gregoryiwanek.altimeter.app.BasePresenter;
import pl.gregoryiwanek.altimeter.app.BaseView;

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

        void saveChangesInRepository(Map<String, String> changes);

        void exportSessionDataAsFile();
    }
}
