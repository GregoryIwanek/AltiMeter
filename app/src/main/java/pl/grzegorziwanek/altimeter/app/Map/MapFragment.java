package pl.grzegorziwanek.altimeter.app.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap mMap) {
//                mGoogleMap = mMap;
//
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                else
//                {
//                    mGoogleMap.setMyLocationEnabled(true);
//
//                    LatLng position = new LatLng(51.7971276, 22.2376661);
//                    if (locationArrayList != null)
//                    {
//                        int number = 0;
//                        for (Location location : locationArrayList)
//                        {
//                            LatLng anotherPosition = new LatLng(location.getLatitude(), location.getLongitude());
//                            mGoogleMap.addMarker(new MarkerOptions().position(anotherPosition).title("Number " + number).snippet("Number " + number));
//                            number++;
//                        }
//                    }
//                    else
//                    {
//                        mGoogleMap.addMarker(new MarkerOptions().position(position).title("Marker Title").snippet("Marker Description"));
//                    }
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                }
//            }
//        });

        return view;
    }

    public void updateMap()
    {
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap mMap) {
//                mGoogleMap = mMap;
//
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                else
//                {
//                    mGoogleMap.setMyLocationEnabled(true);
//
//                    LatLng position = new LatLng(51.7971276, 22.2376661);
//                    if (locationArrayList != null)
//                    {
//                        int number = 0;
//                        for (Location location : locationArrayList)
//                        {
//                            LatLng anotherPosition = new LatLng(location.getLatitudeStr(), location.getLongitudeStr());
//                            mGoogleMap.addMarker(new MarkerOptions().position(anotherPosition).title("Number " + number).snippet("Number " + number));
//                            number++;
//                        }
//                    }
//                    else
//                    {
//                        mGoogleMap.addMarker(new MarkerOptions().position(position).title("Marker Title").snippet("Marker Description"));
//                    }
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                }
//            }
//        });
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

        System.out.println("UPDATE GOOGLE MAPS");
        System.out.println("UPDATE GOOGLE MAPS");
        System.out.println("UPDATE GOOGLE MAPS");
        System.out.println("UPDATE GOOGLE MAPS");
        System.out.println("UPDATE GOOGLE MAPS");
        System.out.println("UPDATE GOOGLE MAPS");
        System.out.println("UPDATE GOOGLE MAPS");
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                CameraPosition cameraPosition = null;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else
                {
                    mGoogleMap.setMyLocationEnabled(true);

                    if (positions != null)
                    {
                        int number = 0;
                        for (LatLng position : positions)
                        {
                            mGoogleMap.addMarker(new MarkerOptions().position(position).title("Number " + number).snippet("Number " + number));
                            cameraPosition = new CameraPosition.Builder().target(position).zoom(5).build();
                            number++;
                        }
                    }
                    else
                    {

                    }
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }
}
