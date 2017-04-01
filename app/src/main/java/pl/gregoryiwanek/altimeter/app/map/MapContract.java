package pl.gregoryiwanek.altimeter.app.map;

import android.content.ContentResolver;
import android.content.Intent;
import android.view.Window;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.gregoryiwanek.altimeter.app.BasePresenter;
import pl.gregoryiwanek.altimeter.app.BaseView;

interface MapContract {

    interface View extends BaseView<Presenter> {

        void updateMap(List<LatLng> positions);

        void showShareMenu(Intent intent);

        void showMapEmpty();

        void showMapLoaded();
    }

    interface Presenter extends BasePresenter {

        void loadMapData();

        void shareScreenShot(Window window, ContentResolver cr, GoogleMap currentMap);
    }
}
