package pl.grzegorziwanek.altimeter.app.Map;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.BasePresenter;
import pl.grzegorziwanek.altimeter.app.BaseView;

/**
 * Created by Grzegorz Iwanek on 07.02.2017.
 */

interface MapContract {

    interface View extends BaseView<Presenter> {

        void updateMap(List<LatLng> positions);
    }

    interface Presenter extends BasePresenter {

        void loadMapData();
    }
}
