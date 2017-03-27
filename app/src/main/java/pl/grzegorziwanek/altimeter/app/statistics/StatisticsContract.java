package pl.grzegorziwanek.altimeter.app.statistics;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;

interface StatisticsContract {

    interface View extends BaseView<Presenter> {

        void setNumSessionsTextView(String str);

        void setNumPointsTextView(String str);

        void setDistanceTextView(String str);

        void setMaxAltTextView(String str);

        void setMinAltTextView(String str);

        void setLongSessionTextView(String str);

        void showIsResetSuccess(String message);
    }

    interface Presenter extends BasePresenter {

        void resetStatistics();
    }
}
