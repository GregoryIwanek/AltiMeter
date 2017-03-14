package pl.grzegorziwanek.altimeter.app.map;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.v7.widget.ShareActionProvider;
import android.view.Window;

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

        void showShareMenu(Intent intent);

        void showMapEmpty();

        void showMapLoaded();
    }

    interface Presenter extends BasePresenter {

        void loadMapData();

        void shareScreenShot(Window window, ContentResolver cr);
    }
}
