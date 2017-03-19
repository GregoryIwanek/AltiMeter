package pl.grzegorziwanek.altimeter.app.map;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileOutputStream;
import java.io.OutputStream;
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
                    positionsFixed.add(new LatLng(52.287638, 21.001133));
                    positionsFixed.add(new LatLng(52.285669, 21.003386));
                    positionsFixed.add(new LatLng(52.283661, 21.005242));
                    positionsFixed.add(new LatLng(52.281429, 21.006841));
                    positionsFixed.add(new LatLng(52.278718, 21.008418));
                    positionsFixed.add(new LatLng(52.276178, 21.009491));
                    positionsFixed.add(new LatLng(52.273769, 21.010382));
                    positionsFixed.add(new LatLng(52.270138, 21.011766));
                    positionsFixed.add(new LatLng(52.267519, 21.012710));
                    positionsFixed.add(new LatLng(52.264886, 21.013504));
                    positionsFixed.add(new LatLng(52.263750, 21.013804));
                    positionsFixed.add(new LatLng(52.262962, 21.013321));
                    positionsFixed.add(new LatLng(52.262535, 21.012828));
                    positionsFixed.add(new LatLng(52.261589, 21.013654));
                    positionsFixed.add(new LatLng(52.262607, 21.016358));
                    positionsFixed.add(new LatLng(52.261149, 21.010661));
                    positionsFixed.add(new LatLng(52.259961, 21.006895));
                    positionsFixed.add(new LatLng(52.258831, 21.002442));
                    positionsFixed.add(new LatLng(52.258634, 20.997915));
                    positionsFixed.add(new LatLng(52.258391, 20.997786));
                    positionsFixed.add(new LatLng(52.257170, 20.997164));
                    positionsFixed.add(new LatLng(52.255475, 20.998247));
                    positionsFixed.add(new LatLng(52.253952, 20.999685));
                    positionsFixed.add(new LatLng(52.252809, 21.000801));
                    positionsFixed.add(new LatLng(52.253321, 21.002389));
                    positionsFixed.add(new LatLng(52.253794, 21.004105));
                    positionsFixed.add(new LatLng(52.252980, 21.005285));
                    positionsFixed.add(new LatLng(52.253485, 21.006873));
                    positionsFixed.add(new LatLng(52.252336, 21.010017));
                    positionsFixed.add(new LatLng(52.251646, 21.011068));
                    positionsFixed.add(new LatLng(52.252086, 21.012442));
                    positionsFixed.add(new LatLng(52.252980, 21.012045));

                    LatLng start = new LatLng(52.287638, 21.001133);
                    LatLng end = new LatLng(52.252980, 21.012045);

                    int colorId = Color.argb(150, 50, 50, 255);

                    PolylineOptions polylineOptions = new PolylineOptions().addAll(positionsFixed);
                    polylineOptions.color(colorId);

                    mGoogleMap.addPolyline(polylineOptions);
                    mGoogleMap.addMarker(new MarkerOptions().position(start).title("Start"));
                    mGoogleMap.addMarker(new MarkerOptions().position(end).title("End"));
                    //cameraPosition = new CameraPosition.Builder().target(position).zoom(8).build();
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
                    //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
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

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}

