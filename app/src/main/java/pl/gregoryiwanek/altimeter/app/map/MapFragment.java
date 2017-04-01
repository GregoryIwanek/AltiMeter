package pl.gregoryiwanek.altimeter.app.map;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Iterables;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.gregoryiwanek.altimeter.app.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Consists view class of Map section.
 * Uses GoogleMap as a map representation.
 * Generates polyline path based on the session's recorded points.
 * Object of this class should be created from within {@link MapActivity}, and always with given
 * id of the session map it's creating.
 */
public class MapFragment extends Fragment implements MapContract.View {

    @BindView(R.id.map) MapView mMapView;

    private MapContract.Presenter mPresenter;
    private GoogleMap mGoogleMap;

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        ButterKnife.bind(this, view);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void setPresenter(MapContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_share_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_facebook:
                shareClicked();
                break;
        }
        return true;
    }

    private void shareClicked() {
        ContentResolver cr = this.getActivity().getContentResolver();
        Window window = getActivity().getWindow();
        mPresenter.shareScreenShot(window, cr, mGoogleMap);
    }

    @Override
    public void updateMap(final List<LatLng> positions) {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (positions != null && positions.size() > 0) {
            generateAMap(positions);
        }
    }

    private void generateAMap(final List<LatLng> positions) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;
                mGoogleMap.setMyLocationEnabled(true);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                } else {
                    LatLng start = positions.get(0);
                    LatLng end = Iterables.getLast(positions);
                    addMarkerToMap(start, "Start");
                    addMarkerToMap(end, "End");
                    addPolylinePathToMap(positions);
                    setCameraPosition(end);
                }
            }
        });
    }

    private void addMarkerToMap(LatLng position, String title) {
        mGoogleMap.addMarker(new MarkerOptions().position(position).title(title));
    }

    private void addPolylinePathToMap(List<LatLng> positions) {
        int colorPoly = Color.argb(150, 50, 50, 255);
        PolylineOptions polyline = new PolylineOptions().addAll(positions);
        polyline.color(colorPoly);
        mGoogleMap.addPolyline(polyline);
    }

    private void setCameraPosition(LatLng focusPoint) {
        CameraPosition position = new CameraPosition.Builder().target(focusPoint).zoom(10).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    @Override
    public void showShareMenu(Intent screenshotIntent) {
        startActivity(Intent.createChooser(screenshotIntent, "Send to"));
    }

    @Override
    public void showMapEmpty() {
        showMessage("Session has no points to show on a map...");
    }

    @Override
    public void showMapLoaded() {
        showMessage("Map loaded successfully");
    }

    @SuppressWarnings("ConstantConditions")
    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}