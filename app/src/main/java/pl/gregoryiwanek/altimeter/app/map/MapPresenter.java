package pl.gregoryiwanek.altimeter.app.map;

import android.content.*;
import android.view.*;

import androidx.annotation.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.*;

import pl.gregoryiwanek.altimeter.app.data.database.source.*;
import pl.gregoryiwanek.altimeter.app.utils.screenshotcatcher.*;

import static com.google.common.base.Preconditions.*;

//import pl.gregoryiwanek.altimeter.app.data.database.SessionDataSource;
//import pl.gregoryiwanek.altimeter.app.data.database.SessionRepository;

/**
 * Presenter of Map section.
 * Works as a bridge between {@link SessionRepository} and {@link MapContract.View}.
 */
class MapPresenter implements MapContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final MapContract.View mMapView;
    private final String mId;

    MapPresenter(@NonNull String id,
                 @NonNull SessionRepository sessionSource,
                 @NonNull MapContract.View mapView) {
        mId = id;
        mSessionRepository = checkNotNull(sessionSource);
        mMapView = checkNotNull(mapView);
        mMapView.setPresenter(this);
    }

    @Override
    public void start() {
        loadMapData();
    }

    @Override
    public void loadMapData() {
        mSessionRepository.getMapData(mId, this::checkMapData);
    }

    @Override
    public void shareScreenShot(Window window, ContentResolver cr, GoogleMap currentMap) {
        ScreenShotCatcher catcher = new ScreenShotCatcher();
        Intent screenshotIntent = catcher.captureAndShare(window, cr, null, currentMap);
        mMapView.showShareMenu(screenshotIntent);
    }

    private void checkMapData(List<LatLng> positions) {
        if (isMapDataEmpty(positions)) {
            mMapView.showMapEmpty();
        } else {
            mMapView.updateMap(positions);
            mMapView.showMapLoaded();
        }
    }

    private boolean isMapDataEmpty(List<LatLng> positions) {
        return positions.isEmpty();
    }
}
