package pl.grzegorziwanek.altimeter.app.Map;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.model.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 07.02.2017.
 */

public class MapPresenter implements MapContract.Presenter {

    private final SessionRepository mSessionRepository;
    private final MapContract.View mMapView;
    private final String mId;

    public MapPresenter(@NonNull String id,
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
                mMapView.updateMap(positions);
            }
        });
    }
}
