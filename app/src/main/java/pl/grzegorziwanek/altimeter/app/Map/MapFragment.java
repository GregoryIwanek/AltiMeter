package pl.grzegorziwanek.altimeter.app.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 07.02.2017.
 */

public class MapFragment extends Fragment implements MapContract.View {

    @BindView(R.id.map) MapView mMapView;

    private MapContract.Presenter mPresenter;
    private GoogleMap mGoogleMap;
    private ArrayList<Location> locationArrayList;

    public MapFragment() {}

    public static MapFragment newInstance() {
        return new MapFragment();
    }


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

        return view;
    }

    @Override
    public void setPresenter(MapContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void updateMap(final List<LatLng> positions) {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                CameraPosition cameraPosition = null;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                } else {
                    mGoogleMap.setMyLocationEnabled(true);

                    List<LatLng> positionsFixed = new ArrayList<>();
                    positionsFixed.add(new LatLng(51.830208, 21.803741));
                    positionsFixed.add(new LatLng(52.830208, 21.803741));
                    positionsFixed.add(new LatLng(53.830208, 21.803741));
                    positionsFixed.add(new LatLng(54.830208, 21.803741));
                    positionsFixed.add(new LatLng(55.830208, 21.803741));
                    positionsFixed.add(new LatLng(56.830208, 21.803741));
                    positionsFixed.add(new LatLng(57.830208, 21.803741));
                    positionsFixed.add(new LatLng(58.830208, 21.803741));
                    positionsFixed.add(new LatLng(59.830208, 21.803741));
                    positionsFixed.add(new LatLng(60.830208, 21.803741));
                    positionsFixed.add(new LatLng(61.830208, 21.803741));
                    positionsFixed.add(new LatLng(62.830208, 21.803741));
                    positionsFixed.add(new LatLng(63.830208, 21.803741));
                    positionsFixed.add(new LatLng(64.830208, 21.803741));
                    positionsFixed.add(new LatLng(64.830208, 22.803741));
                    positionsFixed.add(new LatLng(64.830208, 23.803741));
                    positionsFixed.add(new LatLng(64.830208, 24.803741));
                    positionsFixed.add(new LatLng(64.830208, 25.803741));
                    positionsFixed.add(new LatLng(64.830208, 26.803741));
                    positionsFixed.add(new LatLng(64.830208, 27.803741));
                    positionsFixed.add(new LatLng(64.830208, 28.803741));
                    positionsFixed.add(new LatLng(64.830208, 29.803741));
                    positionsFixed.add(new LatLng(64.830208, 30.803741));
                    positionsFixed.add(new LatLng(64.830208, 31.803741));
                    positionsFixed.add(new LatLng(64.830208, 32.803741));
                    positionsFixed.add(new LatLng(64.830208, 33.803741));

                    int colorId = Color.argb(150, 50, 50, 255);

                    PolylineOptions polylineOptions = new PolylineOptions().addAll(positions);
                    polylineOptions.color(colorId);

                    mGoogleMap.addPolyline(polylineOptions);
                    //cameraPosition = new CameraPosition.Builder().target(position).zoom(8).build();
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
                    //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }

    @Override
    public void showMapEmpty() {
        showMessage("Session has no points to show on a map...");
    }

    @Override
    public void showMapLoaded() {
        showMessage("Map loaded successfully");
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}

