package pl.grzegorziwanek.altimeter.app.map;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Window;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.data.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.utils.ScreenShotCatcher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 07.02.2017.
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
        mSessionRepository.getMapData(mId, new SessionDataSource.LoadMapDataCallback() {
            @Override
            public void onMapDataLoaded(List<LatLng> positions) {
                checkMapData(positions);
            }
        });
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
